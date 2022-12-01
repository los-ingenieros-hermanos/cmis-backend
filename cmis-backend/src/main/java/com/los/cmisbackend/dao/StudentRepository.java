package com.los.cmisbackend.dao;

import com.los.cmisbackend.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(path="students")
public interface StudentRepository extends JpaRepository<Student, Long> {

}
