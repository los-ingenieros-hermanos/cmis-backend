package com.los.cmisbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="event")
public class Event {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    // map event to date, one event can have one date
    // when event is deleted, date is deleted
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name= "date_id")
    private Date date;


    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "events")
    @JsonIgnore
    private Set<Student> attendants = new HashSet<>();

    @Column(name="attendant_num")
    private Integer attendantsNum = Integer.valueOf(0);

    public Event() {

    }

    public Event(Date date) {
        this.date = date;
    }

    public Event(Date date, Set<Student> attendants) {
        this.date = date;
        this.attendants = attendants;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Set<Student> getAttendants() {
        return attendants;
    }

    public void setAttendants(Set<Student> attendants) {
        this.attendants = attendants;
    }

    public void addAttendant(Student student) {
        attendants.add(student);
        student.getEvents().add(this);
    }

    public void removeAttendant(Student student) {
        attendants.remove(student);
        student.getEvents().remove(this);
    }

    public Integer getAttendantsNum() {
        return attendantsNum;
    }

    public void setAttendantsNum(Integer attendantsNum) {
        this.attendantsNum = attendantsNum;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", date=" + date +
                '}';
    }


}
