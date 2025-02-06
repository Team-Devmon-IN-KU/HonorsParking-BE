package org.example.honorsparkingbe.dto.error;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ErrorResponse {
    private Integer code;
    private String message;
}

