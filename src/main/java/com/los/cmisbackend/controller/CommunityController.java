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
import java.util.Set;

@CrossOrigin(origins = "${cmis.app.baseUrl}", maxAge = 3600, allowCredentials = "true")
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

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    MemberUtil memberUtil;

    @GetMapping({ "/communities/{id}", "/users/{id}/communities" })
    public ResponseEntity<Community> getCommunityById(@PathVariable(value = "id") Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found community with id = " + id));

        return new ResponseEntity<>(community, HttpStatus.OK);
    }

    @GetMapping("/communities")
    public ResponseEntity<List<Community>> getAllTeams(
            @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {

        Pageable pageable = PageRequest.of(page, size);

        Page<Community> communitiesPage = communityRepository.findCommunitiesByRole("community", pageable);

        List<Community> communities = communitiesPage.getNumberOfElements() == 0 ? Collections.emptyList()
                : communitiesPage.getContent();

        return new ResponseEntity<>(communities, HttpStatus.OK);
    }

    @GetMapping("/teams")
    public ResponseEntity<List<Community>> getAllCommunities(
            @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
            Pageable pageable = PageRequest.of(page, size);

            Page<Community> teams = communityRepository.findCommunitiesByType("team", pageable);

            List<Community> teamsList = teams.getNumberOfElements() == 0 ? Collections.emptyList()
                    : teams.getContent();

            return new ResponseEntity<>(teamsList, HttpStatus.OK);
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
        System.out.println("string: " + authentication.getPrincipal());
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // check if user is an admin or a member of the community

        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (memberUtil.isAuthorized(id, userDetails.getId()))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Community community = communityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Id " + id + " not found"));

        if(communitiesRequest.getInfo() != null)
            community.setInfo(communitiesRequest.getInfo());

        if(communitiesRequest.getImage() != null)
            community.setImage(communitiesRequest.getImage());

        if(communitiesRequest.getType() != null &&
                (community.getType().equals("community") || community.getType().equals("team")))
            community.setType(communitiesRequest.getType());

        return new ResponseEntity<>(communityRepository.save(community), HttpStatus.OK);
    }

    @DeleteMapping("/communities/{id}")
    public ResponseEntity<HttpStatus> deleteCommunity(@PathVariable("id") Long id) {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (memberUtil.isAuthorized(id, userDetails.getId()))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        communityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Id " + id + " not found"));

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
    public ResponseEntity<Set<Community>> getAllFollowedCommunities(@PathVariable(value = "followerId") Long followerId,
        @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
        @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size) 
    {

        Pageable pageable = PageRequest.of(page, size);

        Page<Community> communitiesPage = communityRepository.findCommunitiesByFollowersId(followerId, pageable);

        Set<Community> communities = communitiesPage.getNumberOfElements() == 0 ? Collections.emptySet()
                : communitiesPage.toSet();

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
							@RequestBody String image)
	{
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (memberUtil.isAuthorized(id, userDetails.getId()))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

            
        Community community = communityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Id " + id + " not found"));

		community.setImage(image);
        communityRepository.save(community);

        return new ResponseEntity<>(community, HttpStatus.OK);
	}

    @PutMapping("/communities/{id}/updateBanner")
	public ResponseEntity<Community> updateBanner(@PathVariable(value = "id") Long id,
							@RequestBody String image)
	{
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
            | (memberUtil.isAuthorized(id, userDetails.getId()))))
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
    public ResponseEntity<Community> deleteCommunityImage(@PathVariable(value = "id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
            | (memberUtil.isAuthorized(id, userDetails.getId()))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                
        Community community = communityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Id " + id + " not found"));

        community.setImage(null);
        communityRepository.save(community);

        return new ResponseEntity<>(community, HttpStatus.OK);
    }

    @DeleteMapping("/communities/{id}/banner")
    public ResponseEntity<Community> deleteCommunityBanner(@PathVariable(value = "id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        

        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
            | (memberUtil.isAuthorized(id, userDetails.getId()))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Community community = communityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Id " + id + " not found"));

        community.setBanner(null);
        communityRepository.save(community);

        return new ResponseEntity<>(community, HttpStatus.OK);
    }

    @GetMapping("/tags/{id}/communities")
    public ResponseEntity<List<Community>> getCommunitiesByTagId(@PathVariable(value = "id") Long id, 
        @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
        @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size) 

    {
        Pageable pageable = PageRequest.of(page, size);

        Page<Community> communitiesPage = communityRepository.findCommunitiesByTagsId(id, pageable);

        List<Community> communities = communitiesPage.getNumberOfElements() == 0 ? Collections.emptyList() 
            : communitiesPage.getContent();

        return new ResponseEntity<>(communities, HttpStatus.OK);
    }
}