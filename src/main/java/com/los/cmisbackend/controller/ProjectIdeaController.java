package com.los.cmisbackend.controller;

import com.los.cmisbackend.dao.ProjectIdeaRepository;
import com.los.cmisbackend.dao.StudentRepository;
import com.los.cmisbackend.entity.Post;
import com.los.cmisbackend.entity.ProjectIdea;
import com.los.cmisbackend.entity.Student;
import com.los.cmisbackend.security.service.UserDetailsImpl;
import com.los.cmisbackend.util.CmisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @DeleteMapping("/projectidea/{id}")
    public ResponseEntity<?> deleteProjectIdea(@PathVariable(value = "id") Long id) {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ProjectIdea projectIdea = projectIdeaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found project idea with id = " + id));
        Long studentId = projectIdea.getStudent().getId();
        if (!(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(studentId)))
        )
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        projectIdeaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found project idea with id = " + id));

        projectIdeaRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // add project idea search
    @GetMapping("/projectidea/search")
    public ResponseEntity<List<ProjectIdea>> searchProjectIdeas(@RequestParam(value = "search", required = false) String search,
                                                                @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                                @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProjectIdea> projectIdeas = projectIdeaRepository.findProjectIdeaByTitleContainingOrTextContaining(search, search, pageable);
        return ResponseEntity.ok(projectIdeas.getContent());
    }

    // student likes a project idea
    @PostMapping("/students/{studentId}/projectidea/{projectIdeaId}/like")
    public ResponseEntity<ProjectIdea> likeProjectIdea(@PathVariable(value = "studentId") Long studentId,
                                         @PathVariable(value = "projectIdeaId") Long projectIdeaId) {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | userDetails.getId().equals(studentId)))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);


        ProjectIdea projectIdea = projectIdeaRepository.findById(projectIdeaId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found project idea with id = " + projectIdeaId));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found student with id = " + studentId));

        if (projectIdea.getLikes().contains(student)) {
            student.getLikedProjectIdeas().remove(projectIdea);
            projectIdea.getLikes().remove(student);
            projectIdea.setLikeNum(projectIdea.getLikeNum().intValue() - 1);
        } else {
            student.getLikedProjectIdeas().add(projectIdea);
            projectIdea.getLikes().add(student);
            projectIdea.setLikeNum(projectIdea.getLikeNum().intValue() + 1);
        }

        studentRepository.save(student);
        ProjectIdea updatedProjectIdea = projectIdeaRepository.save(projectIdea);
        return new ResponseEntity<>(updatedProjectIdea, HttpStatus.OK);
    }

    // students bookmarks a project idea
    @PostMapping("/students/{studentId}/bookMarkedProjectIdeas")
    public ResponseEntity<ProjectIdea> addBookmarkedProjectIdea(@PathVariable(value = "studentId") Long studentId,
                                                  @RequestBody ProjectIdea projectIdeaRequest) {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(studentId))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        ProjectIdea projectIdea = studentRepository.findById(studentId).map(student -> {
            Long postId = projectIdeaRequest.getId();

            // post is existed
            if (postId != null) {
                ProjectIdea _projectIdea = projectIdeaRepository.findById(postId)
                        .orElseThrow(() -> new ResourceNotFoundException("Not found Project Idea with id = " + postId));
                Set<ProjectIdea> bookMarkedProjectIdeas = student.getBookmarkedProjectIdeas();
                if (!bookMarkedProjectIdeas.contains(_projectIdea)) {
                    bookMarkedProjectIdeas.add(_projectIdea);
                    student.setBookmarkedProjectIdeas(bookMarkedProjectIdeas);
                }
                studentRepository.save(student);
                return _projectIdea;
            }
            throw new ResourceNotFoundException("Post does not exists");
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        return new ResponseEntity<>(projectIdea, HttpStatus.OK);
    }

    // get all bookmarked project ideas of a student
    @GetMapping("/students/{studentId}/bookMarkedProjectIdeas")
    public ResponseEntity<List<ProjectIdea>> getBookmarkedProjectIdeas(@PathVariable(value = "studentId") Long studentId,
                                                                      @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                                      @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size) {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(studentId))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Pageable pageable = PageRequest.of(page, size);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found student with id = " + studentId));

        Page<ProjectIdea> projectIdeas = projectIdeaRepository.findAllByBookMarkedBy(student, pageable);

        List<ProjectIdea> _projectIdeas = projectIdeas.getNumberOfElements() == 0 ? Collections.emptyList() : projectIdeas.getContent();

        return new ResponseEntity<>(_projectIdeas, HttpStatus.OK);
    }

    // delete a bookmarked project idea of a student
    @DeleteMapping("/students/{studentId}/bookmarkedProjectIdeas/{projectIdeaId}")
    public ResponseEntity<HttpStatus> deletePostFromStudent(@PathVariable(value = "studentId") Long studentId,
                                                            @PathVariable(value = "projectIdeaId") Long projectIdeaId) {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(studentId))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        ProjectIdea projectIdea = projectIdeaRepository.findById(projectIdeaId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Project Idea with id = " + projectIdeaId));
        Set<ProjectIdea> bookMarkedProjectIdeas = student.getBookmarkedProjectIdeas();
        if (bookMarkedProjectIdeas.contains(projectIdea)) {
            bookMarkedProjectIdeas.remove(projectIdea);
            student.setBookmarkedProjectIdeas(bookMarkedProjectIdeas);
            studentRepository.save(student);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // get all project ideas sorted by like number
    @GetMapping("/projectIdeas/sortedByLikeNum")
    public ResponseEntity<List<ProjectIdea>> getProjectIdeasSortedByLikeNum(@RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                                               @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size) {
            Pageable pageable = PageRequest.of(page, size);
            Page<ProjectIdea> projectIdeas = projectIdeaRepository.findAllByOrderByLikeNumDesc(pageable);

            List<ProjectIdea> _projectIdeas = projectIdeas.getNumberOfElements() == 0 ? Collections.emptyList() : projectIdeas.getContent();

            return new ResponseEntity<>(_projectIdeas, HttpStatus.OK);
    }

    // is project idea is liked by a student
    @GetMapping("/students/{studentId}/projectIdeas/{projectIdeaId}/isLiked")
    public ResponseEntity<Boolean> isLiked(@PathVariable(value = "studentId") Long studentId,
                                           @PathVariable(value = "projectIdeaId") Long projectIdeaId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));
        ProjectIdea projectIdea = projectIdeaRepository.findById(projectIdeaId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Project Idea with id = " + projectIdeaId));

        return new ResponseEntity<>(student.getLikedProjectIdeas().contains(projectIdea), HttpStatus.OK);
    }

    // is project idea is bookmarked by a student
    @GetMapping("/students/{studentId}/projectIdeas/{projectIdeaId}/isBookmarked")
    public ResponseEntity<Boolean> isBookmarked(@PathVariable(value = "studentId") Long studentId,
                                                @PathVariable(value = "projectIdeaId") Long projectIdeaId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));
        ProjectIdea projectIdea = projectIdeaRepository.findById(projectIdeaId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Project Idea with id = " + projectIdeaId));

        return new ResponseEntity<>(student.getBookmarkedProjectIdeas().contains(projectIdea), HttpStatus.OK);
    }

    // get all students which have project idea
    @GetMapping("/students/withProjectIdea")
    public ResponseEntity<List<Student>> getStudentsWithProjectIdea(@RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                                   @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        List<ProjectIdea> projectIdeas = projectIdeaRepository.findAll();

        Set<Student> students = new HashSet<>();

        for (ProjectIdea projectIdea : projectIdeas) {
            students.add(projectIdea.getStudent());
        }

        List<Student> studentList = new ArrayList<>(students);
        Page<Student> _students = new PageImpl<>(studentList, pageable, students.size());
        List<Student> __students = _students.getNumberOfElements() == 0 ? Collections.emptyList() : _students.getContent();

        return new ResponseEntity<>(__students, HttpStatus.OK);
    }
}
