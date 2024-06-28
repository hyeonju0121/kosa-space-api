package com.mycompany.kosa_space.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;

import com.mycompany.kosa_space.dto.TrainingRoom;
import com.mycompany.kosa_space.dto.request.CreateTrainingRoomRequestDTO;
import com.mycompany.kosa_space.service.EduService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/edu")
public class EduController {
	@Autowired
	private EduService eduService;

	// 강의실 관련 -------------------------------------
	
	// 강의실 등록
	@PostMapping("/room/create")
	public void roomCreate(CreateTrainingRoomRequestDTO room) {
		eduService.createRoom(room);
	}
	
	// 강의실 단건 조회
	@GetMapping("/room/info")
	public TrainingRoom roomInfo(@RequestParam int trno) {

		return eduService.infoRoom(trno);
	}
	
	// 교육장 이름 기준으로 강의실 목록 조회
	@GetMapping("/room/list")
	public List<TrainingRoom> roomList(@RequestParam String ecname) {
		
		return eduService.listRoom(ecname);
	}
	
	// 강의실 전체 목록 조회
	@GetMapping("/room/list/all")
	public List<TrainingRoom> roomListAll() {
		
		return eduService.listAllRoom();
	}
	
	// 강의실 수정
	@PutMapping("/room/update")
	public void roomUpdate(@RequestParam int trno,
			CreateTrainingRoomRequestDTO room) {
		eduService.updateRoom(trno, room);
	}
	
	// 강의실 삭제
	@DeleteMapping("/room/delete")
	public void roomDelete(@RequestParam int trno) {
		
	}
	
	// 강의실 전체 삭제
	@DeleteMapping("/room/delete/all")
	public void roomDeleteAll() {
		
	}

}
