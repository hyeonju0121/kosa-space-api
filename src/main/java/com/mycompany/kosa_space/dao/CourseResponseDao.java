package com.mycompany.kosa_space.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.Course;
import com.mycompany.kosa_space.dto.response.CourseResponseDTO;

@Mapper
public interface CourseResponseDao {

	public Course selectByEcnoAndCname(int ecno, String cname);

	public List<CourseResponseDTO> selectByEcnoAndTrno(int ecno, int trno);
}
