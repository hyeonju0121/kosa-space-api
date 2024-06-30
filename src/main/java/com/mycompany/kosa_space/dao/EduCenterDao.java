package com.mycompany.kosa_space.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.EduCenter;
import com.mycompany.kosa_space.dto.request.CreateEduCenterRequestDTO;

@Mapper
public interface EduCenterDao {
	// 교육장 등록
	public int insert(EduCenter center);
	
	// 교육장 수정
	public void update(int ecno, CreateEduCenterRequestDTO request);
	
	// ecno 기준으로 교육장 정보 조회
	public EduCenter selectByEcno(int ecno);
	
	// 교육장 전체 조회
	public List<EduCenter> selectAllCenter();
	
	// 교육장 이름을 기준으로 교육장 정보 조회 
	public EduCenter selectByEcname(String ecname);
	
}
