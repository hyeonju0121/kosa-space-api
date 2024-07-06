package com.mycompany.kosa_space.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.kosa_space.dto.request.AttendanceTraineeRequestDTO;
import com.mycompany.kosa_space.service.AttendanceService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/attendance")
public class AttendanceController {
	@Autowired
	private AttendanceService attendanceService;
	
	// 교육생 입실 기능
	@PostMapping("/checkin")
	public void userCheckIn(@RequestBody AttendanceTraineeRequestDTO attendance,
			HttpServletRequest request) throws Exception {
		String clientIP = request.getHeader("clientIP");
		
		log.info("clientIP: " + clientIP);
		
		//log.info("mid: " + mid);
		log.info("attendance: " + attendance.toString());
		
		attendanceService.checkin(clientIP, attendance);
	}
	
	// 교육생 퇴실 기능
	@PostMapping("/checkout")
	public void userCheckOut(@RequestBody AttendanceTraineeRequestDTO attendance,
			HttpServletRequest request) throws Exception{
		String clientIP = request.getHeader("clientIP");
		
		attendanceService.checkout(clientIP, attendance);
	}
	
	/*
	@GetMapping("/test/getClientIP")
	public String getClientIP(HttpServletRequest request) {
		
		
		String clientIP = request.getRemoteAddr();
		return clientIP;
	}
	*/
}
