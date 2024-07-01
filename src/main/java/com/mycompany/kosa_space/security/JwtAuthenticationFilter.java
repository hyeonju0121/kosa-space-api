package com.mycompany.kosa_space.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter { // 이해 X 그냥 OncePerRequestFilter를 상속받아 정의해야함

//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//			throws IOException, ServletException {
//		// AccessToken 얻기
//		String accessToken = null;
//		
//		HttpServletRequest httpServletRequest = (HttpServletRequest)request; // 이 request를 가지고 GetHeader라는 메소드를 사용할 수 없다. 따라서 타입변환을 해주는 것이다.
//		String headerValue = httpServletRequest.getHeader("Authorization");
//		if(headerValue != null && headerValue.startsWith("Bearer")) {
//			accessToken = headerValue.substring(7);
//			log.info(accessToken);
//		}
//		
//		// AccessToken 유효성 검사
////		Jws<Claims> jws = 
//		
//		// 다음 필터를 실행
//		chain.doFilter(request, response);
//	}
	
	@Autowired
	private JwtProvider jwtProvider;
	
	@Autowired
	private KosaUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
			// AccessToken 얻기
		String accessToken = null;
			
		// 요청 헤더에서 AccessToken 얻기
		HttpServletRequest httpServletRequest = (HttpServletRequest)request; // 이 request를 가지고 GetHeader라는 메소드를 사용할 수 없다. 따라서 타입변환을 해주는 것이다.
		String headerValue = httpServletRequest.getHeader("Authorization");
		if(headerValue != null && headerValue.startsWith("Bearer")) {
			accessToken = headerValue.substring(7);
			log.info(accessToken);
		}
		
		// 6.5
		// 쿼리스트링으로 전달되는 AccessToken 얻기
		// <img src="/board/battach/1?accessToken=xxx" ... > 여기서 xxx값을 얻으려면?
		if(accessToken == null) {
			if(request.getParameter("accessToken") != null) {  // getParameter() 메서드의 활용 방법..
				accessToken = request.getParameter("accessToken");
			}
		}
		
		// accessToken이 null일 경우에는 로그인하지 않았기 때문에 user의 id를 얻을 수 없다.
		if(accessToken != null) {
			// AccessToken 유효성 검사
			Jws<Claims> jws = jwtProvider.validateToken(accessToken);
			if(jws != null) {
				// 유효한 경우
				log.info("AccessToken이 유효함");
				String userId = jwtProvider.getUserId(jws);
				log.info("userId : " + userId);
				
				// userDetail 얻어야함 // @Autowired로 주입받기 // 사용자의 상세정보 얻기.
				UserDetails userDetails = (UserDetails) userDetailsService.loadUserByUsername(userId);
				// 인증 객체 얻기
				Authentication authentication = 
						new UsernamePasswordAuthenticationToken(userId, null, userDetails.getAuthorities());
				// 스프링 시큐리티에 인증 객체 설정
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else {
				// 유효하지 않은 경우
				log.info("AccessToken이 유효하지 않음");
			}
		}
		
		// 다음 필터를 실행
		filterChain.doFilter(request, response);
	}

}
