package com.los.cmisbackend.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.los.cmisbackend.util.BCryptPasswordDeserializer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.persistence.JoinColumn;

/*
+----------+--------------+------+-----+-----------+-------------------+
| Field    	| Type        	| Null 	| Default  	| Extra             |
+----------+--------------+------+-----+-----------+-------------------+
| id       	| varchar(11) 	| NO  	|      		|  AUTO_INCREMENT   |
| name     	| varchar(45) 	| NO  	| 	NULL   	|                   |
| email    	| varchar(45) 	| NO  	|  	NULL  	|                   |
| info 		| varchar(120)	| YES 	|	NULL	|					|
| password 	| varchar(60) 	| NO 	|	NULL 	|                   |
+----------+--------------+------+-----+-----------+-------------------+
*/

@Entity
@Table(name="community")
public class Community {
	
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;

	@Column(name="name")
	private String name;

	@Column(name="email")
	private String email;

	@Column(name="info", length = 120)
	private String info;

	@Column(name="password")
	private String password;

	@OneToMany(mappedBy = "community")
	private Set<Post> posts = new HashSet<>();

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "community_follower",
			joinColumns = @JoinColumn(name = "community_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"))
	private Set<Student> followers = new HashSet<>();

	// add image
	// posts
	// add members

	public Community() {
	}


	public Community(Long id, String name, String email, String info, String password) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.info = info;
		this.password = password;
	}

	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String getInfo() {
		return info;
	}



	public void setInfo(String info) {
		this.info = info;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	

	public Set<Post> getPosts() {
		return posts;
	}


	public void setPosts(Set<Post> posts) {
		this.posts = posts;
	}


	public Set<Student> getFollowers() {
		return followers;
	}


	public void setFollowers(Set<Student> followers) {
		this.followers = followers;
	}


	@Override
	public String toString() {
		return "Community [id=" + id + ", name=" + name + ", email=" + email + ", info=" + info + "]";
	}

}
