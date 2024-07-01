package com.mycompany.kosa_space.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

/*
	로그인, 회원가입, 아이디 찾기, 비밀번호 찾기
	--> 권한 인증 관련해서 처리
*/

import org.springframework.web.bind.annotation.RestController;

import com.mycompany.kosa_space.dto.Member;
import com.mycompany.kosa_space.service.AuthService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AuthController {
	@Autowired
	AuthService authService;
	
	// 로그인 ------------------------------------
	// 로그인 요청 컨트롤러
	@PostMapping("/login")
	public Map<String, String> login(String mid, String mpassword) {
		log.info("login 컨트롤러 실행");
		log.info("패스워드 : " + mpassword);
		// 응답 생성과 동시에 매핑
		Map<String, String> map = authService.memberCheck(mid, mpassword);
		
		log.info("응답 생성 완료");
		return map;
	}
	
	// 회원 가입 ------------------------------------
	// 운영자 회원가입 요청 컨트롤러
	@PostMapping("/signup")
	public void signUp(Member member) {
		log.info(member.getMid());
		log.info(member.getMid().substring(0,4));
		authService.createMember(member);
		log.info(member.toString());
	}
	
	// (공통) 아이디 찾기 ------------------------------------
	@GetMapping("/find/id")
	public String findId(String mphone, String memail) {
		log.info("findId 리턴값 확인 == " + authService.readMemberId(mphone, memail));
		return authService.readMemberId(mphone, memail);
	}
	
	// (공통) 비밀번호 찾기 ------------------------------------
	@GetMapping("/find/password")
	public String findPassword(String mname, String mid, String memail) {
		log.info("mid : " + mid);
		log.info("mname : " + mname);
		log.info("memail : " + memail);
		return authService.readMemberPassword(mname, mid, memail);
	}
	
	// (운영진) 회원정보수정 ------------------------------------
//	// 1. AccessToken을 이용하여 로그인한 유저의 정보를 얻어 수정하기.
//	@PutMapping("/admin/mypage/info/modify")
//	public void adminUpdate(Member member, Authentication authentication) {
//		log.info("Vue에서 받은 mid : " + member.getMid());
//		log.info("토큰으로 확인한 유저의 mid : " + authentication.getName());
//		
//		authService.updateAdmin(member, authentication.getName());
//	}
	
	// (운영진) 회원정보수정 ------------------------------------
	// 프론트에서 로그인 후 저장되어있는 store 값을 이용하여 수정
	@PutMapping("/admin/mypage/info/modify")
	public void adminUpdate(Member modify) {
		log.info("adminUpdate 컨트롤러 실행");
		authService.updateAdmin(modify);
	}
	
	// (교육생) 회원정보수정 ------------------------------------
	@PutMapping("/trainee/mypage/info/modify")
	public void traineeUpdate(Member modify) {
		log.info("traineeUpdate 컨트롤러 실행");
		
	}
	
	// (공통) 회원탈퇴 (비활성화) --------------------------------
	@PutMapping("/admin/mypage/info/modify/inactivation")
	public void inActivation(String mid) {
		authService.inActivationMember(mid);
	}
	
	
	
//	// (공통) 회원 조회 ---------------------------------------
//	// 교육생 목록 전체 조회
//	@GetMapping("/admin/trainee/list/{ecname}") // 임시 url
//	public List<Member> traineeList(String ecname, String cname) {
//		List<Member> member = authService.listTrainee(ecname, cname);
//		return null;
//	}
}
