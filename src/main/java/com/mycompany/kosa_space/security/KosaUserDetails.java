package com.mycompany.kosa_space.security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.mycompany.kosa_space.dto.Member;

/*
 *	저번 프로젝트에서는 Authentication 인터페이스와 
 *	Security의 User 클래스를 이용하여 (User 클래스를 상속받아 UserDetails 클래스를 만듦) 현재 로그인한 사용자의 아이디 정보를 불러올 수 있었다.
 *	
 */


public class KosaUserDetails extends User  {
   private Member member;
   
   public KosaUserDetails(Member member, List<GrantedAuthority> authorities) {   
      super(member.getMid(),
    		  member.getMpassword(), 
    		  member.isMenable(),
    		  true, true, true, 
    		  authorities);
      this.member = member;
   }

   public Member getMember() { // 외부에서 쓸 수 있도록 Getter 생성
      return member;
   }
}
