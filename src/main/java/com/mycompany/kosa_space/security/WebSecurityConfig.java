package com.mycompany.kosa_space.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.extern.slf4j.Slf4j;

/*
 * 시큐리티는 Spring에서 '인증', '인가', '보안'에 대한 기능을 제공
 */

@Slf4j
@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {
	// JwtAuthenticationFilter 주입
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
		
	// 인증 필터 체인을 관리 객체로 등록
	@Bean
	public SecurityFilterChain filterChanin(HttpSecurity http) throws Exception {
		// REST API에서 로그인 폼을 제공하지 않으므로 폼을 통한 로그인 인증을 하지 않도록 설정.
		// 로그인 폼은 front-end에서 제공해야한다.
		// Form 인증 방식  // 스프링 부트에서 jsp로 로그인 폼을 이용할 경우 
		/*http.formLogin(config -> config
					.loginPage("/ch17/loginForm")
					.loginProcessUrl("/login")
					.usernameParameter("mid")
					.passwordParameter("mpassword")
					.disable());*/
		
		http.formLogin(config -> config.disable()); // 람다식으로 formLogin을 사용하지 않겠다는 의미. // 우리는 REST API를 사용할 것이기 때문에 로그인 폼을 사용하지 않겠다는 의미.
		// REST API는 따로 로그아웃을 만들 이유가 없다
		// : 클라이언측에서 AccessToken을 갖고 인증하지 않았다면? -> 로그인을 하지 않은 상태.
		// Token으로 확인하고 서비스를 제공하면 된다.
		
		// HttpSession을 사용하지 않도록 설정
		http.sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		// 사이트간 요청 위조 방지 비활성화(GET 방식 이외의 방식 요청은 _csrf 토큰을 요구하기 때문)
		http.csrf(config -> config.disable());
		
		// CORS 설정 (다른 도메인에서 받은 인증 정보(AccessToken)로 요청할 경우 허가)
		http.cors(config -> {});
		
		// JWT로 인증이 되도록 필터를 등록
		// 이미 AccessToken이 발생이 된 상태를 가정. 클라이언트가 AccessToken을 가져왔을 때 필터를 이용하여 검사하겠다는 코드.
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Before를 쓰는 이유 : 정리 // 아이디 패스워드를 필요로하는 필터 이전에 먼저 해주어야 함
		
		return http.build();
	}
	
	// 인증 관리자를 관리 객체로 등록
	/*@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration authenticationConfiguration) throws Exception{
		return authenticationConfiguration.getAuthenticationManager();
	}*/
	
	// 권한 계층을 관리 객체로 등록
	@Bean
	public RoleHierarchy roleHierarchy() {
		RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
		hierarchy.setHierarchy("ROLE_ADMIN > ROLE_MANAGER > ROLE_USER");
		return hierarchy;
	}
	
	// @PreAuthorize 어노테이션의 표현식을 해석하는 객체를 등록하는 코드
	@Bean
    public MethodSecurityExpressionHandler createExpressionHandler() {
      DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy());
        return handler;
    }
	
	// 다른(크로스) 도메인 제한 설정 : 모든 도메인을 허용하는 것은 아니다.
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		// 요청 사이트 제한  // allowedOrigin -> 처음 인증받은 곳을 의미함
//		configuration.addAllowedOrigin("*"); // 모든 도메인을 허가하겠다... // 이렇게 작성하면 안된다!
		configuration.addAllowedOrigin("*"); // 교육에선 이렇게 사용할 것이다. (현재 우리는 도메인이 없다)
		
		// 요청 방식 제한 (우리가 배웠던 방식들은? : GET / POST / PUT / PATCH / DELETE
		// allowedMethod -> 설정한 메소드로 요청만 들어왔을 때 허용하겠다라는 의미
//		configuration.addAllowedMethod("GET");
//		configuration.addAllowedMethod("POST"); 
		// 아스타(*)를 사용하면 모든 방식들을 허용하겠다는 의미이다.
		configuration.addAllowedMethod("*");
		
		// 요청 헤더 제한
//		configuration.addAllowedHeader("헤더이름");
		configuration.addAllowedHeader("*"); // 현실적으로 보안상의 이유로 필요한 Header행만 받는다. // 수업에선 아스타 사용
		
		// 모든 URL에 대해 위 설정을 적용
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // "/**": 모든 URL에 대해서 configuartion을 적용
		
		return source;
	}
}