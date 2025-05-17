package org.example.honorsparkingbe.config;

import java.net.http.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  public HttpClient httpClient(){
    return HttpClient.newHttpClient();
  }
}
