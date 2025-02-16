package org.example.honorsparkingbe.util.converter.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import java.util.List;

public class JsonIntegerListConverter implements AttributeConverter<List<Integer>, String> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(List<Integer> attribute) {
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error converting list to JSON", e);
    }
  }

  @Override
  public List<Integer> convertToEntityAttribute(String dbData) {
    try {
      return objectMapper.readValue(dbData, new TypeReference<List<Integer>>() {
      });
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error converting JSON to list", e);
    }
  }
}
