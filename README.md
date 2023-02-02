# cmis-backend
CMIS backend implementation using Spring Boot.

# RUN
``` mvn spring-boot:run ```  

# CMIS Project Documentation
[DOC](https://github.com/los-ingenieros-hermanos/gtu-community-management-and-interaction-system)  

# Database Relation Diagram
![](images/relation.png)  
  
# Requests
![](images/r1.jpg)
![](images/r2.jpg)
![](images/r3.jpg)
![](images/r4.jpg)
![](images/r5.jpg)
![](images/r6.jpg)
![](images/r7.jpg)
![](images/r8.jpg)
![](images/r9.jpg)
![](images/r10.jpg)
![](images/r11.jpg)
![](images/r12.jpg)
![](images/r13.jpg)
![](images/r14.jpg)
![](images/r15.jpg)  
  
# Test Cases and Results
![](images/t1.png)
![](images/t2.png)
![](images/t3.png)
![](images/t4.png)
![](images/t5.png)
![](images/t6.png)
![](images/t7.png)
![](images/t8.png)
![](images/t9.png)
![](images/t10.png)
![](images/t11.png)
![](images/t12.png)
![](images/t13.png)
![](images/t14.png)
![](images/t15.png)
![](images/t16.png)
![](images/t17.png)
![](images/t18.png)
![](images/t19.png)
![](images/t20.png)
![](images/t21.png)
![](images/t22.png)
![](images/t23.png)
![](images/t24.png)
![](images/t25.png)
![](images/t26.png)
![](images/t27.png)
![](images/t28.png)
![](images/t29.png)
![](images/t30.png)
![](images/t31.png)
![](images/t32.png)
![](images/t33.png)
![](images/t34.png)
![](images/t35.png)
![](images/t36.png)
![](images/t37.png)
![](images/t38.png)
![](images/t39.png)
![](images/t40.png)
![](images/t41.png)
![](images/t42.png)
![](images/t43.png)
![](images/t44.png)
  

# Detailed Explanations Of Requests and Responses(not up to date)

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
    "firstName": "Elma",
    "lastName" : "Tekne",
    "email": "e.tekne2021@gtu.edu.tr",
    "password": "elmatekne1234",
    "role": ["student"]
}
```
```json
{
    "firstName": "Armut Sehpa Topluluğu",
    "email": "armutsehpa@gtu.edu.tr",
    "password": "abcd123abc",
    "role": ["community"]
}
```

### POST -> /api/auth/signin
```json
{
    "email": "e.tekne2021@gtu.edu.tr",
    "password": "elmatekne1234"
}
```

### POST -> /api/auth/signout
```json

```

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
    "id": 2,
    "firstName": "Elma",
    "lastName": "Tekne",
    "email": "e.tekne2021@gtu.edu.tr",
    "roles": [
        "ROLE_STUDENT"
    ],
    "username": "e.tekne2021@gtu.edu.tr"
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
```json

```
### GET -> /api/cmis/users/{id}
```json

```
### DELETE -> /api/cmis/users/{id}
```json

```
### DELETE -> /api/cmis/users/{id}
```json

```
## responses
empty

### GET -> /api/cmis/users
```json
[
    {
        "id": 2,
        "firstName": "Elma",
        "lastName": "Tekne",
        "username": "e.tekne2021@gtu.edu.tr",
        "email": "e.tekne2021@gtu.edu.tr",
        "password": "$2a$10$klptaGaOpK871Rd1OyWHDeJXx6zD4eb7qXeMjZahtVoPl8jjkAHym",
        "roles": [
            {
                "id": 1,
                "name": "ROLE_STUDENT"
            }
        ]
    },
    {
        "id": 3,
        "firstName": "Armut Sehpa Topluluğu",
        "lastName": null,
        "username": "armutsehpa@gtu.edu.tr",
        "email": "armutsehpa@gtu.edu.tr",
        "password": "$2a$10$OoVkGN8sYGSOEXZwhLHtrO0t3/963MYNb15IAc5QxKZ04EaZ3kkga",
        "roles": [
            {
                "id": 3,
                "name": "ROLE_COMMUNITY"
            }
        ]
    }
]
```
### GET -> /api/cmis/users/{id}
```json
{
    "id": 2,
    "firstName": "Elma",
    "lastName": "Tekne",
    "username": "e.tekne2021@gtu.edu.tr",
    "email": "e.tekne2021@gtu.edu.tr",
    "password": "$2a$10$klptaGaOpK871Rd1OyWHDeJXx6zD4eb7qXeMjZahtVoPl8jjkAHym",
    "roles": [
        {
            "id": 1,
            "name": "ROLE_STUDENT"
        }
    ]
}
```
### DELETE -> /api/cmis/users/{id}
```json

