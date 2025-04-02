package org.example.honorsparkingbe.unit.util;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "api.key=valid-api-key")
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
            .content(requestBody)
        )
        .andExpect(status().isOk());
  }

  @Test
  void API_KEY가_없으면_401_UNAUTHORIZED() throws Exception {
    mockMvc.perform(get("/api/v1/sync/inout")) // ❌ API Key 없음
        .andExpect(status().isUnauthorized());
  }

  @Test
  void API_KEY가_잘못되면_401_UNAUTHORIZED() throws Exception {
    mockMvc.perform(get("/api/v1/sync/inout")
            .header("X-API-KEY", "wrong-key")) // ❌ 잘못된 API Key
        .andExpect(status().isUnauthorized());
  }
}
