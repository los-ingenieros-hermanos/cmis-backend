package com.los.cmisbackend.controller;

import com.los.cmisbackend.dao.CommunityRepository;
import com.los.cmisbackend.dao.EventRepository;
import com.los.cmisbackend.dao.MemberRepository;
import com.los.cmisbackend.dao.StudentRepository;
import com.los.cmisbackend.dao.UserRepository;
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
import java.util.Set;

@CrossOrigin(origins = "${cmis.app.baseUrl}", maxAge = 3600, allowCredentials = "true")
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

    @Autowired
    MemberUtil memberUtil;

    @Autowired
    MemberRepository memberRepository;

    @GetMapping({ "/students/{id}", "/users/{id}/students" })
    public ResponseEntity<Student> getStudentById(@PathVariable(value = "id") Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found student with id = " + id));

        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents(
        @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
        @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size) 
        {

        Pageable pageable = PageRequest.of(page, size);

		Page<Student> studentPage = studentRepository.findAll(pageable);

        List<Student> students = studentPage.getNumberOfElements() == 0 ? Collections.emptyList() : studentPage.getContent();

       
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

        if(studentsRequest.getBanner() != null)
            student.setBanner(studentsRequest.getBanner());

        if(studentsRequest.getFirstName() != null) {
            User user = student.getUser();
            user.setFirstName(studentsRequest.getFirstName());
            student.setUser(user);
            userRepository.save(user);
        }

        if(studentsRequest.getLastName() != null) {
            User user = student.getUser();
            user.setLastName(studentsRequest.getLastName());
            student.setUser(user);
            userRepository.save(user);
        }

        if(studentsRequest.getInfo() != null)
            student.setInfo(studentsRequest.getInfo());

        if(studentsRequest.getGithub() != null)
            student.setGithub(studentsRequest.getGithub());

        if(studentsRequest.getLinkedin() != null)
            student.setLinkedin(studentsRequest.getLinkedin());

        if(studentsRequest.getTwitter() != null)
            student.setTwitter(studentsRequest.getTwitter());

        if(studentsRequest.getInstagram() != null)
            student.setInstagram(studentsRequest.getInstagram());

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
    public ResponseEntity<Set<Student>> getAllFollowersByCommunityId(@PathVariable(value = "communityId") Long communityId,
        @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
        @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size) 
    {

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found community with id = " + communityId));

        Pageable pageable = PageRequest.of(page, size);
        Page<Student> studentPage = studentRepository.findByFollowingCommunitiesContaining(community, pageable);

        Set<Student> students = studentPage.getNumberOfElements() == 0 ? Collections.emptySet() : studentPage.toSet();
        
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

//    @PostMapping("/communities/{communityId}/followers")
//    public ResponseEntity<Student> addFollowerToCommunity(
//            @PathVariable(value = "communityId") Long communityId, @RequestBody Student followerRequest) {
//
//        // check authentication
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
//                | (userDetails.getId().equals(followerRequest.getId()))))
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//
//
//        Community community = communityRepository.findById(communityId)
//                .orElseThrow(() -> new ResourceNotFoundException("Not found community with id = " + communityId));
//
//        Student follower = studentRepository.findById(followerRequest.getId())
//                .orElseThrow(() -> new ResourceNotFoundException("Not found student with id = " + followerRequest.getId()));
//
//        Set<Student> followers = community.getFollowers();
//
//        if(followers.contains(follower))
//            return new ResponseEntity<>(HttpStatus.OK);
//
//        Integer followersCount = community.getFollowerCount();
//        followers.add(follower);
//        community.setFollowers(followers);
//        community.setFollowerCount(followersCount + 1);
//        communityRepository.save(community);
//
////        Student follower = communityRepository.findById(communityId).map(community -> {
////            Long followerId = followerRequest.getId();
////
////            // follower is existed
////            if (followerId != null) {
////                Student _follower = studentRepository.findById(followerId)
////                        .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + followerId));
////                // check if follower is already f
////                community.addFollower(_follower);
////                communityRepository.save(community);
////                return _follower;
////            }
////
////            throw new ResourceNotFoundException("Student does not exists.");
////        }).orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));
//
//        return new ResponseEntity<>(follower, HttpStatus.CREATED);
//    }

//    @DeleteMapping("/communities/{communityId}/followers/{followerId}")
//    public ResponseEntity<HttpStatus> deleteFollowerFromCommunity(@PathVariable(value = "communityId") Long communityId, @PathVariable(value = "followerId") Long followerId) {
//
//        // check authentication
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
//                | (userDetails.getId().equals(followerId))
//                | memberUtil.isAuthorized(communityId, userDetails.getId())))
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//
//        Community community = communityRepository.findById(communityId)
//                .orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));
//
//        Student follower = studentRepository.findById(followerId)
//                        .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + followerId));
//        community.removeFollower(follower);
//        communityRepository.save(community);
//
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }

    @PutMapping("/students/{id}/updateImage")
	public ResponseEntity<Student> updateImage(@PathVariable(value = "id") Long id,
							@RequestParam("image") String image)
	{
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(id))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Id " + id + " not found"));

		student.setImage(image);
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

    @GetMapping("/events/{eventId}/attendants")
    public ResponseEntity<Set<Student>> getAllAttendantsByEventId(@PathVariable(value = "eventId") Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Event with id = " + eventId));

        Set<Student> attendants = event.getAttendants();
        return new ResponseEntity<>(attendants, HttpStatus.OK);
    }

    @GetMapping("/students/{studentId}/memberOf")
    public ResponseEntity<Set<Community>> getAllCommunitiesOfStudent(@PathVariable (value = "studentId") Long studentId,
        @RequestParam (value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
        @RequestParam (value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
        Pageable paging = PageRequest.of(page, size);

        Page<Community> communityPage = communityRepository.findCommunitiesByMembersId(studentId, paging);
        Set<Community> communities = communityPage.getNumberOfElements() == 0 ? Collections.emptySet() : communityPage.toSet();

        return new ResponseEntity<>(communities, HttpStatus.OK);
    }

    @GetMapping("/students/{studentId}/isMemberOf/{communityId}")
    public ResponseEntity<Boolean> isMemberOf(@PathVariable (value = "studentId") Long studentId, 
                                        @PathVariable (value = "communityId") Long communityId)
    {
       
        Member member = memberRepository.findByCommunityIdAndStudentId(communityId, studentId);

        if (member != null)
            return new ResponseEntity<>(true, HttpStatus.OK);
        else
            return new ResponseEntity<>(false, HttpStatus.OK);
    }

    @GetMapping("/students/{studentId}/isFollowerOf/{communityId}")
    public ResponseEntity<Boolean> isFollowerOf(@PathVariable (value = "studentId") Long studentId, 
                                        @PathVariable (value = "communityId") Long communityId)
    {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));  
            
        communityRepository.findById(communityId)   
                .orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        //search followers set of community for student with id = studentId
        for (Community c: student.getFollowingCommunities())
            if (c.getId().equals(communityId))
                return new ResponseEntity<>(true, HttpStatus.OK);

        return new ResponseEntity<>(false, HttpStatus.OK);
    }

}
