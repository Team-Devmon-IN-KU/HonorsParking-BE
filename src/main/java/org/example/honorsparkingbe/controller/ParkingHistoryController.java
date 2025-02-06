package org.example.honorsparkingbe.controller;

import jakarta.validation.Valid;
import org.apache.catalina.connector.Response;
import org.example.honorsparkingbe.dto.error.ErrorResponse;
import org.example.honorsparkingbe.dto.request.ParingHistoryDeloteRequest;
import org.example.honorsparkingbe.dto.response.ParingHistoryDeleteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parking/history")
public class ParkingHistoryController {

    @DeleteMapping
    public ResponseEntity<?> deleteParkingHistory(
            @Valid @RequestBody ParingHistoryDeloteRequest request){
        try{

        }catch (IllegalArgumentException e){
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponse.builder()
                            .code(Response.SC_BAD_REQUEST)
                            .message(e.getMessage())
                    .build());
        }
        return ResponseEntity.ok(new ParingHistoryDeleteResponse());
    }
}
