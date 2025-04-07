package org.example.honorsparkingbe.unit.util;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
    "spring.profiles.active=test",
    "spring.data.redis.port=6379",        // ✅ 강제 설정
    "spring.data.redis.host=localhost"    // ✅ 호스트도 같이 지정
})
@AutoConfigureMockMvc
public class ApiKeyAuthFilterUnitTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void API_KEY가_정상적으로_인증되면_200_OK() throws Exception {
    String requestBody = objectMapper.writeValueAsString(Map.of("inoutList", List.of()));

    mockMvc.perform(post("/api/v1/sync/inout")
            .header("X-API-KEY", "valid-api-key")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody).with(csrf())
        )
        .andExpect(status().isOk());
  }

  @Test
  void API_KEY가_없으면_401_UNAUTHORIZED() throws Exception {
    String requestBody = objectMapper.writeValueAsString(Map.of("inoutList", List.of()));

    mockMvc.perform(post("/api/v1/sync/inout") // ❌ API Key 없음
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody).with(csrf())
        )
        .andExpect(status().isUnauthorized());
  }

  @Test
  void API_KEY가_잘못되면_401_UNAUTHORIZED() throws Exception {
    String requestBody = objectMapper.writeValueAsString(Map.of("inoutList", List.of()));

    mockMvc.perform(post("/api/v1/sync/inout")
            .header("X-API-KEY", "wrongKey") // ❌ 잘못된 API Key
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody).with(csrf())
        )
        .andExpect(status().isUnauthorized());
  }
}