```
### DELETE -> /api/cmis/users/{id}
```json

```

# student

| Method | URL | ACTION |
| ------ | --- | ------ |
| GET   | /api/cmis/students | get all students |
| GET | /api/cmis/students/{id} | get single student with given student id |
| GET | /api/cmis/users/{userId}/students | get student through userId |
| GET | /api/cmis/students/{id}/image |get student image (base64 string) |
| PUT | /api/cmis/students/{id} | update student |
| PUT | /api/cmis/students/{id}/updateImage |update student image|
| DELETE |/api/cmis/students/{id} | delete student |
| DELETE | /api/cmis/students | delete all students |
| GET | api/cmis/communities/{communityId}/followers | get all followers(students) by community id
| POST | api/cmis/communities/{communityId}/followers | add follower to a community |
| DELETE | api/cmis/communities/{communityId}/followers/{followerId} | delete follower from community |

## requests

### GET -> /api/cmis/students
```json

```

### GET -> /api/cmis/students/{id}
```json

```

### GET -> /api/cmis/users/{userId}/students
```json

```

### PUT -> /api/cmis/students/{id}
**NOTE:Student does not have enough property to update yet.** 
```json

```

### PUT -> /api/cmis/students/{id}/updateImage 
```

```

### DELETE -> /api/cmis/students/{id}
```json

```

### DELETE -> /api/cmis/students 
```json

```

### GET -> api/cmis/communities/{communityId}/followers
```json

```

### POST -> api/cmis/communities/{communityId}/followers
```json
{
    "id" : 2
}
```

### DELETE -> api/cmis/communities/{communityId}/followers/{followerId}
```json

```

## responses

### GET -> /api/cmis/students
```json
[
    {
        "id": 2,
        "user": {
            "id": 2,
            "firstName": "Elma",
            "lastName": "Tekne",
            "username": "e.tekne2021@gtu.edu.tr",
            "email": "e.tekne2021@gtu.edu.tr",
            "password": "$2a$10$klptaGaOpK871Rd1OyWHDeJXx6zD4eb7qXeMjZahtVoPl8jjkAHym",
            "roles": [
                {
                    "id": 1,
                    "name": "ROLE_STUDENT"
                }
            ]
        },
        "bookMarkedPosts": []
    },
    {
        "id": 4,
        "user": {
            "id": 4,
            "firstName": "ahmet",
            "lastName": "kahve",
            "username": "kahmet@gtu.edu.tr",
            "email": "kahmet@gtu.edu.tr",
            "password": "$2a$10$GBK2u9YPXiSfTH0GLHoW1u0x/U905Bap4DGCVVpRshWj34kJcaOPO",
            "roles": [
                {
                    "id": 1,
                    "name": "ROLE_STUDENT"
                }
            ]
        },
        "bookMarkedPosts": []
    }
]
```

### GET -> /api/cmis/students/{id}
```json
{
    "id": 4,
    "user": {
        "id": 4,
        "firstName": "ahmet",
        "lastName": "kahve",
        "username": "kahmet@gtu.edu.tr",
        "email": "kahmet@gtu.edu.tr",
        "password": "$2a$10$GBK2u9YPXiSfTH0GLHoW1u0x/U905Bap4DGCVVpRshWj34kJcaOPO",
        "roles": [
            {
                "id": 1,
                "name": "ROLE_STUDENT"
            }
        ],
        "image" : //base64 string
    },
    "bookMarkedPosts": []
}
```

### GET -> /api/cmis/students/{id}/image
```
    //base64 string 
```

### POST -> /api/cmis/users/{userId}/students
userId=6
```json
{
    "id": 6,
    "user": {
        "id": 6,
        "firstName": "kehribar",
        "lastName": "iklim",
        "username": "kehrib@gtu.edu.tr",
        "email": "kehrib@gtu.edu.tr",
        "password": "$2a$10$9EupkT7fVBpdivOVU1vJg.WApdl2E/qRZDrQQ4X4BxUc7W/BGLf56",
        "roles": [
            {
                "id": 1,
                "name": "ROLE_STUDENT"
            }
        ]
    },
    "bookMarkedPosts": []
}
```

### PUT -> /api/cmis/students/{id}
```json

