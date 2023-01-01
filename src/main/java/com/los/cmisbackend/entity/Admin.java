package com.los.cmisbackend.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "admin")
public class Admin {

    @Id
    Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name= "user_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinColumn(name = "community_id")
    private Set<User> unverifiedCommunities = new HashSet<>();

    public Admin() {
    }

    public Admin(User user) {
        this.user = user;
    }

    public Admin(User user, Set<User> unverifiedCommunities) {
        this.user = user;
        this.unverifiedCommunities = unverifiedCommunities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<User> getUnverifiedCommunities() {
        return unverifiedCommunities;
    }

    public void setUnverifiedCommunities(Set<User> unverifiedCommunities) {
        this.unverifiedCommunities = unverifiedCommunities;
    }

    public void addUnverifiedCommunity(User user) {
        unverifiedCommunities.add(user);
    }

    public void acceptCommunity(User user) {
        unverifiedCommunities.remove(user);
    }
}
