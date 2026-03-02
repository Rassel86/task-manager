package com.viacheslav.taskmanager.entity;

import com.viacheslav.taskmanager.entity.enums.TagColor;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tags")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "color", nullable = false)
    private TagColor color = TagColor.GRAY;

    @ManyToMany(mappedBy = "tags")
    private Set<Task> tasks = new HashSet<>();
}
