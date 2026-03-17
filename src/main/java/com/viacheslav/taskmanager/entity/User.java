package com.viacheslav.taskmanager.entity;

import com.viacheslav.taskmanager.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.ZonedDateTime;
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

    @Column(nullable = false, length = 68)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private ZonedDateTime created_at;

    @Column(name = "updated_at")
    @LastModifiedDate
    private ZonedDateTime updated_at;

    @OneToMany(mappedBy = "owner")
    List<Project> projects = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "author")
    private List<Task> createdTasks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "assignee")
    private List<Task> assignedTasks = new ArrayList<>();

}
