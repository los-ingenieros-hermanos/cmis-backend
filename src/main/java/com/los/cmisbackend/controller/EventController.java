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
import org.springframework.data.domain.PageImpl;
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
import java.util.List;
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
    public ResponseEntity<Set<Event>> getAllEventsByStudentId(@PathVariable(value = "id") Long id)
    {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + id));

        //System.out.println(student.getEvents());
        Set<Event> events = student.getEvents();

        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("events/{eventId}")
    public ResponseEntity<Event> getEventByStudentId(@PathVariable(value = "eventId") Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Event with id = " + eventId));
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @GetMapping("/events")
    public ResponseEntity<List<Event>> getAllEvents(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Event> events = eventRepository.findAll(paging);
        List<Event> eventList = events.getNumberOfElements() == 0 ? Collections.emptyList() : events.getContent();
        return new ResponseEntity<>(eventList, HttpStatus.OK);
    }

    @PostMapping("/students/{id}/events")
    public ResponseEntity<Event> addEventToStudent(@PathVariable(value = "id") Long id, @RequestBody Event eventRequest) {
        // check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                | (userDetails.getId().equals(id))))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Event event = eventRepository.findById(eventRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Event with id = " + eventRequest.getId()));

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + id));

        Set<Event> events = student.getEvents();

        if (!events.contains(event)) {
            events.add(event);
            event.setAttendantsNum(event.getAttendantsNum() + 1);
            student.setEvents(events);
            studentRepository.save(student);
        }

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

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + id));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Event with id = " + eventId));

        Set<Event> events = student.getEvents();

        if (events.contains(event)) {
            events.remove(event);
            event.setAttendantsNum(event.getAttendantsNum() - 1);
            student.setEvents(events);
            studentRepository.save(student);
        }
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

    // check if student is attended event
    @GetMapping("/students/{studentId}/events/{eventId}/isAttended")
    public ResponseEntity<Boolean> isAttendedEvent(@PathVariable(value = "studentId") Long studentId,
                                                   @PathVariable(value = "eventId") Long eventId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + studentId));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Event with id = " + eventId));

        return new ResponseEntity<>(student.getEvents().contains(event), HttpStatus.OK);
    }

    // get all eventDetails
    @GetMapping("/allEventDetails")
    public ResponseEntity<List<Post>> getAllEventDetails() {
        List<Post> posts = postRepository.findAll();
        List<Post> eventDetails = new ArrayList<>();
        for (Post post : posts) {
            if (!post.getEvent().isEmpty()) {
                eventDetails.add(post);
            }
        }
        return new ResponseEntity<>(eventDetails, HttpStatus.OK);
    }
}
