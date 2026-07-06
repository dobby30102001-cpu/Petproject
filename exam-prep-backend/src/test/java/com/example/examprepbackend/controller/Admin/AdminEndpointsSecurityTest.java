package com.example.examprepbackend.controller.Admin;

import com.example.examprepbackend.config.JwtFilter;
import com.example.examprepbackend.config.SecurityConfig;
import com.example.examprepbackend.exception.GlobalException;
import com.example.examprepbackend.repository.UsersRepository;
import com.example.examprepbackend.service.AuthenticationService;
import com.example.examprepbackend.service.ClassService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.mail.autoconfigure.MailSenderAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Drives the real Spring Security filter chain through MockMvc to assert the
 * URL rules in SecurityConfig. Only the two admin controllers are booted, and
 * JPA / DataSource / Mail auto-config is switched off so the test doesn't need
 * a database or SMTP credentials.
 *
 * JwtFilter is replaced by a pass-through no-op so authentication comes purely
 * from Spring Security Test's @WithMockUser / @WithAnonymousUser.
 */
@SpringBootTest(classes = AdminEndpointsSecurityTest.TestApp.class)
class AdminEndpointsSecurityTest {

    @Autowired
    private WebApplicationContext ctx;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private ClassService classService;

    @MockitoBean
    private UsersRepository usersRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithAnonymousUser
    void lockAccount_withoutAnyToken_returns401() throws Exception {
        mockMvc.perform(put("/api/v1/admin/security/account/lock/{id}", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void lockAccount_withNonAdminToken_returns403() throws Exception {
        mockMvc.perform(put("/api/v1/admin/security/account/lock/{id}", 1))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void lockAccount_withTeacherToken_returns403() throws Exception {
        mockMvc.perform(put("/api/v1/admin/security/account/lock/{id}", 1))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void updateClass_withStudentToken_returns403() throws Exception {
        mockMvc.perform(put("/api/v1/admin/classes/{id}", 42)
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void addTeachersToClass_withStudentToken_returns403() throws Exception {
        mockMvc.perform(put("/api/v1/admin/classes/{id}/teachers", 42)
                        .contentType("application/json")
                        .content("[]"))
                .andExpect(status().isForbidden());
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            DataJpaRepositoriesAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            MailSenderAutoConfiguration.class
    })
    @Import({
            SecurityAutoConfiguration.class,
            SecurityFilterAutoConfiguration.class,
            SecurityConfig.class,
            GlobalException.class,
            AdminSecurityController.class,
            AdminClassController.class
    })
    static class TestApp {
        // Replace the real JwtFilter with a pass-through so tests don't need a JWT.
        @Bean
        @Primary
        JwtFilter jwtFilter() {
            return new JwtFilter(null, null) {
                @Override
                protected void doFilterInternal(HttpServletRequest request,
                                                HttpServletResponse response,
                                                FilterChain filterChain)
                        throws ServletException, IOException {
                    filterChain.doFilter(request, response);
                }
            };
        }
    }
}
