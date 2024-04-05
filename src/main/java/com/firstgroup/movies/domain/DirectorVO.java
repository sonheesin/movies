package com.firstgroup.movies.domain;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat; 
//<input>으로 입력한 문자열을 그대로 쓸 수는 없고 LocalDateTime 타입으로 변환해야함
//날짜값 변환 어노테이션

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class DirectorVO { //감독 객체
	private long dirBno; 
	private String dirName;
	private int age;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date dirBirth;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date dirDead;
	private List<ImgVO> imgList;
}
