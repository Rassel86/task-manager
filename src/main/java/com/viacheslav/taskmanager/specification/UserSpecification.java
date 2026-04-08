package com.viacheslav.taskmanager.specification;

import com.viacheslav.taskmanager.dto.user.UserFilterRequest;
import com.viacheslav.taskmanager.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserSpecification {

    public Specification<User> getUsersSpecification(UserFilterRequest filter) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.createdAt() != null) {
                predicates.add(cb.equal(
                        root.get("createdAt"),
                        filter.createdAt()));
            }

            if (filter.lessCreatedAt() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("createdAt"),
                        filter.lessCreatedAt())
                );
            }

            if (filter.greaterCreatedAt() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        filter.greaterCreatedAt())
                );
            }

            if (filter.firstName() != null && !filter.firstName().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("firstName")),
                        "%" + filter.firstName().toLowerCase() + "%")
                );
            }

            if (filter.lastName() != null && !filter.lastName().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("lastName")),
                        "%" + filter.lastName().toLowerCase() + "%")
                );
            }

            if (filter.email() != null && !filter.email().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("email")),
                        "%" + filter.email().toLowerCase() + "%")
                );
            }

            if (filter.username() != null && !filter.username().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("username")),
                        "%" + filter.username().toLowerCase() + "%")
                );
            }

            if (filter.role() != null) {
                predicates.add(cb.equal(
                        root.get("role"),
                        filter.role())
                );
            }

            if (filter.enabled() != null) {
                predicates.add(cb.equal(
                        root.get("enabled"),
                        filter.enabled())
                );
            }

            return cb.and(
                    predicates.toArray(new Predicate[0]));
        };
    }
}
