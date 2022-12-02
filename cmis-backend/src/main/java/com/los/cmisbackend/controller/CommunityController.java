package com.los.cmisbackend.controller;

import com.los.cmisbackend.dao.CommunityRepository;
import com.los.cmisbackend.dao.StudentRepository;
import com.los.cmisbackend.dao.UserRepository;
import com.los.cmisbackend.entity.Community;
import com.los.cmisbackend.entity.Student;
import com.los.cmisbackend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/cmis")
public class CommunityController {

    @Autowired
    CommunityRepository communityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudentRepository studentRepository;

    @GetMapping({ "/communities/{id}", "/users/{id}/communities" })
    public ResponseEntity<Community> getCommunityById(@PathVariable(value = "id") Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found community with id = " + id));

        return new ResponseEntity<>(community, HttpStatus.OK);
    }

    @GetMapping("/communities")
    public ResponseEntity<List<Community>> getAllCommunities() {
        List<Community> communities = new ArrayList<Community>();

        communityRepository.findAll().forEach(communities::add);

        if (communities.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(communities, HttpStatus.OK);
    }

    @PostMapping("/users/{userId}/communities")
    public ResponseEntity<Community> createCommunity(@PathVariable(value = "userId") Long userId,
                                                 @RequestBody Community communitiesRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found User with id = " + userId));

        communitiesRequest.setUser(user);
        Community community = communityRepository.save(communitiesRequest);

        return new ResponseEntity<>(community, HttpStatus.CREATED);
    }

    @PutMapping("/communities/{id}")
    public ResponseEntity<Community> updateCommunity(@PathVariable("id") Long id,
                                                 @RequestBody Community communitiesRequest) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id " + id + " not found"));

        community.setFollowers(communitiesRequest.getFollowers());
        community.setInfo(communitiesRequest.getInfo());
        // handle posts !!

        return new ResponseEntity<>(communityRepository.save(community), HttpStatus.OK);
    }

    @DeleteMapping("/communities/{id}")
    public ResponseEntity<HttpStatus> deleteCommunity(@PathVariable("id") Long id) {
        communityRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/communities")
    public ResponseEntity<HttpStatus> deleteAllCommunities() {
        communityRepository.deleteAll();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/students/{followerId}/followingCommunities")
    public ResponseEntity<Set<Community>> getAllFollowedCommunities(@PathVariable(value = "followerId") Long followerId) {
        Student follower = studentRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + followerId));
        Set<Community> communities = follower.getFollowingCommunities();
        return new ResponseEntity<>(communities, HttpStatus.OK);
    }

    @PostMapping("/students/{followerId}/followingCommunities")
    public ResponseEntity<Community> addCommunityToStudentsFollowers(@PathVariable(value = "followerId") Long followerId, @RequestBody Community communityRequest) {
        Community community = studentRepository.findById(followerId).map(follower -> {
            Long communityId = communityRequest.getId();

            // community is existed
            if (communityId != null) {
                Community _community = communityRepository.findById(communityId)
                        .orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));
                follower.addCommunityToFollowing(_community);
                studentRepository.save(follower);
                return _community;
            }

            throw new ResourceNotFoundException("Community does not exists.");
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + followerId));

        return new ResponseEntity<>(community, HttpStatus.OK);
    }

    @DeleteMapping("/students/{followerId}/followingCommunities/{communityId}")
    public ResponseEntity<HttpStatus> deleteCommunityFromStudentsFollowers(@PathVariable(value = "followerId") Long followerId, @PathVariable(value = "communityId") Long communityId) {
        Student follower = studentRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + followerId));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        follower.deleteCommunityFromFollowing(community);
        studentRepository.save(follower);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}