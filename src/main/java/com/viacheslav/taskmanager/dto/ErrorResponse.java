package com.viacheslav.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
        int status,
        String error,
        String message,
        List<FieldErrorDetail> errors,
        String path
) {

    private static final Logger log = LoggerFactory.getLogger(ErrorResponse.class);

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
