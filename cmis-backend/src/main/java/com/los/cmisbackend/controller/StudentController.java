package com.los.cmisbackend.controller;

import com.los.cmisbackend.dao.CommunityRepository;
import com.los.cmisbackend.dao.EventRepository;
import com.los.cmisbackend.dao.StudentRepository;
import com.los.cmisbackend.dao.UserRepository;
import com.los.cmisbackend.entity.*;
import com.los.cmisbackend.security.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.los.cmisbackend.util.Base64ImageEncoder;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:8070", maxAge = 3600)
@RestController
@RequestMapping("api/cmis")
public class StudentController {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommunityRepository communityRepository;

    @Autowired
    EventRepository eventRepository;

    private Base64ImageEncoder imageEncoder = new Base64ImageEncoder();

    @GetMapping({ "/students/{id}", "/users/{id}/students" })
    public ResponseEntity<Student> getStudentById(@PathVariable(value = "id") Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found student with id = " + id));

        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = new ArrayList<Student>();

        studentRepository.findAll().forEach(students::add);

        if (students.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @PostMapping("/users/{userId}/students")
    public ResponseEntity<Student> createStudent(@PathVariable(value = "userId") Long userId,
                                                         @RequestBody Student studentsRequest) {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found User with id = " + userId));

        studentsRequest.setUser(user);
        Student student = studentRepository.save(studentsRequest);

        return new ResponseEntity<>(student, HttpStatus.CREATED);
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable("id") Long id,
                                                         @RequestBody Student studentsRequest) {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(id))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id " + id + " not found"));

        if(studentsRequest.getImage() != null)
            student.setImage(studentsRequest.getImage());

        return new ResponseEntity<>(studentRepository.save(student), HttpStatus.OK);
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<HttpStatus> deleteStudent(@PathVariable("id") Long id) {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(id))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        //studentRepository.deleteById(id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + id));
        List<Community> communities = communityRepository.findCommunitiesByFollowersId(id);

        for(Community c: communities) {
            c.removeFollower(student);
            communityRepository.save(c);
        }
        studentRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // bug occurs if any of the students follow a community, fix bug
    @DeleteMapping("/students")
    public ResponseEntity<HttpStatus> deleteAllStudents() {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        studentRepository.deleteAll();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/communities/{communityId}/followers")
    public ResponseEntity<Set<Student>> getAllFollowersByCommunityId(@PathVariable(value = "communityId") Long communityId) {
        Community community = communityRepository.findById(communityId)
                    .orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        Set<Student> students = community.getFollowers();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @PostMapping("/communities/{communityId}/followers")
    public ResponseEntity<Student> addFollowerToCommunity(
            @PathVariable(value = "communityId") Long communityId, @RequestBody Student followerRequest) {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(followerRequest.getId()))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Student follower = communityRepository.findById(communityId).map(community -> {
            Long followerId = followerRequest.getId();

            // follower is existed
            if (followerId != null) {
                Student _follower = studentRepository.findById(followerId)
                        .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + followerId));
                community.addFollower(_follower);
                communityRepository.save(community);
                return _follower;
            }

            throw new ResourceNotFoundException("Student does not exists.");
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        return new ResponseEntity<>(follower, HttpStatus.CREATED);
    }

    @DeleteMapping("/communities/{communityId}/followers/{followerId}")
    public ResponseEntity<HttpStatus> deleteFollowerFromCommunity(@PathVariable(value = "communityId") Long communityId, @PathVariable(value = "followerId") Long followerId) {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(followerId))
                | (userDetails.getId().equals(communityId))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        Student follower = studentRepository.findById(followerId)
                        .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + followerId));
        community.removeFollower(follower);
        communityRepository.save(community);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/students/{id}/updateImage")
	public ResponseEntity<Student> updateImage(@PathVariable(value = "id") Long id,
							@RequestParam("image") MultipartFile image)
	{
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(id))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Id " + id + " not found"));

		student.setImage(imageEncoder.encodeImage(image));

        studentRepository.save(student);

        return new ResponseEntity<>(student, HttpStatus.OK);
	}

    @GetMapping("/students/{id}/image")
    public ResponseEntity<String> getStudentImage(@PathVariable(value = "id") Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found student with id = " + id));

        var image = student.getImage();
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    @GetMapping("/communities/{communityId}/members")
    public ResponseEntity<Set<Student>> getAllMembersByCommunityId(@PathVariable(value = "communityId") Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        Set<Student> members = community.getMembers();
        return new ResponseEntity<>(members, HttpStatus.OK);
    }
    
    @GetMapping("/communities/{communityId}/members/{memberId}")
    public ResponseEntity<Student> getMemberByCommunityId(@PathVariable(value = "communityId") Long communityId, @PathVariable(value = "memberId") Long memberId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        Student member = community.getMembers().stream()
                .filter(m -> m.getId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Not found Member with id = " + memberId));

        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    @PostMapping("/communities/{communityId}/members")
    public ResponseEntity<Student> addMemberToCommunity(
            @PathVariable(value = "communityId") Long communityId, @RequestBody Student memberRequest) {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(communityId))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Student member = communityRepository.findById(communityId).map(community -> {
            Long memberId = memberRequest.getId();

            // member is existed
            if (memberId != null) {
                Student _member = studentRepository.findById(memberId)
                        .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + memberId));
                community.addMember(_member);
                communityRepository.save(community);
                return _member;
        }
        
        throw new ResourceNotFoundException("Student does not exists.");
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        return new ResponseEntity<>(member, HttpStatus.CREATED);
    }

    @GetMapping("/events/{eventId}/attendants")
    public ResponseEntity<Set<Student>> getAllAttendantsByEventId(@PathVariable(value = "eventId") Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Event with id = " + eventId));

        Set<Student> attendants = event.getAttendants();
        return new ResponseEntity<>(attendants, HttpStatus.OK);
    }
}
