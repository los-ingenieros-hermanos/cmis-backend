package com.los.cmisbackend.controller;

import com.los.cmisbackend.dao.CommunityRepository;
import com.los.cmisbackend.dao.RoleRepository;
import com.los.cmisbackend.dao.StudentRepository;
import com.los.cmisbackend.dao.UserRepository;
import com.los.cmisbackend.entity.Community;
import com.los.cmisbackend.entity.ERole;
import com.los.cmisbackend.entity.Role;
import com.los.cmisbackend.entity.User;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@CrossOrigin(origins = "${cmis.app.baseUrl}", maxAge = 3600, allowCredentials = "true")
@RestController()
@RequestMapping("/api/cmis")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CommunityRepository communityRepository;

    @Autowired
    RoleRepository roleRepository;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {

        List<User> users = new ArrayList<User>();

        userRepository.findAll().forEach(users::add);

        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found User with id = " + id));

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") Long id) {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if ( !(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(id))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
        }
        else if (communityRepository.existsById((id))) {
            communityRepository.deleteById(id);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/users")
    public ResponseEntity<HttpStatus> deleteAllUsers() {

        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        userRepository.deleteAll();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // find users by first name containing, add pagination
    @GetMapping("/users/search")
    public ResponseEntity<List<User>> getUsersBySearch(@RequestParam(value = "search") String search,
                                                                  @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                                  @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);

        Page<User> userPage = userRepository.findUsersByFirstNameContaining(search, pageable);

        List<User> users = userPage.getNumberOfElements() == 0 ? Collections.emptyList()
                : userPage.getContent();

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // find communities by first name containing, add pagination
    @GetMapping("/communities/search")
    public ResponseEntity<List<User>> getCommunitiesBySearch(@RequestParam(value = "search") String search,
                                                                  @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                                  @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);
        Role role = roleRepository.findByName(ERole.ROLE_COMMUNITY)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        Page<User> communities = userRepository.findUsersByRolesAndFirstNameContaining(role, search, pageable);

        List<User> communityList = communities.getNumberOfElements() == 0 ? Collections.emptyList()
                : communities.getContent();

        return new ResponseEntity<>(communityList, HttpStatus.OK);
    }

    // find communities by first name containing, add pagination
    @GetMapping("/students/search")
    public ResponseEntity<List<User>> getStudentsBySearch(@RequestParam(value = "search") String search,
                                                             @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                             @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);
        Role role = roleRepository.findByName(ERole.ROLE_STUDENT)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        Page<User> students = userRepository.findUsersByRolesAndFirstNameContaining(role, search, pageable);

        List<User> studentList = students.getNumberOfElements() == 0 ? Collections.emptyList()
                : students.getContent();

        return new ResponseEntity<>(studentList, HttpStatus.OK);
    }

    // find unverified users by first name containing, add pagination
    @GetMapping("/users/unverified/search")
    public ResponseEntity<List<User>> getUnverifiedUsersBySearch(@RequestParam(value = "search") String search,
                                                                  @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                                  @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);
        Role role = roleRepository.findByName(ERole.ROLE_UNVERIFIED)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        Page<User> students = userRepository.findUsersByRolesAndFirstNameContaining(role, search, pageable);

        List<User> unverifiedUsers = students.getNumberOfElements() == 0 ? Collections.emptyList()
                : students.getContent();

        return new ResponseEntity<>(unverifiedUsers, HttpStatus.OK);
    }

}
