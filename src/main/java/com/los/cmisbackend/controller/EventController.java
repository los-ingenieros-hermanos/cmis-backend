package com.los.cmisbackend.controller;


import com.los.cmisbackend.dao.EventRepository;
import com.los.cmisbackend.dao.PostRepository;
import com.los.cmisbackend.dao.StudentRepository;
import com.los.cmisbackend.entity.Event;
import com.los.cmisbackend.entity.Post;
import com.los.cmisbackend.entity.Student;
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
import java.util.Set;

@CrossOrigin(origins = "${cmis.app.baseUrl}", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/cmis")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/students/{id}/events")
    public ResponseEntity<Set<Event>> getAllEventsByStudentId(@PathVariable(value = "id") Long id,
    @RequestParam(value = "page", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_NUMBER) Integer page,
    @RequestParam(value = "size", required = false, defaultValue = CmisConstants.DEFAULT_PAGE_SIZE) Integer size)
    {

        studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + id));

        Pageable pageable = PageRequest.of(page, size);

        Page<Event> eventsPage = eventRepository.findEventsById(id, pageable);
        Set<Event> events = eventsPage.getNumberOfElements() == 0 ? Collections.emptySet() 
            : eventsPage.toSet();
            
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/students/{id}/events/{eventId}")
    public ResponseEntity<Event> getEventByStudentId(@PathVariable(value = "id") Long id,
                                                     @PathVariable(value = "eventId") Long eventId) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + id));
        // get event
        Event event = student.getEvents().stream()
                .filter(e -> e.getId().equals(eventId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Not found Event with id = " + eventId));
        // return event
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @PostMapping("/students/{id}/events")
    public ResponseEntity<Event> addEventToStudent(@PathVariable(value = "id") Long id, @RequestBody Event eventRequest) {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(id))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Event event = studentRepository.findById(id).map(student -> {
            Long eventId = eventRequest.getId();

            // event is existed
            if (eventId != null) {
                Event _event = eventRepository.findById(eventId)
                        .orElseThrow(() -> new ResourceNotFoundException("Not found Event with id = " + eventId));
                student.addEvent(_event);
                studentRepository.save(student);
                return _event;
            }
            throw new ResourceNotFoundException("Event does not exists.");
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + id));

        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

    @DeleteMapping("/students/{id}/events/{eventId}")
    public ResponseEntity<Event> deleteEventFromStudent(@PathVariable(value = "id") Long id,
                                                        @PathVariable(value = "eventId") Long eventId) {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(id))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Event event = studentRepository.findById(id).map(student -> {
            Event _event = student.getEvents().stream()
                    .filter(e -> e.getId().equals(eventId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Not found Event with id = " + eventId));
            student.removeEvent(_event);
            studentRepository.save(student);
            return _event;
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + id));

        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @SuppressWarnings("unused")
    @GetMapping("/events/{id}/eventDetails")
    public ResponseEntity<Post> getEventDetailByEventId(@PathVariable(value = "id") Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Event with id = " + id));
        // find post by event id
        Post post = postRepository.findPostByEventId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Post with event id = " + id));
        // return post
        return new ResponseEntity<>(post, HttpStatus.OK);
    }
}
