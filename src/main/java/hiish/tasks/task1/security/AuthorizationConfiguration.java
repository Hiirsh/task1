package hiish.tasks.task1.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class AuthorizationConfiguration {

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.httpBasic();
    http.csrf().disable();
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.authorizeRequests(authorize -> {
      authorize
          .mvcMatchers("/api/v1/account/register").permitAll()
          .mvcMatchers("/api/v1/account/user/*/role/*").access("hasRole('admin')")
          .mvcMatchers(HttpMethod.DELETE, "/avi/v1/s3/**").hasRole("admin")
          .anyRequest().authenticated();
    });
    return http.build();
  }
}