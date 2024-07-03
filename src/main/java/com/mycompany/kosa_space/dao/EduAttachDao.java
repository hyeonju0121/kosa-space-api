package com.mycompany.kosa_space.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.EduAttach;

@Mapper
public interface EduAttachDao {
	public int insertEduCenter(EduAttach attach);
	
	public int insertEduCenterNewAttach(EduAttach attach);
	
	public int insertTrainingRoomNewAttach(EduAttach attach);
	
	public int insertCourseNewAttach(EduAttach attach);
	
	public int insertTrainingRoom(EduAttach attach);
	
	public int insertCourse(EduAttach attach);
	
	public int deleteByEano(int eano);
	
	public int deleteEduCenterByEcno(int ecno);
	
	public int deleteTrainingRoomByTrno(int trno);
	
	// eano 기준으로 단건 조회
	public EduAttach selectByEano(int eano);
	
	// ecno 기준으로 첨부파일 전체 조회
	public List<EduAttach> selectEduCenterByEcno(int ecno);
	
	// trno 기준으로 첨부파일 전체 조회
	public List<EduAttach> selectTrainingRoomByTrno(int trno);
	
	// cno 기준으로 첨부파일 전체 조회
	public List<EduAttach> selectCourseByCno(int cno);
	
	// eano 기준으로 첨부파일 조회
	public EduAttach selectEduCenterByEano(int eano);
	public EduAttach selectCourseByEano(int eano);
	public EduAttach selectTrainingRoomByEano(int eano);
	
	// eano 기준으로 첨부파일 수정
	public void updateByEano(int eano, EduAttach attach);

	
}
