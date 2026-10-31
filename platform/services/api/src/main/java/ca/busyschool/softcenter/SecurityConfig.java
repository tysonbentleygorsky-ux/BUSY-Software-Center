package ca.busyschool.softcenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
@Configuration public class SecurityConfig {
 @Bean SecurityFilterChain security(HttpSecurity http)throws Exception{
  return http.csrf(csrf->csrf.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->a.requestMatchers("/actuator/health","/api/health").permitAll()
    .requestMatchers(HttpMethod.GET,"/api/catalog/**").permitAll()
    .requestMatchers("/api/admin/**").hasAuthority("SCOPE_IT_ADMIN")
    .requestMatchers("/api/teacher/**").hasAnyAuthority("SCOPE_TEACHER","SCOPE_IT_ADMIN")
    .anyRequest().authenticated())
   .oauth2ResourceServer(o->o.jwt(Customizer.withDefaults())).build();
 }
}
