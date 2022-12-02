package com.los.cmisbackend.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.los.cmisbackend.util.BCryptPasswordDeserializer;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="student")
public class Student {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "bookmarkedpost_student",
            joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"))
    private Set<Post> bookMarkedPosts = new HashSet<>();

    @ManyToMany(mappedBy = "followers")
    private Set<Community> followingCommunities = new HashSet<>();

    public Student () {

    }

    public Student(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        System.out.println(password);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Post> getBookMarkedPosts() {
        return bookMarkedPosts;
    }

    public void setBookMarkedPosts(Set<Post> bookMarkedPosts) {
        this.bookMarkedPosts = bookMarkedPosts;
    }


    public Set<Community> getFollowingCommunities() {
        return followingCommunities;
    }

    public void setFollowingCommunities(Set<Community> followingCommunities) {
        this.followingCommunities = followingCommunities;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}