package com.los.cmisbackend.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "project_idea")
public class ProjectIdea {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="title")
    private String title;

    @Column(name="text")
    private String text;

    @Lob
    @Column(name = "image", nullable = true, columnDefinition = "MEDIUMBLOB", length = Integer.MAX_VALUE)
    private String image;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="student_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Student student;

    // no arg constructor
    public ProjectIdea() {

    }

    public ProjectIdea(String title, String text, String image) {
        this.title = title;
        this.text = text;
        this.image = image;
    }

    // getter setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
