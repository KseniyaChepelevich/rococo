package io.student.rococo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.student.rococo.service.cors.CorsCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.springframework.http.HttpMethod.GET;

@EnableWebSecurity
@Configuration
public class RococoApiConfiguration {

  private final CorsCustomizer corsCustomizer;

  @Autowired
  public RococoApiConfiguration(CorsCustomizer corsCustomizer) {
    this.corsCustomizer = corsCustomizer;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    corsCustomizer.apply(http);
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(customizer ->
            customizer
                .requestMatchers(GET, "/api/session").permitAll()
                .requestMatchers(GET, "/api/artist/**").permitAll()
                .requestMatchers(GET, "/api/museum/**").permitAll()
                .requestMatchers(GET, "/api/painting/**").permitAll()
                .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
    return http.build();
  }

  @Bean
  public ObjectMapper objectMapper(){
    ObjectMapper objectMapper = new ObjectMapper();
    DateFormat df = new SimpleDateFormat("yyyy:MM:dd HH:mm:ssZ");
    objectMapper.setDateFormat(df);
    return objectMapper;
  }


}
