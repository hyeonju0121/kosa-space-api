package com.mycompany.kosa_space.dao;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.EduCenter;

@Mapper
public interface EduCenterDao {
	// 교육장 등록
	public int insert(EduCenter center);
	
	// 교육장 이름을 기준으로 교육장 정보 조회 
	public EduCenter selectByEcname(String ecname);
	
}
