package com.mycompany.kosa_space.controller;


import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.kosa_space.dto.Attendance;
import com.mycompany.kosa_space.dto.AttendanceNotes;
import com.mycompany.kosa_space.dto.request.AttendanceNotesRequestDTO;
import com.mycompany.kosa_space.dto.request.AttendanceTraineeRequestDTO;
import com.mycompany.kosa_space.dto.response.AttendanceNotesResponseDTO;
import com.mycompany.kosa_space.service.AttendanceService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/attendance")
public class AttendanceController {
	@Autowired
	private AttendanceService attendanceService;
	
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
				
		return attendanceService.detailReason(mid, adate);
	}
	
	// 교육생 사유 수정 기능 
	@PostMapping("/reason/update")
	public void reasonUpdate(AttendanceNotesRequestDTO request) throws Exception{
		attendanceService.updateReason(request);
	}
	
	// 파라미터에 해당하는 교육생 출결 목록 조회
	// parameter: ecname, cname, startdate, enddate, checkinstatus, chechoutstatus, mname
	
	
	/*
	@GetMapping("/test/getClientIP")
	public String getClientIP(HttpServletRequest request) {
		
		
		String clientIP = request.getRemoteAddr();
		return clientIP;
	}
	*/
}
