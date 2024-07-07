package com.mycompany.kosa_space.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.AttendanceNotes;

@Mapper
public interface AttendanceNotesDao {
	
	// 사유 등록
	public int insert(AttendanceNotes reason);
	
	// mid 와 adate 에 해당하는 사유 단건 조회
	public AttendanceNotes selectByMidAndAdate(String mid, Date adate);

	// 사유 수정
	public void update(AttendanceNotes reason);
}