```

### DELETE -> /api/cmis/students/{id}
```json

```

### DELETE -> /api/cmis/students 
```json

```

### GET -> api/cmis/communities/{communityId}/followers
```json
[
    {
        "id": 2,
        "user": {
            "id": 2,
            "firstName": "Elma",
            "lastName": "Tekne",
            "username": "e.tekne2021@gtu.edu.tr",
            "email": "e.tekne2021@gtu.edu.tr",
            "password": "$2a$10$klptaGaOpK871Rd1OyWHDeJXx6zD4eb7qXeMjZahtVoPl8jjkAHym",
            "roles": [
                {
                    "id": 1,
                    "name": "ROLE_STUDENT"
                }
            ]
        },
        "bookMarkedPosts": []
    }
]
```

### POST -> api/cmis/communities/{communityId}/followers
```json
{
    "id": 2,
    "user": {
        "id": 2,
        "firstName": "Elma",
        "lastName": "Tekne",
        "username": "e.tekne2021@gtu.edu.tr",
        "email": "e.tekne2021@gtu.edu.tr",
        "password": "$2a$10$klptaGaOpK871Rd1OyWHDeJXx6zD4eb7qXeMjZahtVoPl8jjkAHym",
        "roles": [
            {
                "id": 1,
                "name": "ROLE_STUDENT"
            }
        ]
    },
    "bookMarkedPosts": []
}
```

### DELETE -> api/cmis/communities/{communityId}/followers/{followerId}
```json

```

# post

| Method | URL | ACTION |
| ------ | --- | ------ |
| GET   | /api/cmis/posts | get all posts |
| GET | /api/cmis/posts/{id} | get single post with given post id |
| DELETE | /api/cmis/posts/{id} | delete post |
| GET | /api/cmis/students/{studentId}/bookmarkedPost | get all bookmarked posts one student with id |
| POST | /api/cmis/students/{studentId}/bookmarkedPosts | add bookmarked post to student |
| DELETE | /api/cmis/students/{studentId}/bookmarkedPosts/{postId} | delete post from student |
| GET | api/cmis/communities/{communityId}/posts | get all posts of community |
| POST | api/cmis/communities/{communityId}/posts | add post to a community |

## requests

### GET -> /api/cmis/posts
```json

```

### GET -> /api/cmis/posts/{id}
```json

```

### DELETE -> /api/cmis/posts/{id}
```json

```

### GET -> /api/cmis/students/{studentId}/bookmarkedPost
```json

```

### POST -> /api/cmis/students/{studentId}/bookmarkedPosts
```json
{
    "id" : "1"
}
```

### DELETE -> /api/cmis/students/{studentId}/bookmarkedPosts/{postId}
```json

```

### GET -> api/cmis/communities/{communityId}/posts
```json

```

### POST -> api/cmis/communities/{communityId}/posts
```json
{
    "title": "Announcement!",
    "text": "text"
}
```

## responses

### GET -> /api/cmis/posts
```json
[
    {
        "id": 1,
        "title": "Announcement!",
        "text": "text"
    },
    {
        "id": 2,
        "title": "Event!",
        "text": "text4"
    },
    {
        "id": 3,
        "title": "Meeting",
        "text": "meeting on tuesday."
    }
]
```

### GET -> /api/cmis/posts/{id}
```json
{
    "id": 2,
    "title": "Event!",
    "text": "text4"
}
```

### DELETE -> /api/cmis/posts/{id}
```json

```

### GET -> /api/cmis/students/{studentId}/bookmarkedPost
```json
{
    "id": 1,
    "title": "Announcement!",
    "text": "text"
}
```

### POST -> /api/cmis/students/{studentId}/bookmarkedPosts
```json
{
    "id": 1,
    "title": "Announcement!",
    "text": "text"
}
```

### DELETE -> /api/cmis/students/{studentId}/bookmarkedPosts/{postId}
```json

