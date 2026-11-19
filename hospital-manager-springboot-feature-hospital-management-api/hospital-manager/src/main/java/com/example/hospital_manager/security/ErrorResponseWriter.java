
package com.example.hospital_manager.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class ErrorResponseWriter {

    public void write(
            HttpServletResponse response,
            HttpStatus status,
            String message
    ) throws IOException {

        String timestamp = LocalDateTime.now().toString();

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                "{\"timestamp\":" + quote(timestamp)
                        + ",\"status\":" + status.value()
                        + ",\"error\":" + quote(status.getReasonPhrase())
                        + ",\"message\":" + quote(message)
                        + "}"
        );
    }

    private String quote(String value) {
        if (value == null) {
            return "null";
        }

        StringBuilder json = new StringBuilder(value.length() + 2).append('"');
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            switch (character) {
                case '"' -> json.append("\\\"");
                case '\\' -> json.append("\\\\");
                case '\b' -> json.append("\\b");
                case '\f' -> json.append("\\f");
                case '\n' -> json.append("\\n");
                case '\r' -> json.append("\\r");
                case '\t' -> json.append("\\t");
                default -> {
                    if (character < 0x20) {
                        json.append(String.format("\\u%04x", (int) character));
                    } else {
                        json.append(character);
                    }
                }
            }
        }
        return json.append('"').toString();
    }
}