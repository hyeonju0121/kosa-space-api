package com.mycompany.kosa_space.dao;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.Course;

@Mapper
public interface CourseDao {
	public int insert(Course course);
	
	public int selectByCnameAndCstatus(int trno, String cstatus, String cname);
}
