package com.firstgroup.movies.domain;

import java.util.Date;
import java.util.List;

import lombok.Data;


@Data
public class MemberVO { //멤버 객체
	
	private long membno; //시퀀스 번호
	private String id;
	private String pw;
	private String name;
	private String nickName;
	private String zonecode;
	private String roadAddress;
	private String buildingName;
	private String adress;
	private String phone;
	private Date regdate;
	
	private List<AuthVO> authList; //fk 인증 리스트
	
	private List<ImgVO> imgList; //이미지 리스트

}
