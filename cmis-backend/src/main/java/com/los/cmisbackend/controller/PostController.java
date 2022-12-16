package com.los.cmisbackend.controller;

import com.los.cmisbackend.dao.CommunityRepository;
import com.los.cmisbackend.dao.PostRepository;
import com.los.cmisbackend.dao.StudentRepository;
import com.los.cmisbackend.entity.Community;
import com.los.cmisbackend.entity.Post;
import com.los.cmisbackend.entity.Student;
import com.los.cmisbackend.util.Base64ImageEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private Base64ImageEncoder imageEncoder = new Base64ImageEncoder();


    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = new ArrayList<Post>();

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
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Not found Student with id = " + studentId);
        }

        Student student = studentRepository.findStudentById(studentId);
        Set<Post> posts = student.getBookMarkedPosts();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @PostMapping("/students/{studentId}/bookmarkedPosts")
    public ResponseEntity<Post> addBookmarkedPost(@PathVariable(value = "studentId") Long studentId, @RequestBody Post postRequest) {
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
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        Post post = postRepository.findPostById(postId);
        student.removePost(post);
        studentRepository.save(student);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/communities/{communityId}/posts")
    public ResponseEntity<List<Post>> getAllPostsByCommunityId(@PathVariable(value = "communityId") Long communityId) {
        if (!communityRepository.existsById(communityId)) {
            throw new ResourceNotFoundException("Not found Community with id = " + communityId);
        }

        List<Post> posts = postRepository.findByCommunityId(communityId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @PostMapping("/communities/{communityId}/posts")
    public ResponseEntity<Post> createPost(@PathVariable(value = "communityId") Long communityId,
                                                 @RequestBody Post postRequest,
                                                 final @RequestParam("image") MultipartFile image) {
        Post post = communityRepository.findById(communityId).map(community -> {
            postRequest.setImage(imageEncoder.encodeImage(image));
            postRequest.setCommunity(community);
            return postRepository.save(postRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }
    @DeleteMapping("/communities/{communityId}/posts")
    public ResponseEntity<List<Post>> deleteAllPostsOfCommunity(@PathVariable(value = "communityId") Long communityId) {
        if (!communityRepository.existsById(communityId)) {
            throw new ResourceNotFoundException("Not found Community with id = " + communityId);
        }

        postRepository.deleteByCommunityId(communityId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
