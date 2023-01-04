package com.los.cmisbackend.dao;

import com.los.cmisbackend.entity.ProjectIdea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectIdeaRepository extends JpaRepository<ProjectIdea, Long> {
    List<ProjectIdea> findByStudentId(Long studentId);
    ProjectIdea findProjectIdeaById(Long id);
}
