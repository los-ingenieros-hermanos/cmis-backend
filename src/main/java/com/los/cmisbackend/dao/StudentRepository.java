package com.los.cmisbackend.dao;

import com.los.cmisbackend.entity.Community;
import com.los.cmisbackend.entity.Student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findStudentById(Long id);
    List<Student> findStudentsByBookmarkedPostsId(Long id);
	Page<Student> findByFollowingCommunitiesContaining(Community community, Pageable pageable);
    Page<Student> findAllByFirstNameContainingOrLastNameContaining(String firstName, String lastName, Pageable pageable);
}
