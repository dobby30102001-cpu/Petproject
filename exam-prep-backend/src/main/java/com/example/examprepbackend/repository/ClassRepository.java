package com.example.examprepbackend.repository;

import com.example.examprepbackend.entity.Classes;
import org.modelmapper.internal.bytebuddy.implementation.Implementation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ClassRepository extends JpaRepository<Classes, Integer>, JpaSpecificationExecutor<Classes> {

    Classes findByName(String name);

    Page<Classes> findByIdIn(List<Integer> idList, Pageable pageable);


}
