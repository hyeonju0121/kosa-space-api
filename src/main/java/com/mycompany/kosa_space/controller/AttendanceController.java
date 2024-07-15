package com.mycompany.kosa_space.controller;


import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.kosa_space.dto.AttendanceNotes;
import com.mycompany.kosa_space.dto.TraineeInfo;
import com.mycompany.kosa_space.dto.request.AttendanceNotesRequestDTO;
import com.mycompany.kosa_space.dto.request.AttendanceTraineeRequestDTO;
import com.mycompany.kosa_space.dto.request.TraineeAttendanceDetailRequestDTO;
import com.mycompany.kosa_space.dto.response.AttendanceNotesResponseDTO;
import com.mycompany.kosa_space.dto.response.AttendanceReasonDashboardResponseDTO;
import com.mycompany.kosa_space.dto.response.TraineeApproveAttendanceListResponseDTO;
import com.mycompany.kosa_space.dto.response.TraineeAttendanceDetailResponseDTO;
import com.mycompany.kosa_space.dto.response.TraineeAttendanceListResponseDTO;
import com.mycompany.kosa_space.dto.response.UserAttendanceTimeInfoResponseDTO;
import com.mycompany.kosa_space.service.AttendanceService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/attendance")
public class AttendanceController {
	@Autowired
	private AttendanceService attendanceService;
	
	// 교육생 입실 시간, 퇴실 시간, 과제 작성 여부 조회 기능
	@GetMapping("/user/attendance/time")
	public UserAttendanceTimeInfoResponseDTO attendanceTime(
			@RequestParam String mid, @RequestParam String adate) throws ParseException{
		
		return attendanceService.getAttendanceTime(mid, adate);
	}
	
	
	// (운영진) 교육생 출결 활성화 기능
	@PostMapping("/active")
	public void attendanceActive(@RequestParam String adate) throws Exception {

		attendanceService.active(adate);	
	}
	
	// 교육생 입실 기능
	@PostMapping("/checkin")
	public void userCheckIn(@RequestBody AttendanceTraineeRequestDTO attendance,
			HttpServletRequest request) throws Exception {
		String clientIP = request.getHeader("clientIP");
		
		//log.info("clientIP: " + clientIP);
		attendanceService.checkin(clientIP, attendance);
	}
	
	// 교육생 퇴실 기능
	@PostMapping("/checkout")
	public void userCheckOut(@RequestBody AttendanceTraineeRequestDTO attendance,
			HttpServletRequest request) throws Exception{
		String clientIP = request.getHeader("clientIP");
		
		attendanceService.checkout(clientIP, attendance);
	}
	
	// 교육생 사유 작성 기능
	@PostMapping("/reason/create")
	public void reasonCreate(AttendanceNotesRequestDTO request) throws Exception {
		log.info("request: " + request.toString());
		
		attendanceService.createReason(request);
	}
	
	// 교육생 사유 단건 조회 기능
	@GetMapping("/reason/detail")
	public AttendanceNotesResponseDTO reasonInfo(@RequestParam String mid,
			@RequestParam String adate) throws Exception{
		
		log.info("mid: " + mid);
		log.info("adate: " + adate);
		return attendanceService.detailReason(mid, adate);
	}
	
	// 교육생 사유 수정 기능 
	@PostMapping("/reason/update")
	public void reasonUpdate(AttendanceNotesRequestDTO request) throws Exception{
		attendanceService.updateReason(request);
	}
	
	// (운영진) 교육생이 등록한 사유에 대한 승인 기능
	@GetMapping("/reason/approve")
	public void reasonApprove(@RequestParam String mid,
			@RequestParam String adate) throws Exception {
		attendanceService.approveReason(mid, adate);
	}
	
	// (운영진) 교육생 출결 승인 기능 
	@GetMapping("/approve")
	public void attendanceApprove(@RequestParam String mid,
			@RequestParam String adate) throws Exception {
		attendanceService.approveAttendance(mid, adate);
	}
	
	// (운영진) 사유 대시보드 
	@GetMapping("/reason/dashboard")
	public AttendanceReasonDashboardResponseDTO reasonDashboard(
			@RequestParam(value = "ecname", required = false, defaultValue = "all") String ecname,
			@RequestParam(value = "cname", required = false, defaultValue = "all") String cname,
			@RequestParam(value = "adate", required = true) String adate) throws Exception{
		
		return attendanceService.dashboard(ecname, cname, adate);
	}
	
	// (운영진, 교육생) 교육생 출결 상세 조회 
	@GetMapping("/trainee/detail")
	public List<TraineeAttendanceDetailResponseDTO> traineeAttendanceDetail(
			@RequestParam(value = "mid", required = true) String mid,
			@RequestParam(value = "startdate", required = false, defaultValue = "all") String startdate,
			@RequestParam(value = "enddate", required = false, defaultValue = "all") String enddate) {
		
		return attendanceService.detailTraineeAttendance(mid, startdate, enddate);
	}
	
	// 파라미터에 해당하는 교육생 출결 목록 조회
	@GetMapping("/list")
	public List<TraineeAttendanceListResponseDTO> attendanceList(
			@RequestParam String ecname, @RequestParam String cname,
			@RequestParam String adate) throws ParseException{
		
		return attendanceService.listAttendance(ecname, cname, adate);
	}
	
	// 파라미터에 해당하는 교육생 출결 승인 조회
	@GetMapping("/approve/list")
	public List<TraineeApproveAttendanceListResponseDTO> attendanceApproveList(
			@RequestParam String ecname, @RequestParam String cname,
			@RequestParam String adate) throws ParseException{
		return attendanceService.listApproveAttendnace(ecname, cname, adate);
	}
	
	// 교육생 출결 정보 상세 조회
	@GetMapping("/trainee/attendance/dashboard/detail")
	public TraineeAttendanceListResponseDTO attendanceTraineeDetail(
			@RequestParam String mid, @RequestParam String adate) throws ParseException {
		return attendanceService.dashboardDetail(mid, adate);
	}
	
	// 교육생 사유 첨부파일 다운로드 기능 
	@GetMapping("/download/reason/attach")
	public void traineeImgDownload(
			@RequestParam String mid, @RequestParam String adate,
			HttpServletResponse response) throws ParseException {
		
		log.info("mid = " + mid);
		
		AttendanceNotes traineeInfo = attendanceService.anattachDownload(mid, adate);
		
		// 파일 이름이 한글일 경우, 브라우저에서 한글 이름으로 다운로드 받기 위해 헤더에 추가할 내용
		try {
			// 한글 파일의 이름 -> 인코딩 변경
			String fileName = new String(traineeInfo.getAnattachoname().getBytes("UTF-8"), "ISO-8859-1");

			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

			// 파일 타입을 헤더에 추가
			response.setContentType(traineeInfo.getAnattachtype());

			// 응답 바디에 파일 데이터를 출력
			OutputStream os = response.getOutputStream();
			os.write(traineeInfo.getAnattach()); // byte 배열 타입을 받아서 저장
			os.flush();
			os.close();

		} catch (IOException e) {
			log.error(e.getMessage()); // error 출력
		}
	}
	
	/*
	@GetMapping("/test/getClientIP")
	public String getClientIP(HttpServletRequest request) {
		
		
		String clientIP = request.getRemoteAddr();
		return clientIP;
	}
	*/
}
