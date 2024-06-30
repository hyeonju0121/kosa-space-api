package com.mycompany.kosa_space.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.EduAttach;

@Mapper
public interface EduAttachDao {
	public int insertEduCenter(EduAttach attach);
	
	public int insertEduCenterNewAttach(EduAttach attach);
	
	public int deleteEduCenterByEano(int eano);
	
	// ecno 기준으로 첨부파일 전체 조회
	public List<EduAttach> selectEduCenterByEcno(int ecno);
	
	// eano 기준으로 첨부파일 수정
	public void updateEduCenterByEano(int eano, EduAttach attach);

}
