package com.los.cmisbackend.entity;

import javax.persistence.*;

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

    // map student and event, one event can have many students
    // when student is deleted, event is not deleted

    public Event() {

    }

    public Event(Date date) {
        this.date = date;
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


    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", date=" + date +
                '}';
    }


}
