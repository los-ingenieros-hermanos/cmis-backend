package com.los.cmisbackend.controller;

import com.los.cmisbackend.dao.CommunityRepository;
import com.los.cmisbackend.dao.StudentRepository;
import com.los.cmisbackend.dao.TagRepository;
import com.los.cmisbackend.entity.Community;
import com.los.cmisbackend.entity.Student;
import com.los.cmisbackend.entity.Tag;
import com.los.cmisbackend.security.service.UserDetailsImpl;
import com.los.cmisbackend.util.MemberUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("api/cmis")
public class TagController {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CommunityRepository communityRepository;

    @Autowired
    MemberUtil memberUtil;

    @GetMapping("/tags")
    public ResponseEntity<List<Tag>> getAllTags() {
        List<Tag> tags = new ArrayList<Tag>();

        tagRepository.findAll().forEach(tags::add);

        if (tags.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    @GetMapping("/tags/{id}")
    public ResponseEntity<Tag> getTagsById(@PathVariable("id") Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Tag with id = " + id));

        return new ResponseEntity<>(tag, HttpStatus.OK);
    }

    @DeleteMapping("/tags/{id}")
    public ResponseEntity<HttpStatus> deleteTag(@PathVariable("id") Long id) {
        //tagRepository.deleteById(id);

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Tag with id = " + id));

        tagRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/students/{studentId}/interests")
    public ResponseEntity<Set<Tag>> getAllTagsByStudentId(@PathVariable(value = "studentId") Long studentId) {

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
        Set<Tag> tags = student.getInterests();
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    @PostMapping("/students/{studentId}/interests")
    public ResponseEntity<Tag> addInterest(@PathVariable(value = "studentId") Long studentId,
                                           @RequestBody Tag tagRequest) {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(studentId))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Tag tag = studentRepository.findById(studentId).map(student -> {
            Long tagId = tagRequest.getId();

            // tag is existed
            if (tagId != null) {
                Tag _tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new ResourceNotFoundException("Not found Tag with id = " + tagId));
                student.addInterest(_tag);
                studentRepository.save(student);
                return _tag;
            }
            throw new ResourceNotFoundException("Tag does not exists");
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        return new ResponseEntity<>(tag, HttpStatus.OK);
    }

    @DeleteMapping("/students/{studentId}/interests/{tagId}")
    public ResponseEntity<HttpStatus> deleteTagFromStudent(@PathVariable(value = "studentId") Long studentId,
                                                           @PathVariable(value = "tagId") Long tagId) {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(studentId))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        Tag tag = tagRepository.findTagById(tagId);
        student.removeTag(tag);
        studentRepository.save(student);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/communities/{communityId}/tags")
    public ResponseEntity<Set<Tag>> getAllTagsByCommunityId(@PathVariable(value = "communityId") Long communityId) {

        // add authentication check for community members
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | memberUtil.isAuthorized(communityId, userDetails.getId()) ))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if (!communityRepository.existsById(communityId)) {
            throw new ResourceNotFoundException("Not found Community with id = " + communityId);
        }

        Community community = communityRepository.findCommunityById(communityId);
        Set<Tag> tags = community.getTags();
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    @PostMapping("/communities/{communityId}/tags")
    public ResponseEntity<Tag> addTag(@PathVariable(value = "communityId") Long communityId,
                                           @RequestBody Tag tagRequest) {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | memberUtil.isAuthorized(communityId, userDetails.getId())))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Tag tag = communityRepository.findById(communityId).map(community -> {
            Long tagId = tagRequest.getId();

            // tag is existed
            if (tagId != null) {
                Tag _tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new ResourceNotFoundException("Not found Tag with id = " + tagId));
                community.addTag(_tag);
                communityRepository.save(community);
                return _tag;
            }
            throw new ResourceNotFoundException("Tag does not exists");
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        return new ResponseEntity<>(tag, HttpStatus.OK);
    }

    @DeleteMapping("/communities/{communityId}/tags/{tagId}")
    public ResponseEntity<HttpStatus> deleteTagFromCommunity(@PathVariable(value = "communityId") Long communityId,
                                                           @PathVariable(value = "tagId") Long tagId) {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | memberUtil.isAuthorized(communityId, userDetails.getId())))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        Tag tag = tagRepository.findTagById(tagId);
        community.removeTag(tag);
        communityRepository.save(community);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
