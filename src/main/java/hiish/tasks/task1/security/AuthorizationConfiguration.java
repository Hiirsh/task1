package hiish.tasks.task1.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AuthorizationConfiguration {

  @Bean
  RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    String hierarchy = "ROLE_ADMIN > ROLE_MODER \n ROLE_MODER > ROLE_USER";
    roleHierarchy.setHierarchy(hierarchy);
    return roleHierarchy;
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.httpBasic();
    http.csrf().disable();
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.authorizeRequests(authorize -> 
      authorize
          .mvcMatchers("/api/v1/account/register").permitAll()
          .mvcMatchers(HttpMethod.PUT, "/api/v1/account/user/*/role/*").hasRole("ADMIN")
          .mvcMatchers(HttpMethod.DELETE/* , "/avi/v1/s3/*" */).hasRole("admin")
          .anyRequest().authenticated()
    );
    return http.build();
  }
}