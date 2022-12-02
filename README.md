# cmis-backend
CMIS backend implementation using Spring Boot.

# authentication

| Method | URL | ACTION |
| ------ | --- | ------ |
| POST   | /api/auth/signin | login an account |
| POST | /api/auth/signup | register an account |
| POST | /api/auth/signout | logout an account |

## requests

### POST -> /api/auth/signup
```json
{
    "firstName": "name",
    "lastName" : "lastname",
    "email": "email1@gtu.edu.tr",
    "password": "abcdefaeg",
    "role": ["student"]
}
```
```json
{
    "firstName": "name",
    "email": "email2@gtu.edu.tr",
    "password": "abcdefaeg",
    "role": ["community"]
}
```

### POST -> /api/auth/signin
```json
{
    "email": "email1@gtu.edu.tr",
    "password": "abcdefaeg"
}
```

### POST -> /api/auth/signout
request is not required.

## responses

### POST -> /api/auth/signup

#### success
```json
{
    "message": "User registered successfully!"
}
```
#### error
```json
{
    "message": "Error: Email is already in use!"
}
```

### POST -> /api/auth/signin 

#### success
```json
{
    "id": 1,
    "firstName": "name",
    "lastName": "lastname",
    "email": "email1@gtu.edu.tr",
    "roles": [
        "ROLE_STUDENT"
    ],
    "username": "email1@gtu.edu.tr"
}
```

#### error
```json
{
    "path": "/api/auth/signin",
    "error": "Unauthorized",
    "message": "Bad credentials",
    "status": 401
}
```

# user
| Method | URL | ACTION |
| ------ | --- | ------ |
| GET   | /api/cmis/users | get all users |
| GET | /api/cmis/users/{id} | get single user with given user id |
| DELETE | /api/cmis/users/{id} | delete single user with given user id |
| DELETE | /api/cmis/users/{id} | delete all users |

## requests

### GET -> /api/cmis/users

### GET -> /api/cmis/users/{id}

### DELETE -> /api/cmis/users/{id}

### DELETE -> /api/cmis/users/{id}

## responses

### GET -> /api/cmis/users

### GET -> /api/cmis/users/{id}

### DELETE -> /api/cmis/users/{id}

### DELETE -> /api/cmis/users/{id}

# student

| Method | URL | ACTION |
| ------ | --- | ------ |
| GET   | /api/cmis/students | get all students |
| GET | /api/cmis/students/{id} | get single student with given student id |
| POST | /api/cmis/users/{userId}/students | create student |
| PUT | /api/cmis/students/{id} | update student |
| DELETE |/api/cmis/students/{id} | delete student |
| DELETE | /api/cmis/students | delete all students |
| GET | api/cmis/communities/{communityId}/followers | get all followers(students) by community id
| POST | api/cmis/communities/{communityId}/followers | add follower to a community |
| DELETE | api/cmis/communities/{communityId}/followers/{followerId} | delete follower from community |

## requests

### GET -> /api/cmis/students

### GET -> /api/cmis/students/{id}

### POST -> /api/cmis/users/{userId}/students

### PUT -> /api/cmis/students/{id}

### DELETE -> /api/cmis/students/{id}

### DELETE -> /api/cmis/students 

### GET -> api/cmis/communities/{communityId}/followers

### POST -> api/cmis/communities/{communityId}/followers

### DELETE -> api/cmis/communities/{communityId}/followers/{followerId}

## responses

### GET -> /api/cmis/students

### GET -> /api/cmis/students/{id}

### POST -> /api/cmis/users/{userId}/students

### PUT -> /api/cmis/students/{id}

### DELETE -> /api/cmis/students/{id}

### DELETE -> /api/cmis/students 

### GET -> api/cmis/communities/{communityId}/followers

### POST -> api/cmis/communities/{communityId}/followers

### DELETE -> api/cmis/communities/{communityId}/followers/{followerId}

# post

| Method | URL | ACTION |
| ------ | --- | ------ |
| GET   | /api/cmis/posts | get all posts |
| GET | /api/cmis/posts/{id} | get single post with given post id |
| DELETE | /api/cmis/posts/{userId} | delete post |
| GET | /api/cmis/students/{studentId}/bookmarkedPost | get all bookmarked posts one student with id |
| POST | /api/cmis/students/{studentId}/bookmarkedPosts | add bookmarked post to student |
| DELETE | /api/cmis/students/{studentId}/bookmarkedPosts/{postId} | delete post from student |
| GET | api/cmis/communities/{communityId}/posts | get all posts of community |
| POST | api/cmis/communities/{communityId}/posts | add post to a community |

## requests

### GET -> /api/cmis/posts

### GET -> /api/cmis/posts/{id}

### DELETE -> /api/cmis/posts/{userId}

### GET -> /api/cmis/students/{studentId}/bookmarkedPost

### POST -> /api/cmis/students/{studentId}/bookmarkedPosts

### DELETE -> /api/cmis/students/{studentId}/bookmarkedPosts/{postId}

### GET -> api/cmis/communities/{communityId}/posts

### POST -> api/cmis/communities/{communityId}/posts

## responses

### GET -> /api/cmis/posts

### GET -> /api/cmis/posts/{id}

### DELETE -> /api/cmis/posts/{userId}

### GET -> /api/cmis/students/{studentId}/bookmarkedPost

### POST -> /api/cmis/students/{studentId}/bookmarkedPosts

### DELETE -> /api/cmis/students/{studentId}/bookmarkedPosts/{postId}

### GET -> api/cmis/communities/{communityId}/posts

### POST -> api/cmis/communities/{communityId}/posts

# community

| Method | URL | ACTION |
| ------ | --- | ------ |
| GET   | "api/cmis/communities/{id}", "api/cmis/users/{id}/communities" | get community by id |
| GET | /api/cmis/communities | get all communities |
| POST | /api/cmis/users/{userId}/communities | create community |
| PUT | /api/cmis/communities/{id} | update community |
| DELETE | /api/cmis/communities/{id} | delete community |
| DELETE | /api/cmis/communities | delete all communities |
| GET | api/cmis/students/{followerId}/followingCommunities | get all followed communities of student |
| POST | api/cmis/students/{followerId}/followingCommunities | add community to students following communities |
| DELETE | api/cmis/students/{followerId}/followingCommunities/{communityId} | delete community from students following communities |

## requests

### GET -> "api/cmis/communities/{id}", "api/cmis/users/{id}/communities"

### GET -> /api/cmis/communities

### POST -> /api/cmis/users/{userId}/communities

### PUT -> /api/cmis/communities/{id}

### DELETE -> /api/cmis/communities/{id} 

### DELETE -> /api/cmis/communities

### GET -> api/cmis/students/{followerId}

### POST -> api/cmis/students/{followerId}/followingCommunities

### DELETE -> api/cmis/students/{followerId}/followingCommunities/{communityId}

## responses

### GET -> "api/cmis/communities/{id}", "api/cmis/users/{id}/communities"

### GET -> /api/cmis/communities

### POST -> /api/cmis/users/{userId}/communities

### PUT -> /api/cmis/communities/{id}

### DELETE -> /api/cmis/communities/{id} 

### DELETE -> /api/cmis/communities

### GET -> api/cmis/students/{followerId}

### POST -> api/cmis/students/{followerId}/followingCommunities

### DELETE -> api/cmis/students/{followerId}/followingCommunities/{communityId}
