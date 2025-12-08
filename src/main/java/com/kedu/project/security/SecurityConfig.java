// SecurityConfig.java
package com.kedu.project.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                // 인증 없이 접근 가능한 URL
                auth.requestMatchers(
                    "/user/idChack", "/user/nicknameChack", "/user/signup",
                    "/user/login", "/user/pindIdByEmail", "/user/pindPwByEmail",
                    "/file/**", "/alarm/**", "/ws-stomp/**", "/sockjs/**", "/emailCheck/**"
                ).permitAll();
                auth.requestMatchers(HttpMethod.GET, "/board/**").permitAll();
                auth.requestMatchers(HttpMethod.POST, "/board/**").authenticated();
                // 나머지 요청은 모두 인증 필요
                auth.anyRequest().authenticated();
            }); // 익명 접근 차단

        http.addFilterBefore(JWTFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();


        config.addAllowedOrigin("http://10.10.55.103:3000");
        config.addAllowedOrigin("http://10.5.5.4:3000");
        config.addAllowedOrigin("http://192.168.0.6:3000");
        config.addAllowedOrigin("http://10.10.55.80:3000");
        config.addAllowedOrigin("http://10.10.55.89:3000");
        config.addAllowedOrigin("http://10.5.5.4:3001");
        config.addAllowedOrigin("http://10.5.5.5:3000");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
