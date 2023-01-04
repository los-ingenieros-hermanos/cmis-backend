package com.los.cmisbackend.controller;

import com.los.cmisbackend.dao.ProjectIdeaRepository;
import com.los.cmisbackend.dao.StudentRepository;
import com.los.cmisbackend.entity.Post;
import com.los.cmisbackend.entity.ProjectIdea;
import com.los.cmisbackend.security.service.UserDetailsImpl;
import com.los.cmisbackend.util.CmisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "${cmis.app.baseUrl}", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("api/cmis")
public class ProjectIdeaController {

    @Autowired
    ProjectIdeaRepository projectIdeaRepository;

    @Autowired
    StudentRepository studentRepository;

    // get all project ideas
    @GetMapping("/projectidea")
    public ResponseEntity<List<ProjectIdea>> getAllProjectIdeas() {
        List<ProjectIdea> projectIdeas = projectIdeaRepository.findAll();
        return ResponseEntity.ok(projectIdeas);
    }

    // get a project idea by id
    @GetMapping("/projectidea/{id}")
    public ResponseEntity<ProjectIdea> getProjectIdeaById(@PathVariable(value = "id") Long id) {
        ProjectIdea projectIdea = projectIdeaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found project idea with id = " + id));
        return ResponseEntity.ok(projectIdea);
    }

    // get a students project ideas
    @GetMapping("/students/{studentId}/projectidea")
    public ResponseEntity<List<ProjectIdea>> getAllProjectIdeasByStudentId(@PathVariable(value = "studentId") Long studentId,
                                                               @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                               @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found student with id = " + studentId));

        List<ProjectIdea> projectIdeas = projectIdeaRepository.findByStudentId(studentId);
        return ResponseEntity.ok(projectIdeas);
    }

    // create a project idea
    @PostMapping("/students/{studentId}/projectidea")
    public ResponseEntity<ProjectIdea> createProjectIdea(@PathVariable(value = "studentId") Long studentId,
                                                         @RequestBody ProjectIdea projectIdeaRequest) {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(studentId))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found student with id = " + studentId));

        ProjectIdea projectIdea = studentRepository.findById(studentId).map(student -> {
            projectIdeaRequest.setStudent(student);
            return projectIdeaRepository.save(projectIdeaRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found student with id = " + studentId));

        return new ResponseEntity<>(projectIdea, HttpStatus.CREATED);
    }

    // update a project idea
    @PutMapping("/students/{studentId}/projectidea/{id}")
    public ResponseEntity<ProjectIdea> updateProjectIdea(@PathVariable(value = "studentId") Long studentId,
                                                         @PathVariable(value = "id") Long id,
                                                         @RequestBody ProjectIdea projectIdeaRequest) {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(studentId))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found student with id = " + studentId));

        projectIdeaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found project idea with id = " + id));

        ProjectIdea projectIdea = projectIdeaRepository.findProjectIdeaById(id);
        if (projectIdeaRequest.getTitle() != null)
            projectIdea.setTitle(projectIdeaRequest.getTitle());
        if (projectIdeaRequest.getText() != null)
            projectIdea.setText(projectIdeaRequest.getText());
        if (projectIdeaRequest.getImage() != null)
            projectIdea.setImage(projectIdeaRequest.getImage());
        ProjectIdea updatedProjectIdea = projectIdeaRepository.save(projectIdea);
        return new ResponseEntity<>(updatedProjectIdea, HttpStatus.OK);
    }

}
