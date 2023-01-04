package com.los.cmisbackend.controller;

import com.los.cmisbackend.dao.*;
import com.los.cmisbackend.entity.*;
import com.los.cmisbackend.security.service.UserDetailsImpl;
import com.los.cmisbackend.util.CmisConstants;
import com.los.cmisbackend.util.MemberUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@CrossOrigin(origins = "${cmis.app.baseUrl}", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("api/cmis")
public class PostController {

    @Autowired
    PostRepository postRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CommunityRepository communityRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    DateRepository dateRepository;

    @Autowired
    MemberUtil memberUtil;

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts(
        @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
        @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size) 
        {

        Pageable pageable = PageRequest.of(page, size);

		Page<Post> posts = postRepository.findAll(pageable);

        List<Post> _posts = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

       
        return new ResponseEntity<>(_posts, HttpStatus.OK);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable("id") Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Post with id = " + id));

        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<HttpStatus> deletePost(@PathVariable("id") Long id) {
        //postRepository.deleteById(id);

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Post with id = " + id));
        List<Student> students = studentRepository.findStudentsByBookmarkedPostsId(id);

        for(Student s: students) {
            s.removePost(post);
            studentRepository.save(s);
        }
        postRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/students/{studentId}/bookmarkedPosts")
    public ResponseEntity<List<Post>> getAllPostsByStudentId(@PathVariable(value = "studentId") Long studentId,
    @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
    @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size) {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(studentId))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        Pageable pageable = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findByStudents(student, pageable);

        List<Post> _posts = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new ResponseEntity<>(_posts, HttpStatus.OK);
    }

    @PostMapping("/students/{studentId}/bookmarkedPosts")
    public ResponseEntity<Post> addBookmarkedPost(@PathVariable(value = "studentId") Long studentId, @RequestBody Post postRequest) {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(studentId))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Post post = studentRepository.findById(studentId).map(student -> {
            Long postId = postRequest.getId();

            // post is existed
            if (postId != null) {
                Post _post = postRepository.findById(postId)
                        .orElseThrow(() -> new ResourceNotFoundException("Not found Post with id = " + postId));
                student.addPost(_post);
                studentRepository.save(student);
                return _post;
            }
            throw new ResourceNotFoundException("Post does not exists");
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @DeleteMapping("/students/{studentId}/bookmarkedPosts/{postId}")
    public ResponseEntity<HttpStatus> deletePostFromStudent(@PathVariable(value = "studentId") Long studentId, @PathVariable(value = "postId") Long postId) {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(studentId))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        Post post = postRepository.findPostById(postId);
        student.removePost(post);
        studentRepository.save(student);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/communities/{communityId}/posts")
    public ResponseEntity<List<Post>> getAllPostsByCommunityId(@PathVariable(value = "communityId") Long communityId,
    @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
    @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size) 
    {
        // add authentication check for community members
        // check authentication
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
        //         | memberUtil.isAuthorized(communityId, userDetails.getId())))
        //     return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        Pageable pageable = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findByCommunityId(communityId, pageable);

        List<Post> _posts = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new ResponseEntity<>(_posts, HttpStatus.OK);
    }

    @PostMapping("/communities/{communityId}/posts")
    public ResponseEntity<Post> createPost(@PathVariable(value = "communityId") Long communityId,
                                                 @RequestBody Post postRequest) {
        System.out.println(postRequest);
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | memberUtil.isAuthorized(communityId, userDetails.getId())))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Post post = communityRepository.findById(communityId).map(community -> {
            postRequest.setCommunity(community);
            if (postRequest.getEvent() != null && !postRequest.getEvent().isEmpty()) {
                List<Event> events = postRequest.getEvent();
                Event event = events.get(0);

                // if date is null throw exception
                if (event.getDate() == null) {
                    throw new ResourceNotFoundException("Date is null");
                }

                Date date = new Date();
                date.setDay(event.getDate().getDay());
                date.setMonth(event.getDate().getMonth());
                date.setYear(event.getDate().getYear());
                date = dateRepository.save(date);
                event.setDate(date);

                System.out.println(event);
                System.out.println(date);
                events.set(0, eventRepository.save(event));
                postRequest.setEvent(events);
                System.out.println(postRequest);
            }
            System.out.println(postRequest);
            return postRepository.save(postRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }
    @DeleteMapping("/communities/{communityId}/posts")
    public ResponseEntity<List<Post>> deleteAllPostsOfCommunity(@PathVariable(value = "communityId") Long communityId) {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | memberUtil.isAuthorized(communityId, userDetails.getId())))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if (!communityRepository.existsById(communityId)) {
            throw new ResourceNotFoundException("Not found Community with id = " + communityId);
        }

        postRepository.deleteByCommunityId(communityId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // get all posts by title search
    @GetMapping("/posts/searchTitle")
    public ResponseEntity<List<Post>> getAllPostsByTitle(@RequestParam(value = "title") String title,
    @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
    @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findPostsByTitleContaining(title, pageable);

        List<Post> _posts = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new ResponseEntity<>(_posts, HttpStatus.OK);
    }

}
