package com.mycompany.kosa_space.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.ReferenceData;
import com.mycompany.kosa_space.dto.request.DailyNoteRequestDTO;
import com.mycompany.kosa_space.dto.response.DailyNoteDetailResponseDTO;

@Mapper
public interface ReferenceDataDao {
	
	public int insert(ReferenceData dailyNote);
	
	public void update(int refno, ReferenceData dailyNote);
	
	public ReferenceData selectByRefno(int refno);
	
	public ReferenceData selectByMidAndRefdate(String mid, Date refdate);
	
	public DailyNoteRequestDTO selectInfoByRefno(int refno);
	
	// 교육생&해당 주차별 상세조회 기능
	public List<DailyNoteDetailResponseDTO> selectByMidAndRefWeek(
			String mid, String refweek);
	
	
	public void delete(int refno);
	
}
