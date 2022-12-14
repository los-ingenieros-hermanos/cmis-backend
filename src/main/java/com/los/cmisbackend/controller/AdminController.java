package com.los.cmisbackend.controller;

import com.los.cmisbackend.dao.AdminRepository;
import com.los.cmisbackend.dao.CommunityRepository;
import com.los.cmisbackend.dao.RoleRepository;
import com.los.cmisbackend.dao.UserRepository;
import com.los.cmisbackend.entity.*;
import com.los.cmisbackend.security.service.UserDetailsImpl;
import com.los.cmisbackend.util.CmisConstants;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "${cmis.app.baseUrl}", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("api/cmis")
public class AdminController {

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CommunityRepository communityRepository;

    @GetMapping("/admin")
    public ResponseEntity<List<Admin>> getAdmin() {
        List<Admin> admins = adminRepository.findAll();
        return new ResponseEntity<>(admins, HttpStatus.OK);
    }

    @GetMapping("/admin/{id}/unverifiedCommunities")
    public ResponseEntity<List<Community>> getUnverifiedCommunities(@PathVariable(value = "id") Long id,
                                                                    @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                                    @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found admin with id = " + id));

        Pageable pageable = PageRequest.of(page, size);

        Page<Community> unverifiedCommunities = communityRepository.findCommunitiesByRole("unverified", pageable);

        List<Community> communities = unverifiedCommunities.getNumberOfElements() > 0 ? unverifiedCommunities.getContent() : Collections.emptyList();

        return new ResponseEntity<>(communities, HttpStatus.OK);
    }

    @PostMapping("/admin/{id}/unverifiedCommunities/{userId}/accept")
    public ResponseEntity<User> verifyCommunity(@PathVariable(value = "id") Long id,
                                                @PathVariable(value = "userId") Long userId) {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found user with id = " + userId));

        Role userRole = roleRepository.findByName(ERole.ROLE_UNVERIFIED)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        if (!user.getRoles().contains(userRole)) {
            throw new ResourceNotFoundException("User is already verified!");
        }

        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found admin with id = " + id));

        admin.acceptCommunity(user);
        adminRepository.save(admin);

        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByName(ERole.ROLE_COMMUNITY)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);

        if(role.getName().equals(ERole.ROLE_COMMUNITY)) {
            Community community = communityRepository.findCommunityById(user.getId());
            community.setRole("community");
            communityRepository.save(community);
        }


        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/admin/{id}/unverifiedCommunities/{userId}/decline")
    public ResponseEntity<HttpStatus> declineCommunity(@PathVariable(value = "id") Long id,
                                                @PathVariable(value = "userId") Long userId) {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found user with id = " + id));

        Role userRole = roleRepository.findByName(ERole.ROLE_UNVERIFIED)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        if (!user.getRoles().contains(userRole)) {
            throw new ResourceNotFoundException("User is already verified!");
        }

        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found admin with id = " + id));

        // deletes from set, does not accepts
        admin.acceptCommunity(user);
        adminRepository.save(admin);

        if (communityRepository.existsById((userId))) {
            communityRepository.deleteById(userId);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/admin/createCommunity")
    public ResponseEntity<User> createCommunity(@RequestBody User user) {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if (userRepository.existsByUsername(user.getUsername())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByName(ERole.ROLE_COMMUNITY)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
