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
		// http
		// 		.authorizeHttpRequests(auth -> auth
		// 				.requestMatchers("/health").permitAll()
		// 				.requestMatchers("/actuator/health/**").permitAll()
		// 				.anyRequest().authenticated())
		// 		.httpBasic(withDefaults())
		// 		.formLogin(withDefaults());

		// // Not strictly required for GET /actuator/health, but avoids surprises if actuator endpoints expand later.
		// http.csrf(csrf -> csrf.ignoringRequestMatchers("/actuator/**"));

		// MVP 2주차까지는 "인증" 범위가 아님: 방 생성/입장/퇴장 흐름을 빠르게 검증하기 위해 전체 permitAll
		http.cors(cors -> {});
		http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
		http.csrf(csrf -> csrf.disable());

		return http.build();
	}
}


