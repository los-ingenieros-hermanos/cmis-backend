package com.los.cmisbackend.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.los.cmisbackend.util.BCryptPasswordDeserializer;

import java.util.List;

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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;

	@Column(name="name", nullable = false, length = 45)
	private String name;

	@Column(name="email", nullable = false, length = 45, unique = true)
	private String email;

	@Column(name="info", length = 120)
	private String info;

	@Size(min = 60, max = 60)
	@JsonDeserialize(using = BCryptPasswordDeserializer.class )
	@Column(name="password", nullable = false, length = 60)
	private String password;

	@OneToMany(mappedBy = "community", fetch = FetchType.EAGER)
	private List<Post> posts;

	@ManyToMany(
		fetch=FetchType.LAZY,
		cascade= {CascadeType.PERSIST, CascadeType.MERGE,
		CascadeType.DETACH, CascadeType.REFRESH})
	@JoinTable(
		name="community_follower",
		inverseJoinColumns=@JoinColumn(name="follower_id"),
		joinColumns=@JoinColumn(name="community_id")
	)
	private List<User> followers;

	// add image
	// posts
	// add members

	public Community() {
	}


	public Community(String name, String email, String info, String password) {
		this.name = name;
		this.email = email;
		this.info = info;
		this.password = password;
	}

	public int getId() {
		return id;
	}



	public void setId(int id) {
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

	

	public List<Post> getPosts() {
		return posts;
	}


	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}


	public List<User> getFollowers() {
		return followers;
	}


	public void setFollowers(List<User> followers) {
		this.followers = followers;
	}


	@Override
	public String toString() {
		return "Community [id=" + id + ", name=" + name + ", email=" + email + ", info=" + info + "]";
	}

}