```

### GET -> api/cmis/communities/{communityId}/posts
```json
[
    {
        "id": 1,
        "title": "Announcement!",
        "text": "text"
    },
    {
        "id": 2,
        "title": "Event!",
        "text": "text4"
    }
]
```

### POST -> api/cmis/communities/{communityId}/posts
```json
{
    "id": 1,
    "title": "Announcement!",
    "text": "text"
}
```

# community

| Method | URL | ACTION |
| ------ | --- | ------ |
| GET   | "api/cmis/communities/{id}", "api/cmis/users/{id}/communities" | get community by id |
| GET | /api/cmis/communities | get all communities |
| GET | /api/cmis/communities/{id}/image | get community image |
| PUT | /api/cmis/communities/{id} | update community |
| PUT | /api/cmis/communities/{id}/updateImage | update community image |
| DELETE | /api/cmis/communities/{id} | delete community |
| DELETE | /api/cmis/communities | delete all communities |
| GET | api/cmis/students/{followerId}/followingCommunities | get all followed communities of student |
| POST | api/cmis/students/{followerId}/followingCommunities | add community to students following communities |
| DELETE | api/cmis/students/{followerId}/followingCommunities/{communityId} | delete community from students following communities |

## requests

### GET -> "api/cmis/communities/{id}", "api/cmis/users/{id}/communities"
```json

```

### GET -> /api/cmis/communities
```json

```

### GET -> /api/cmis/communities/{id}/image

```json
    //base64 string
```


### PUT -> /api/cmis/communities/{id}
```json
{
    "info" : "sehpa tepsi tahta ve kavun."
}
```

### PUT -> /api/cmis/communities/{id}/updateImage
```json
{
    //community fields
}
```



### DELETE -> /api/cmis/communities/{id} 
```json

```

### DELETE -> /api/cmis/communities
```json

```

### GET -> api/cmis/students/{followerId}/followingCommunities
```json

```

### POST -> api/cmis/students/{followerId}/followingCommunities
```json
{
    "id" : "3"
}
```

### DELETE -> api/cmis/students/{followerId}/followingCommunities/{communityId}
```json

```

## responses

### GET -> "api/cmis/communities/{id}", "api/cmis/users/{id}/communities"
```json
{
    "id": 7,
    "user": {
        "id": 7,
        "firstName": "Karbon",
        "lastName": null,
        "username": "karbon@gtu.edu.tr",
        "email": "karbon@gtu.edu.tr",
        "password": "$2a$10$5vAhecYImTYqLGhdgW3w9OgMKzQ17RjERrrSDAKqV6ytMa/I9pcXS",
        "roles": [
            {
                "id": 3,
                "name": "ROLE_COMMUNITY"
            }
        ]
    },
    "info": null,
    "followers": []
}
```

### GET -> /api/cmis/communities
```json
[
    {
        "id": 3,
        "user": {
            "id": 3,
            "firstName": "Armut Sehpa Topluluğu",
            "lastName": null,
            "username": "armutsehpa@gtu.edu.tr",
            "email": "armutsehpa@gtu.edu.tr",
            "password": "$2a$10$OoVkGN8sYGSOEXZwhLHtrO0t3/963MYNb15IAc5QxKZ04EaZ3kkga",
            "roles": [
                {
                    "id": 3,
                    "name": "ROLE_COMMUNITY"
                }
            ]
        },
        "info": null,
        "followers": [
            {
                "id": 2,
                "user": {
                    "id": 2,
                    "firstName": "Elma",
                    "lastName": "Tekne",
                    "username": "e.tekne2021@gtu.edu.tr",
                    "email": "e.tekne2021@gtu.edu.tr",
                    "password": "$2a$10$klptaGaOpK871Rd1OyWHDeJXx6zD4eb7qXeMjZahtVoPl8jjkAHym",
                    "roles": [
                        {
                            "id": 1,
                            "name": "ROLE_STUDENT"
                        }
                    ]
                },
                "bookMarkedPosts": []
            }
        ]
    },
    {
        "id": 7,
        "user": {
            "id": 7,
            "firstName": "Karbon",
            "lastName": null,
            "username": "karbon@gtu.edu.tr",
            "email": "karbon@gtu.edu.tr",
            "password": "$2a$10$5vAhecYImTYqLGhdgW3w9OgMKzQ17RjERrrSDAKqV6ytMa/I9pcXS",
            "roles": [
                {
                    "id": 3,
                    "name": "ROLE_COMMUNITY"
                }
            ]
        },
        "info": null,
        "followers": []
    },
    {
        "id": 8,
        "user": {
            "id": 8,
            "firstName": "Gemi",
            "lastName": null,
            "username": "gtopl2019@gtu.edu.tr",
            "email": "gtopl2019@gtu.edu.tr",
            "password": "$2a$10$ADwfjYLI0OG3Fz2l/P2GBe5YETeiMNyjEHfgYbgrEYXTHVfZhvsHm",
            "roles": [
                {
                    "id": 3,
                    "name": "ROLE_COMMUNITY"
                }
            ]
        },
        "info": null,
        "followers": []
    },
    {
        "id": 9,
        "user": {
            "id": 9,
            "firstName": "Bilgisayar Topluluğu",
            "lastName": null,
            "username": "gtubt@gtu.edu.tr",
            "email": "gtubt@gtu.edu.tr",
            "password": "$2a$10$omPaft0HZMO8vx9Qm6tbfuvbV9A8evdm72Imw/loy3J.w9ElVeqCq",
            "roles": [
                {
                    "id": 3,
                    "name": "ROLE_COMMUNITY"
                }
            ]
        },
        "info": null,
        "followers": []
    }
]
```

### PUT -> /api/cmis/communities/{id}
```json
{
    "id": 3,
    "user": {
        "id": 3,
        "firstName": "Armut Sehpa Topluluğu",
        "lastName": null,
        "username": "armutsehpa@gtu.edu.tr",
        "email": "armutsehpa@gtu.edu.tr",
        "password": "$2a$10$OoVkGN8sYGSOEXZwhLHtrO0t3/963MYNb15IAc5QxKZ04EaZ3kkga",
        "roles": [
            {
                "id": 3,
                "name": "ROLE_COMMUNITY"
            }
        ]
    },
    "info": "sehpa tepsi tahta ve kavun.",
    "followers": []
}
```

### DELETE -> /api/cmis/communities/{id} 
```json

