package com.kedu.project.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.kedu.project.security.jwt.JWTFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JWTFilter JWTFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http // security 필터 역할
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // JWT 토큰 기반 인증을 사용하는 서버에서 불필요한 Session 생성 및 검증 로직을 차단하여 자원 낭비 방지

        http.authorizeHttpRequests(auth -> { // 인증.인가 처리 부분
            // auth.requestMatchers("/members/**").authenticated()
            //         .requestMatchers("/admin/**").hasRole("ADMIN")
                    auth.anyRequest().permitAll();
        });

        http.addFilterBefore(JWTFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		config.addAllowedOrigin("http://10.10.55.103:3000"); // 모든 출처 -> 나중에 서버맞춰지면 설정
		config.addAllowedOrigin("http://10.5.5.4:3000"); // 모든 출처 -> 나중에 서버맞춰지면 설정
		config.addAllowedOrigin("http://192.168.0.10:3000");
		config.addAllowedOrigin("http://10.5.5.0:3000");
        config.addAllowedMethod("*");
		config.addAllowedHeader("*");
        config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

}
