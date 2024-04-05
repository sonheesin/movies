package com.firstgroup.movies.domain;


import lombok.Data;

@Data
public class AuthVO { //인증 객체

  private String id; //member 아이디 fk
  private String auth;
  
}
