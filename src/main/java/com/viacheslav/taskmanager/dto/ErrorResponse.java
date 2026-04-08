package com.viacheslav.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        List<FieldErrorDetail> errors,
        String path
) {

    public record FieldErrorDetail(
            String field,
            String message,
            Object rejectedValue
    ) {

    }

    public String logAsJson(ObjectMapper mapper) {
        String json = null;

        try {
            json = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert ErrorResponse to JSON");
        }
        return json;
    }
}
