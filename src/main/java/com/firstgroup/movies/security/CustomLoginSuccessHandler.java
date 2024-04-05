package com.firstgroup.movies.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;import org.springframework.security.authentication.jaas.AuthorityGranter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import lombok.extern.log4j.Log4j2;


@Log4j2

public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)throws IOException,ServletException{
		
		log.warn("login success");
		// 콘솔에 성공 출력
		List<String> roleNames=new ArrayList<>();
		// 리스트(문자열) 권한리스트 등록
		auth.getAuthorities().forEach(authority->{
		
			
			roleNames.add(authority.getAuthority());
			// security-context.xml에 있는 hasRole 값을 가져와 roleNames에 넣음
			// ROLE_MEMBER , ROLE_ADMIN 가 있음
						
		});
		log.warn("ADMIN");
		if(roleNames.contains("ROLE_ADMIN")) {
			response.sendRedirect("/home");
			return;
		} //ROLE_ADMIN이면 response(응답을 /home으로 보냄)
		response.sendRedirect("/home");
	}// 권한이 없으면"http://localhost/home"로 보냄
	
	

}
