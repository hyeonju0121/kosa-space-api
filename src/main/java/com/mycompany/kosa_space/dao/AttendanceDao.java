package com.mycompany.kosa_space.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.Attendance;

@Mapper
public interface AttendanceDao {
	// (운영진) 교육생 활성화 기능
	public int active(Attendance attendance);
	
	// adate 기준으로 count 조사
	public int selectCntByAdate(Date adate);
	
	public Attendance selectByMid(String mid);
	
	public int insert(Attendance attendance);
	
	public void updateCheckout(Attendance attendance);
}
