package com.firstgroup.movies.security.domain;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.firstgroup.movies.domain.MemberVO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class CustomUser extends User{
	// MemberVO를 UsersDetails 타입으로 변환하기
	
	private static final long serialVersionUID = 1L;
	
	private MemberVO member;
	// VO 객체 생성
	
	//CustomUser의 매개변수의 다형성
	public CustomUser(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		// username, password, authorities를 받아 부모 객체에게 전달 
	}
	
	public CustomUser(MemberVO vo) {
		super(vo.getId(), vo.getPw(), vo.getAuthList().stream().map(auth -> new SimpleGrantedAuthority(auth.getAuth())).collect(Collectors.toList()));
		log.info(vo.getAuthList().stream().map(auth -> new SimpleGrantedAuthority(auth.getAuth())).collect(Collectors.toList()));
		//AuthVO인스턴스를 GrantedAuthority 객체로 변환해야 되므로 .stream().map() 이용해 처리
		
		this.member = vo;
	}// vo 객체를 받아 vo로 생성

}
