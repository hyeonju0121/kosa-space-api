package com.mycompany.kosa_space.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.Attendance;
import com.mycompany.kosa_space.dto.response.TraineeApproveAttendanceListResponseDTO;
import com.mycompany.kosa_space.dto.response.TraineeAttendanceListResponseDTO;
import com.mycompany.kosa_space.dto.response.UserAttendanceTimeInfoResponseDTO;

@Mapper
public interface AttendanceDao {
	// (운영진) 교육생 활성화 기능
	public int active(Attendance attendance);
	
	// adate 기준으로 count 조사
	public int selectCntByAdate(Date adate);
	
	public Attendance selectByMid(String mid);
	
	public Attendance selectByMidAndAdate(String mid, Date adate);
	
	public UserAttendanceTimeInfoResponseDTO selectUserInfoByMidAndAdate(String mid, Date date);
	
	// (교육생) 입실 기능
	public void checkin(Attendance attendance);
	
	// (교육생) 퇴실 기능
	public void checkout(Attendance attendance);
	
	// (운영진) 교육생 출결 승인 기능 
	public void approveAttendance(Attendance attendance);
	
	// 교육생 이름으로 모든 출결 현황 조회
	public List<Attendance> selectTotalAttendanceByMid(String mid);
	
	// startdate ~ enddate 에 해당하는 교육생의 출결 현황 조회
	public List<Attendance> selectTotalAttendanceByMidAndAdate(String mid,
			String startdate, String enddate);
	
	// cno 에 해당하는 현재 날짜의 교육생 전체 출결 정보 가져오기
	public List<Attendance> selectAttendanceInfoByAdateAndCno(
			Date adate, int cno);
	
	public List<TraineeAttendanceListResponseDTO> selectAttendanceList(
			int cno, Date adate);
	
	public List<TraineeApproveAttendanceListResponseDTO> selectAttendanceApproveList(
			int cno, Date adate);
}
