package com.mycompany.kosa_space.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.Course;
import com.mycompany.kosa_space.dto.Pager;
import com.mycompany.kosa_space.dto.request.CourseParameterRequestDTO;
import com.mycompany.kosa_space.dto.response.CourseResponseDTO;

@Mapper
public interface CourseResponseDao {

	public Course selectByEcnoAndCname(int ecno, String cname);

	public List<CourseResponseDTO> selectByEcnoAndTrno(int ecno, int trno);

	public CourseResponseDTO selectByCno(int cno);
	
	public int selectCntlistByParameter(CourseParameterRequestDTO params);
	
	public List<CourseResponseDTO> listByParameter(
			CourseParameterRequestDTO params, Pager pager);
	
	public List<String> listByEcname(String ecname);
	
	public List<String> listInProgressByEcname(String ecname);
	
	public List<String> listCprofessorByEcname(String ecname);
	
	public CourseResponseDTO listByEcnameAndTrno(String ecname, int trno);
	
	public List<CourseResponseDTO> listByEcnameAndCstatus(String ecname);
	
	public List<CourseResponseDTO> selectAllList();
	
	public List<CourseResponseDTO> selectAllListByEcno(int ecno);
	
	public CourseResponseDTO selectAllListByCname(String cname);
}
