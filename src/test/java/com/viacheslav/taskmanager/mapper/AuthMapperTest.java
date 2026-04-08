package com.viacheslav.taskmanager.mapper;

import com.viacheslav.taskmanager.dto.auth.RegisterRequest;
import com.viacheslav.taskmanager.dto.user.UserCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AuthMapperTest {

    @InjectMocks
    private AuthMapperImpl authMapper;

    private RegisterRequest registerRequest;
    private final String encodedPassword = "huy";

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("rawPassword123")
                .confirmPassword("rawPassword123")
                .build();
    }

    @Test
    @DisplayName("Should map all fields correctly")
    void toUserCreateRequest_ShouldMapAllFields() {
        // when
        UserCreateRequest result = authMapper.toUserCreateRequest(registerRequest, encodedPassword);

        // then
        assertThat(result)
                .isNotNull()
                .satisfies(userCreateRequest -> {
                    assertThat(userCreateRequest.username()).isEqualTo("john_doe");
                    assertThat(userCreateRequest.email()).isEqualTo("john@example.com");
                    assertThat(userCreateRequest.password()).isEqualTo(encodedPassword);
                });
    }

//    @Test
//    @DisplayName("Testing AuthMapperImpl")
//    void toUserCreateRequest_ShouldMapAllFields() {
//        //given
//        RegisterRequest registerRequest = RegisterRequest.builder()
//                .username("rassel86")
//                .email("john@example.com")
//                .password("Password123!")
//                .confirmPassword("Password123!")
//                .build();
//
//        //when
//        UserCreateRequest result = authMapper.toUserCreateRequest(registerRequest, );
//
//        //that
//        assertEquals(result.username(), registerRequest.username(), "Username should match");
//        assertEquals(result.email(), registerRequest.email(), "Email should match");
//        assertEquals(result.password(), registerRequest.password(), "Password should match");
//    }
}
