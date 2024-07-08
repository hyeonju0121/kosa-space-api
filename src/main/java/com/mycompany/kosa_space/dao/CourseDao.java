package com.mycompany.kosa_space.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.Course;
import com.mycompany.kosa_space.dto.response.DashBoardResponseDTO;

@Mapper
public interface CourseDao {
	public int insert(Course course);
	
	public void update(int cno, Course course);
	
	public int selectByCnameAndCstatus(int trno, String cstatus, String cname);

	public Course selectByCno(int cno);
	
	public Course readCourse(String cname);
	
	// 대시보드 조회
	public List<DashBoardResponseDTO> totalCountByEcname(String ecname);
	
	// ecname 기준으로 현재 진행중인 교육과정 조회
	public List<DashBoardResponseDTO> inProgressCountByEcname(String ecname);
	
	// ecname 기준으로 현재 진행예정인 교육과정 조회
		public List<DashBoardResponseDTO> scheduledCountByEcname(String ecname);
	
	// ecname 기준으로 진행완료된 교육과정 조회
	public List<DashBoardResponseDTO> completedCountByEcname(String ecname);
}
