package com.viacheslav.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

//    @Column(nullable = false)
//    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Builder.Default
    @OneToMany(mappedBy = "author")
    private List<Task> createdTasks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "assignee")
    private List<Task> assignedTasks = new ArrayList<>();

}
