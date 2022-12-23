package com.los.cmisbackend.dao;

import com.los.cmisbackend.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findStudentById(Long id);
    List<Student> findStudentsByBookmarkedPostsId(Long id);
}
