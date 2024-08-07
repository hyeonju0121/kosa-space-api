package com.mycompany.kosa_space.dao;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.Member;
import com.mycompany.kosa_space.dto.request.UpdateTraineeRequestDto;

@Mapper
public interface MemberDao {
	// (운영진) 회원가입
	public void insert(Member member);
	// 시큐리티를 이용한 아이디 조회
	public Member selectByMid(String username);
	// (공통) 아이디 찾기 (휴대폰 번호를 이용하여 회원의 정보를 가져옴)
	public Member selectByMphone(String mphone);
	// (운영진) 회원정보수정
	public void updateAdmin(Member member);
	// (공통) 회원 비활성화
	public void inActivation(Member member);
	
	// 이름으로 회원 조회
	public Member selectByMname(String mname);
	// (교육생) 운영진 -> 교육생 수정
	public void updateTrainee(UpdateTraineeRequestDto request);
	
	// 중복검사
	// 아이디
	public String checkMid(String mid);
	// 이메일
	public String checkMemail(String memail);
	// 휴대폰 번호	
	public String checkMphone(String mphone);
	
	
}
