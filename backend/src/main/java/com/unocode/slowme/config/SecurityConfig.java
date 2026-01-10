package com.unocode.slowme.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/actuator/health/**").permitAll()
						.anyRequest().authenticated())
				.httpBasic(withDefaults())
				.formLogin(withDefaults());

		// Not strictly required for GET /actuator/health, but avoids surprises if actuator endpoints expand later.
		http.csrf(csrf -> csrf.ignoringRequestMatchers("/actuator/**"));

		return http.build();
	}
}


