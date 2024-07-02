package com.mycompany.kosa_space.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mycompany.kosa_space.dao.MemberDao;
import com.mycompany.kosa_space.dto.Member;
import com.mycompany.kosa_space.security.JwtProvider;
import com.mycompany.kosa_space.security.KosaUserDetails;
import com.mycompany.kosa_space.security.KosaUserDetailsService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {
	@Autowired
	MemberDao memberDao;
	
	@Autowired
	private JwtProvider jwtProvider;
	
	@Autowired
	private KosaUserDetailsService userDetailsService;
	
	// ---------------------------------------------------------------------------
	// 회원가입
	public void createMember(Member member) {
		/* 파라미터의 member 객체에는
		 * 아이디, 비밀번호, 이름, 휴대폰 번호, 이메일 등의 정보가 담겨있다.
		 * 따라서 mrole, menable의 값을 셋팅해주면 된다.
		 * '회원가입' 부분에 있어서 '운영진' 로그인 페이지와 '교육생' 로그인 페이지를 분리시킬지 고려해야 함.
		 * 	--> 이에 따라 권한 부여에 관한 비즈니스 로직이 변경 될 것임. 
		 */
		
		// 비밀번호를 DB에 암호화하여 저장한다.
		// 따라서 비밀번호를 암호화하기 위한 인코더 변수 선언과 초기화
		PasswordEncoder passwordEncoder = PasswordEncoderFactories
				.createDelegatingPasswordEncoder();
		
		// 가입할 회원의 비밀번호에 인코더를 적용하여 세팅
		member.setMpassword(passwordEncoder.encode(member.getMpassword()));
		
		/* 
		 	가입하는 회원에 대한 권한 부여
		 	회원가입은 '운영진'만 가능하도록 설계함.
		 	따라서 '운영진'의 권한을 얻으려면 앞 글자는 'kosa'를 붙여야 함.
		 	Ex) kosa001
		 */
		
		// 회원가입은 운영진, 강사만 가능하다.
		if(member.getMid().substring(0,4).equals("kosa")) {
			member.setMrole("ROLE_ADMIN");			
		} else {
			member.setMrole("ROLE_MANAGER");
		}
		
		// 아이디 활성화
		member.setMenable(true);
		
		// 회원가입 처리 (Dao의 추상 메소드를 사용하여 DB에 값을 저장) 
		memberDao.insert(member);
	}
	
	// ---------------------------------------------------------------------------
	// 로그인
	public Map<String, String> memberCheck(String mid, String mpassword) {
		log.info("userCheck 실행");
		// # 사용자 상세 정보 얻기 (KosaUserDetailsService에서 유저의 아이디가 없다면 예외를 발생시키도록 했음.)
		// 1. loadUserByUsername(mid)는 DB로부터 사용자가 입력한 mid에 해당하는 Member 객체를 가져온다.
		// 2. 강제 형변환을 사용하여 userDetails 객체에 Member 객체의 값을 저장한다. 
		KosaUserDetails userDetails = (KosaUserDetails) userDetailsService.loadUserByUsername(mid);
		
		// 비밀번호 체크 ( DB에서 '{알고리즘} 암호화된 비밀번호' 형식을 기억해보자. )
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		
		// 매개값 첫번째 : 사용자에게 입력받은 Password
		// 매개값 두번째 : DB에서 암호화된 Password
		boolean checkResult = passwordEncoder.matches(
				mpassword, userDetails.getMember().getMpassword());
		
		// Spring 시큐리티 인증 처리
		if(checkResult) {
			Authentication authentication =
					new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		
		// 응답 생성 (Response)
		Map<String, String> map = new HashMap<>();
		
		if(checkResult) {
			// true일 경우 AccessToken을 생성한다.
			// JwtProvider의 createAccessToken메소드를 사용하여 토큰 생성 후 accessToken 변수에 저장
			String accessToken = jwtProvider.createAccessToken(mid, userDetails.getMember().getMrole());
			
			// JSON 응답 구성
			map.put("result", "success");
			map.put("mid", mid);
			map.put("accessToken", accessToken);
		} else {
			map.put("result", "fail");
		}
		
		return map;
	}
	
	// ---------------------------------------------------------------------------
	// 아이디 찾기
	public String readMemberId(String mphone, String memail) {
		log.info("readMemberId 실행");
		// 핸드폰 번호는 중복될 수 없다는 전제.
		// --> 회원가입시 구현하진 않았음. 팀 논의 후 수정 결정
		log.info("mphone: " + mphone); // 확인
		log.info("memail: " + memail); // 확인
		
		if(memberDao.selectByMphone(mphone) == null) {
			log.info("휴대폰 번호가 등록되어있지 않음");
			return "none";
		} else {
			log.info("memberDao.selectByMphone(mphone) = " + memberDao.selectByMphone(mphone).toString());
			log.info("member.getMid() = " + memberDao.selectByMphone(mphone).getMid());			
			log.info("member.getMid() = " + memberDao.selectByMphone(mphone).getMemail());
			Member member = memberDao.selectByMphone(mphone);
			if(memail.equals(member.getMemail())) {
				return member.getMid();
			} else {
				log.info("휴대폰 번호는 존재하나, 이메일이 일치하지 않음");
				return "none";
			}
		}
	}
	
	// ---------------------------------------------------------------------------
	// 임시 비밀번호 발급을 위한 
	// 비밀번호 찾기 (--> 복호화를 할 수 없어서 '12345'로 비밀번호 초기화)
	public String readMemberPassword(String mname, String mid, String memail) {
		log.info("비밀번호 찾기 service 실행");
		
		Member member = memberDao.selectByMid(mid);
		
		if(member == null) {
			return "존재하지 않는 아이디입니다.";
		} else if(member.getMname().equals(mname) && member.getMemail().equals(memail)) {
			PasswordEncoder passwordEncoder = 
					PasswordEncoderFactories.createDelegatingPasswordEncoder();
			member.setMpassword(passwordEncoder.encode("12345"));
			return "12345";
		} else {
			return "회원의 정보가 일치하지 않습니다. 다시 확인해주세요.";
		}
	}
	
	// (운영진) 회원정보수정
	public void updateAdmin(Member modify) {
		log.info("updateAdmin 서비스 실행");
		log.info("modify : " + modify.toString());
		
		Member member = memberDao.selectByMid(modify.getMid());

		PasswordEncoder passwordEncoder = PasswordEncoderFactories
				.createDelegatingPasswordEncoder();
		
		member.setMpassword(passwordEncoder.encode(modify.getMpassword()));
		member.setMemail(modify.getMemail());
		member.setMphone(modify.getMphone());
		
		memberDao.updateAdmin(member);
	}
	
	// (공통) 회원탈퇴 (비활성화)
	public void inActivationMember(String mid) {
		Member member = memberDao.selectByMid(mid);
		member.setMenable(false);
		memberDao.inActivation(member);
	}
	
	


	


//	// 교육장, 교육과정에 따른 교육생 목록 조회
//	public List<Member> listTrainee(String ecname, String cname) {
//		// TODO Auto-generated method stub
//		return null;
//	}
}
