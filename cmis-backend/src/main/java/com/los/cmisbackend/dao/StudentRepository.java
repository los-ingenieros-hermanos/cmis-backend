package com.los.cmisbackend.dao;

import com.los.cmisbackend.entity.Post;
import com.los.cmisbackend.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
