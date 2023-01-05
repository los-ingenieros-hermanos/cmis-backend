package com.los.cmisbackend.dao;

import com.los.cmisbackend.entity.ProjectIdea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectIdeaRepository extends JpaRepository<ProjectIdea, Long> {
    List<ProjectIdea> findByStudentId(Long studentId);
    ProjectIdea findProjectIdeaById(Long id);
    Page<ProjectIdea> findProjectIdeaByTitleContainingOrTextContaining(String title, String text, Pageable pageable);
}