```

### DELETE -> /api/cmis/communities
```json

```

### GET -> api/cmis/students/{followerId}/followingCommunities
```json
[
    {
        "id": 3,
        "user": {
            "id": 3,
            "firstName": "Armut Sehpa Topluluğu",
            "lastName": null,
            "username": "armutsehpa@gtu.edu.tr",
            "email": "armutsehpa@gtu.edu.tr",
            "password": "$2a$10$OoVkGN8sYGSOEXZwhLHtrO0t3/963MYNb15IAc5QxKZ04EaZ3kkga",
            "roles": [
                {
                    "id": 3,
                    "name": "ROLE_COMMUNITY"
                }
            ]
        },
        "info": null,
        "followers": [
            {
                "id": 2,
                "user": {
                    "id": 2,
                    "firstName": "Elma",
                    "lastName": "Tekne",
                    "username": "e.tekne2021@gtu.edu.tr",
                    "email": "e.tekne2021@gtu.edu.tr",
                    "password": "$2a$10$klptaGaOpK871Rd1OyWHDeJXx6zD4eb7qXeMjZahtVoPl8jjkAHym",
                    "roles": [
                        {
                            "id": 1,
                            "name": "ROLE_STUDENT"
                        }
                    ]
                },
                "bookMarkedPosts": []
            }
        ]
    }
]
```

### POST -> api/cmis/students/{followerId}/followingCommunities
```json
{
    "id": 3,
    "user": {
        "id": 3,
        "firstName": "Armut Sehpa Topluluğu",
        "lastName": null,
        "username": "armutsehpa@gtu.edu.tr",
        "email": "armutsehpa@gtu.edu.tr",
        "password": "$2a$10$OoVkGN8sYGSOEXZwhLHtrO0t3/963MYNb15IAc5QxKZ04EaZ3kkga",
        "roles": [
            {
                "id": 3,
                "name": "ROLE_COMMUNITY"
            }
        ]
    },
    "info": "sehpa tepsi tahta ve kavun.",
    "followers": [
        {
            "id": 4,
            "user": {
                "id": 4,
                "firstName": "ahmet",
                "lastName": "kahve",
                "username": "kahmet@gtu.edu.tr",
                "email": "kahmet@gtu.edu.tr",
                "password": "$2a$10$GBK2u9YPXiSfTH0GLHoW1u0x/U905Bap4DGCVVpRshWj34kJcaOPO",
                "roles": [
                    {
                        "id": 1,
                        "name": "ROLE_STUDENT"
                    }
                ]
            },
            "bookMarkedPosts": []
        }
    ]
}
```

### DELETE -> api/cmis/students/{followerId}/followingCommunities/{communityId}
```json

```
