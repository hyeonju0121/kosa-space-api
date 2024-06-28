package com.mycompany.kosa_space.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.TrainingRoom;
import com.mycompany.kosa_space.dto.request.CreateTrainingRoomRequestDTO;

@Mapper
public interface TrainingRoomDao {
	public int insert(TrainingRoom trainingRoom);
	
	public void update(int trno, CreateTrainingRoomRequestDTO room);
	
	// 강의실 명 존재 여부
	public int selectCntByTrname(int ecno, String trname);
	
	public TrainingRoom selectByTrno(int trno);
	
	public List<TrainingRoom> selectByEcno(int ecno);
	
	public List<TrainingRoom> selectAllRoom();

}
