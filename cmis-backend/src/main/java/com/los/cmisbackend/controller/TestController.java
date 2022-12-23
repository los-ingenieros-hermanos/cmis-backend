package com.los.cmisbackend.controller;

import com.los.cmisbackend.dao.StudentRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:8070", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public String studentAccess() {
        return "Student Content.";
    }

    @GetMapping("/community")
    @PreAuthorize("hasRole('COMMUNITY') or hasRole('ADMIN')")
    public String moderatorAccess() {
        return "community content.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
}
