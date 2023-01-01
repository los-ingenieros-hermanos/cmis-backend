package com.los.cmisbackend.entity;

import javax.persistence.*;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    private ETag tag;

    public Tag () {

    }

    public Tag(ETag tag) {
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ETag getTag() {
        return tag;
    }

    public void setTag(ETag tag) {
        this.tag = tag;
    }
}
