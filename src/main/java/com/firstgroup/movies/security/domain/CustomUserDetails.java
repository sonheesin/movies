package com.firstgroup.movies.security.domain;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

@Getter
public class CustomUserDetails implements UserDetails{

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() { //계정이 가지고 있는 권한 목록을 리턴
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassword() { //계정의 비밀번호를 리턴
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsername() { //	계정의 이름을 리턴
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAccountNonExpired() { //계정이 만료되지 않았는지를 리턴(true: 만료되지 않음)

		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() { //계정이 잠겨있지 않은지를 리턴(true: 계정이 잠겨있지 않음)
		
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() { //계정의 비밀번호가 만료되지 않았는지를 리턴(true: 패스워드가 만료되지 않음)
		
		return true;
	}

	@Override
	public boolean isEnabled() { //계정이 사용 가능한지를 리턴합니다.(true: 사용 가능한 계정임) 
		return true;
	}
	
}
