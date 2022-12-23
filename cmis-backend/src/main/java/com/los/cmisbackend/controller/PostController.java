package com.los.cmisbackend.controller;

import com.los.cmisbackend.dao.*;
import com.los.cmisbackend.entity.*;
import com.los.cmisbackend.security.service.UserDetailsImpl;
import com.los.cmisbackend.util.Base64ImageEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
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

    private Base64ImageEncoder imageEncoder = new Base64ImageEncoder();


    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = new ArrayList<Post>();

        System.out.println("Get all posts");
        postRepository.findAll().forEach(posts::add);

        if (posts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(posts, HttpStatus.OK);
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
    public ResponseEntity<Set<Post>> getAllPostsByStudentId(@PathVariable(value = "studentId") Long studentId) {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(studentId))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }

        Student student = studentRepository.findStudentById(studentId);
        Set<Post> posts = student.getBookmarkedPosts();
        return new ResponseEntity<>(posts, HttpStatus.OK);
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
    public ResponseEntity<List<Post>> getAllPostsByCommunityId(@PathVariable(value = "communityId") Long communityId) {

        // add authentication check for community members
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(communityId))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if (!communityRepository.existsById(communityId)) {
            throw new ResourceNotFoundException("Not found Community with id = " + communityId);
        }

        List<Post> posts = postRepository.findByCommunityId(communityId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @PostMapping("/communities/{communityId}/posts")
    public ResponseEntity<Post> createPost(@PathVariable(value = "communityId") Long communityId,
                                                 @RequestBody Post postRequest/*,
                                                 final @RequestParam("image") MultipartFile image */) {
        System.out.println(postRequest);
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(communityId))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Post post = communityRepository.findById(communityId).map(community -> {
            /*postRequest.setImage(imageEncoder.encodeImage(image));
            */
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
                | (userDetails.getId().equals(communityId))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if (!communityRepository.existsById(communityId)) {
            throw new ResourceNotFoundException("Not found Community with id = " + communityId);
        }

        postRepository.deleteByCommunityId(communityId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
