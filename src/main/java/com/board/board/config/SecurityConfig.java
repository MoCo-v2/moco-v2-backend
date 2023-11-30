package com.board.board.config;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.board.board.exception.CustomAuthenticationEntryPoint;
import com.board.board.service.token.JwTokenService;
import com.board.board.service.user.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	private final CustomUserDetailsService customUserDetailsService;
	private final JwTokenService jwtTokenService;

	@Bean
	public AuthenticationManager authenticationManagerBean(
		AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customUserDetailsService).passwordEncoder(encoder());
	}

	@Bean
	@Order(SecurityProperties.BASIC_AUTH_ORDER)
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// cors 설정
			.cors().and()
			.formLogin().disable()
			.httpBasic().disable()

			// token을 사용하는 방식이기 때문에 csrf를 disable
			.csrf().disable()

			// token 방식이기 때문에 session stateless로 설정
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

			.and()

			.authorizeHttpRequests()
			.requestMatchers("", "/", "/actuator/health").permitAll()
			.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
			.requestMatchers("/admin", "/admin/**").hasRole("MASTER")
			.requestMatchers("/login").permitAll()
			.requestMatchers("/posts/**").authenticated()
			.requestMatchers("/signup", "/login/signup").permitAll()
			.requestMatchers("/id/check", "/name/check").permitAll()
			.anyRequest().authenticated() // 그 외 인증 없이 접근 X
			.and()
			.exceptionHandling()
			.authenticationEntryPoint(new CustomAuthenticationEntryPoint())//인증 예외가 발생했을때 처리
			.and()

			// Filter 등록 및 예외처리
			.addFilterBefore(new JwtVerificationFilter(jwtTokenService), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}