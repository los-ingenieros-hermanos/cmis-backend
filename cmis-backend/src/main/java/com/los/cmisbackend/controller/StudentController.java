package com.los.cmisbackend.controller;

import com.los.cmisbackend.dao.StudentRepository;
import com.los.cmisbackend.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/cmis")
public class StudentController {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    PasswordEncoder encoder;

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = new ArrayList<Student>();

        studentRepository.findAll().forEach(students::add);

        if (students.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable("id") Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + id));

        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @PostMapping("/students")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student _student = studentRepository.save(new Student(student.getFirstName(), student.getLastName(),
                                                                student.getEmail(), encoder.encode(student.getPassword())));
        return new ResponseEntity<>(_student, HttpStatus.CREATED);
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable("id") long id, @RequestBody Student student) {
        Student _student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + id));

        _student.setFirstName(student.getFirstName());
        _student.setLastName(student.getLastName());
        _student.setEmail(student.getEmail());
        _student.setPassword(student.getPassword());

        return new ResponseEntity<>(studentRepository.save(_student), HttpStatus.OK);
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<HttpStatus> deleteStudent(@PathVariable("id") long id) {
        studentRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/students")
    public ResponseEntity<HttpStatus> deleteAllStudents() {
        studentRepository.deleteAll();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
