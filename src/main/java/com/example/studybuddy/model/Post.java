package com.example.studybuddy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;


import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 4, message = "Post name must contain at least 4 character")
    @Column(nullable = false)
    private String topic;
    @Column(name = "sub_topic")
    private String subTopic;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable =false)
    private Date createDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;



    @PrePersist
    protected void onCreate() {
        createDate = new Date(); // Set the current date
    }


}
