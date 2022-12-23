package com.los.cmisbackend.controller;

import com.los.cmisbackend.dao.CommunityRepository;
import com.los.cmisbackend.dao.StudentRepository;
import com.los.cmisbackend.dao.TagRepository;
import com.los.cmisbackend.dao.UserRepository;
import com.los.cmisbackend.entity.Community;
import com.los.cmisbackend.entity.Student;
import com.los.cmisbackend.entity.User;
import com.los.cmisbackend.security.service.UserDetailsImpl;

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
public class CommunityController {

    @Autowired
    CommunityRepository communityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TagRepository tagRepository;

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

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !userDetails.getId().equals(id))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        // check existence
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id " + id + " not found"));

        if(communitiesRequest.getInfo() != null)
            community.setInfo(communitiesRequest.getInfo());

        if(communitiesRequest.getImage() != null)
            community.setImage(communitiesRequest.getImage());


        return new ResponseEntity<>(communityRepository.save(community), HttpStatus.OK);
    }

    @DeleteMapping("/communities/{id}")
    public ResponseEntity<HttpStatus> deleteCommunity(@PathVariable("id") Long id) {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(id))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        communityRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/communities")
    public ResponseEntity<HttpStatus> deleteAllCommunities() {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

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
    public ResponseEntity<Community> addCommunityToStudentsFollowers(@PathVariable(value = "followerId") Long followerId,
                                                                     @RequestBody Community communityRequest) {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !userDetails.getId().equals(followerId))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Community community = studentRepository.findById(followerId).map(follower -> {
            Long communityId = communityRequest.getId();

            // community is existed
            if (communityId != null) {
                Community _community = communityRepository.findById(communityId)
                        .orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));
                _community.addFollower(follower);
                communityRepository.save(_community);
                return _community;
            }

            throw new ResourceNotFoundException("Community does not exists.");
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + followerId));

        return new ResponseEntity<>(community, HttpStatus.OK);
    }

    @DeleteMapping("/students/{followerId}/followingCommunities/{communityId}")
    public ResponseEntity<HttpStatus> deleteCommunityFromStudentsFollowers(
            @PathVariable(value = "followerId") Long followerId, @PathVariable(value = "communityId") Long communityId) {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !userDetails.getId().equals(followerId))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Student follower = studentRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + followerId));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Community with id = " + communityId));

        community.removeFollower(follower);
        communityRepository.save(community);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/communities/{id}/updateImage")
	public ResponseEntity<Community> updateImage(@PathVariable(value = "id") Long id,
							@RequestParam("image") String image)
	{
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userDetails.getId().equals(id))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Community community = communityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Id " + id + " not found"));

		community.setImage(image);
        communityRepository.save(community);

        return new ResponseEntity<>(community, HttpStatus.OK);
	}

    @PutMapping("/communities/{id}/updateBanner")
	public ResponseEntity<Community> updateBanner(@PathVariable(value = "id") Long id,
							@RequestParam("banner") String image)
	{
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userDetails.getId().equals(id))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Community community = communityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Id " + id + " not found"));

		community.setBanner(image);
        communityRepository.save(community);

        return new ResponseEntity<>(community, HttpStatus.OK);
	}

    @GetMapping("/communities/{id}/image")
    public ResponseEntity<String> getCommunityImage(@PathVariable(value = "id") Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found community with id = " + id));

        var image = community.getImage();
        if (image == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    @GetMapping("/communities/{id}/banner")
    public ResponseEntity<String> getCommunityBanner(@PathVariable(value = "id") Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found community with id = " + id));

        var image = community.getBanner();
        if (image == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    @DeleteMapping("/communities/{id}/image")
    public ResponseEntity<HttpStatus> deleteCommunityImage(@PathVariable(value = "id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userDetails.getId().equals(id))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found community with id = " + id));

        community.setImage(null);
        communityRepository.save(community);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/communities/{id}/banner")
    public ResponseEntity<HttpStatus> deleteCommunityBanner(@PathVariable(value = "id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userDetails.getId().equals(id))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found community with id = " + id));

        community.setBanner(null);
        communityRepository.save(community);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/tags/{id}/communities")
    public ResponseEntity<List<Community>> getCommunitiesByTagId(@PathVariable(value = "id") Long id) {
        List<Community> communities = communityRepository.findCommunitiesByTagsId(id);

        return new ResponseEntity<>(communities, HttpStatus.OK);
    }
}