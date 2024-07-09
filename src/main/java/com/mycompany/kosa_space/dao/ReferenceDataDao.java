package com.mycompany.kosa_space.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.ReferenceData;
import com.mycompany.kosa_space.dto.response.DailyNoteDetailResponseDTO;

@Mapper
public interface ReferenceDataDao {
	
	public int insert(ReferenceData dailyNote);
	
	// 교육생&해당 주차별 상세조회 기능
	public List<DailyNoteDetailResponseDTO> selectByMidAndRefWeek(
			String mid, String refweek);

}
