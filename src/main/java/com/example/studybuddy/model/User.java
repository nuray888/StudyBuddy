package com.example.studybuddy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String userName;
    //    private MessageStatus status;
//    private LocalDate lastSeen;

    @Email
    @Column(unique = true)
    private String email;
    private String password;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "user_authorities",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
    private Set<Authority> authorities = new HashSet<>();


    public User(String username, String password) {
        this.userName = username;
        this.password = password;
    }


//    @Column(nullable = true)
//    private int avatar;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();


//    @OneToMany(mappedBy = "user1")
//    private List<ChatRoom> chatsAsUser1;
//
//    @OneToMany(mappedBy = "user2")
//    private List<ChatRoom> chatsAsUser2;


}
