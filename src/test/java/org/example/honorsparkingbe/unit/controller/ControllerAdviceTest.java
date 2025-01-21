package org.example.honorsparkingbe.unit.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.apache.catalina.security.SecurityConfig;
import org.example.honorsparkingbe.Slf4jRestControllerAdvice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.web.bind.annotation.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
        }
)
@Import({Slf4jRestControllerAdvice.class, ControllerAdviceTest.TestController.class})
public class ControllerAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    // 샘플 컨트롤러
    @RestController
    @RequestMapping("/test")
    public static class TestController {
        @GetMapping("/exception")
        public void throwException() {
            throw new RuntimeException("Test Exception");
        }

        @GetMapping("/illegal-argument")
        public void throwIllegalArgumentException() {
            throw new IllegalArgumentException("Illegal Argument");
        }

        @PostMapping("/validation")
        public void validateRequest(@Valid @RequestBody TestRequest request) {
            // 요청 바디 검증
        }
    }

    // 요청 DTO (Validation 테스트용)
    public static class TestRequest {
        @NotBlank(message = "Name must not be blank")
        private String name;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    @DisplayName("Exception 처리 테스트")
    void testHandleException() throws Exception {
        mockMvc.perform(get("/test/exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Test Exception"));
    }

    @Test
    @DisplayName("IllegalArgumentException 처리 테스트")
    void testHandleIllegalArgumentException() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Illegal Argument"));
    }

    @Test
    @DisplayName("ValidationException 처리 테스트")
    void testHandleValidationException() throws Exception {
        String invalidRequestBody = "{ \"name\": \"\" }"; // Name이 비어 있는 잘못된 요청

        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Name must not be blank")));
    }

    @TestConfiguration
    static class SecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            // CSRF 보호 비활성화
            http
                    .csrf((auth) -> auth.disable());
            return http.build();
        }
    }
}