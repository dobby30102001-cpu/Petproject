package com.example.examprepbackend.service.impl;

import com.example.examprepbackend.config.JwtUtils;
import com.example.examprepbackend.constant.Status;
import com.example.examprepbackend.dto.authentication.ForgotPassword;
import com.example.examprepbackend.dto.authentication.LoginRequest;
import com.example.examprepbackend.dto.authentication.LoginResponse;
import com.example.examprepbackend.dto.authentication.ResetPassword;
import com.example.examprepbackend.entity.Otp;
import com.example.examprepbackend.entity.Users;
import com.example.examprepbackend.exception.ApplicationException;
import com.example.examprepbackend.repository.OtpRepository;
import com.example.examprepbackend.repository.UsersRepository;
import com.example.examprepbackend.service.AuthenticationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Random;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthentiationServiceImpl implements AuthenticationService {


    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository userRepository;
    private final OtpRepository otpRepository;
    private final MailService mailService;
    private final ModelMapper modelMapper;

    //fomat giờ vn
    ZoneId vnZone = ZoneId.of("Asia/Ho_Chi_Minh");
    LocalDateTime vnTime = LocalDateTime.now(vnZone);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");


    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Optional<Users> users = userRepository
                .findByEmailOrUsername(
                        loginRequest.getEmailOrUsername(),
                        loginRequest.getEmailOrUsername()
                );
        if (users.isEmpty()) {
            throw new ApplicationException("Sai email hoặc username ");
        }

        Users user = users.get();
        // Kiểm tra tài khoản có bị khóa không
        if (user.getStatus() == Status.LOCKED && user.getLockTime() != null)
            if (user.getLockTime().plusMinutes(15).isBefore(LocalDateTime.now())) {
                user.setStatus(Status.ACTIVED);
                user.setFailCount(0);
                user.setLockTime(null);

                userRepository.save(user);
            } else {
                throw new ApplicationException("Tài khoản bị khóa. Thử lại sau 15 phút");
            }
        //  Sai pass
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {

            int failCount = user.getFailCount() + 1;
            user.setFailCount(failCount);

            // Sai quá 5 lần sẽ khóa
            if (failCount >= 5) {
                user.setStatus(Status.LOCKED);
                user.setLockTime(LocalDateTime.now());
                //gửi mail
                mailService.sendEmail(
                        user.getEmail(),
                        "Account locked",
                        "Tài khoản của bạn đã bị khóa 15 phút"
                );
            }
            userRepository.save(user);

            throw new ApplicationException(
                    "Sai mật khẩu. Bạn còn " + (5 - failCount) + "/5 lần thử"
            );
        }

        ZoneId vnZone = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDateTime vnTime = LocalDateTime.now(vnZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" HH:mm:ss dd/MM/yyyy");

        user.setFailCount(0);
        userRepository.save(user);

        mailService.sendEmail(
                user.getEmail(),
                "Account Login",
                "Tài khoản của bạn vừa login lúc " + vnTime.format(formatter)
        );

        String token = jwtUtils.generateToken(user.getUsername());

        return new LoginResponse(
                token,
                user.getUsername(),
                user.getRole().name(),
                user.getFailCount()
        );
    }

    @Transactional
    @Override
    public String sendOtp(ForgotPassword forgetpw) {
        Optional<Users> user = userRepository.findByEmail(forgetpw.getEmail());
        if (user.isEmpty()) {
            throw new ApplicationException("Account not found");
        }
        //kiểm tra OTP gần nhất xem đã tạo bao giờ
        Optional<Otp> lastOtp = otpRepository
                .findTopByEmailOrderByCreatedAtDesc(forgetpw.getEmail());
        if (lastOtp.isPresent()) {
            LocalDateTime lastTime = lastOtp.get().getCreatedAt();
            // thời gian tạo otp chưa đến 30s mà user gửi thêm thì quăng ra exception
            if (lastTime.plusSeconds(30).isAfter(LocalDateTime.now())) {
                throw new ApplicationException("Vui lòng chờ 30s trước khi gửi lại OTP");
            }
        }
        otpRepository.deleteByEmail(forgetpw.getEmail());

        // Tạo OTP 6 chữ số
        int otp = new Random().nextInt(900000) + 100000;

        Otp newOtp = new Otp();
        newOtp.setEmail(forgetpw.getEmail());
        newOtp.setOtp(otp);
        newOtp.setExpireAt(LocalDateTime.now().plusSeconds(30));//30s bị xóa
        newOtp.setCreatedAt(LocalDateTime.now());
        otpRepository.save(newOtp);
        log.info("Send OTP for user {} : {}", forgetpw.getEmail(), otp);
        mailService.sendEmail(
                user.get().getEmail(),
                "Mã OTP xác thực lấy lại mật khẩu",
                "Xin chào " + user.get().getUsername() + ",\n\n"
                        + "Mã OTP xác thực của bạn là:\n\n"
                        + otp + "\n\n"
                        + "Mã OTP này có hiệu lực trong vòng 1 phút.\n\n"
                        + "Vui lòng không chia sẻ mã này cho bất kỳ ai để đảm bảo an toàn cho tài khoản.\n\n"
                        + "Trân trọng"
        );
        return "OTP đã được gửi qua email :" + forgetpw.getEmail();
    }

    @Override
    public Boolean resetPassword(ResetPassword resetpw) {

        Optional<Users> users = userRepository.findByEmail(resetpw.getEmail());
        if (users.isEmpty()) {
            throw new ApplicationException("Account not found");
        }

        Otp otp = otpRepository
                .findByEmailAndOtp(resetpw.getEmail(), resetpw.getOtp());

        if (otp == null) {
            throw new ApplicationException("OTP không đúng");
        }

        if (otp.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new ApplicationException("OTP đã hết hạn");
        }

        if (!resetpw.getNewPassword().equals(resetpw.getConfirmNewPassword())) {
            throw new ApplicationException("Confirm password không khớp");
        }

        Users user = users.get();
        user.setPassword(passwordEncoder.encode(resetpw.getNewPassword()));
        userRepository.save(users.get());
        otpRepository.delete(otp);
        ZoneId vnZone = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDateTime vnTime = LocalDateTime.now(vnZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" HH:mm:ss dd/MM/yyyy");
        mailService.sendEmail(
                user.getEmail(),
                "Password changed",
                "Mật khẩu của bạn vừa được thay đổi lúc " + vnTime.format(formatter)
        );

        return true;
    }

    @Override
    public String unlockAccount(Integer userId) {

        Optional<Users> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ApplicationException("Account not found ID");
        }
        Users users = user.get();
        users.setStatus(Status.ACTIVED);
        users.setFailCount(0);
        users.setLockTime(null);

        userRepository.save(users);

        mailService.sendEmail(
                users.getEmail(),
                "Tài khoản của bạn đã được mở khóa",
                "Xin chào " + users.getUsername() + ",\n\n"
                        + "Tài khoản của bạn đã được quản trị viên mở khóa thành công.\n"
                        + "Bạn hiện có thể đăng nhập và tiếp tục sử dụng hệ thống như bình thường.\n\n"
                        + "Nếu bạn gặp bất kỳ vấn đề nào khi đăng nhập, vui lòng liên hệ admin.\n\n"
                        + "Trân trọng"
        );
        return users.getEmail();
    }

    @Override
    public String lockAccount(Integer userId) {
        Optional<Users> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ApplicationException("Account not found ID");
        }
        Users users = user.get();
        users.setStatus(Status.LOCKED);
        users.setFailCount(5);
        users.setLockTime(LocalDateTime.now());

        userRepository.save(users);
        mailService.sendEmail(
                users.getEmail(),
                "Tài khoản của bạn đã bị khóa",
                "Xin chào " + users.getUsername() + ",\n\n"
                        + "Tài khoản của bạn đã bị admin khóa.\n"
                        + "Vui lòng liên hệ quản trị viên để được hỗ trợ mở khóa."
        );

        return users.getEmail();
    }
}
