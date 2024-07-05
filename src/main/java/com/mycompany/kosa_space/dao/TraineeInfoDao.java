package com.mycompany.kosa_space.dao;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.TraineeInfo;
import com.mycompany.kosa_space.dto.request.UpdateTraineeRequestDto;
import com.mycompany.kosa_space.dto.response.TraineeResponseDto;

@Mapper
public interface TraineeInfoDao {
	// 교육과정을 검색하여 학생들의 수를 반환
	public int readTraineeCnt(String cname);
	// 교육생의 정보 DB에 저장
	public void insert(TraineeInfo traineeInfo);
	// mid를 검색하여 TraineeInfo 객체 반환
	public TraineeInfo selectByMid(String mid);
	// 교육생 단건 조회
	public TraineeResponseDto detailInfo(String mid);
	// (교육생) 운영진 -> 교육생 수정
	public void updateTrainee(UpdateTraineeRequestDto request);
}
