package com.mycompany.kosa_space.dao;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.Attendance;

@Mapper
public interface AttendanceDao {
	public Attendance selectByMid(String mid);
	
	public int insert(Attendance attendance);
}
