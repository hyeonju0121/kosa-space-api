package com.mycompany.kosa_space.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.TrainingRoom;
import com.mycompany.kosa_space.dto.request.CreateTrainingRoomRequestDTO;

@Mapper
public interface TrainingRoomDao {
	public int insert(TrainingRoom trainingRoom);
	
	public void update(int trno, CreateTrainingRoomRequestDTO room);
	
	public void updateByTrenable(int trno, boolean trenable);
	
	public void delete();
	
	public void deleteByTrno(int trno);
	
	// 강의실 명 존재 여부
	public int selectCntByTrname(int ecno, String trname);
	
	public TrainingRoom selectByTrname(String trname);
	
	public TrainingRoom selectByTrno(int trno);
	
	public List<TrainingRoom> selectByEcno(int ecno);
	
	public List<TrainingRoom> selectAllRoom();
	
	// 저장된 trno 찾기
	public int selectByEcnoAndTrname(int ecno, String trname);
	
	public List<TrainingRoom> selectByEcnoAndTrenable(int ecno, boolean trenable);

}
