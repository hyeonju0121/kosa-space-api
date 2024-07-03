package com.mycompany.kosa_space.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;

import com.mycompany.kosa_space.dto.EduAttach;
import com.mycompany.kosa_space.dto.EduCenter;
import com.mycompany.kosa_space.dto.TrainingRoom;
import com.mycompany.kosa_space.dto.request.CourseParameterRequestDTO;
import com.mycompany.kosa_space.dto.request.CreateCourseRequestDTO;
import com.mycompany.kosa_space.dto.request.CreateEduCenterRequestDTO;
import com.mycompany.kosa_space.dto.request.CreateTrainingRoomRequestDTO;
import com.mycompany.kosa_space.dto.response.CourseResponseDTO;
import com.mycompany.kosa_space.dto.response.EduCenterResponseDTO;
import com.mycompany.kosa_space.service.EduService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/edu")
public class EduController {
	@Autowired
	private EduService eduService;
	
	// 교육장 관련 -------------------------------------
	// 교육장 등록
	@PostMapping("/center/create")
	public void centerCreate(CreateEduCenterRequestDTO request) {
		log.info(request.toString());
		
		eduService.createCenter(request);
	}
	
	// 교육장 단건 조회
	@GetMapping("/center/info")
	public EduCenterResponseDTO centerInfo(@RequestParam int ecno) {
		return eduService.infoCenter(ecno);
	}
	
	// 교육장 전체 조회
	@GetMapping("/center/list")
	public List<EduCenterResponseDTO> centerList() {
		return eduService.listCenter();
	}
	
	// 교육장 수정
	@PutMapping("/center/update")
	public void centerUpdate(@RequestParam int ecno, 
				CreateEduCenterRequestDTO request) {
		eduService.updateCenter(ecno, request);
	}
	
	// 교육장 단건 삭제
	@DeleteMapping("/center/delete")
	public void centerDelete(@RequestParam int ecno) {
		eduService.deleteCenter(ecno);
	}
	
	// 교육장 이름 전체 조회
	@GetMapping("/center/name/list")
	public List<String> centerNameList() {
		return eduService.listCenterName();
	}
	

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
		eduService.deleteRoom(trno);
	}
	
	// 교육과정 관련 -------------------------------------
	
	// 교육과정 등록
	@PostMapping("/course/create")
	public void courseCreate(CreateCourseRequestDTO request) {
		eduService.createCourse(request);
	}
	
	// 교육과정 수정
	@PutMapping("/course/update")
	public void courseUpdate(@RequestParam int cno, 
			CreateCourseRequestDTO request) {
		eduService.updateCourse(cno, request);
	}
	
	// 교육과정 단건조회
	@GetMapping("/course/info")
	public CourseResponseDTO courseInfo(@RequestParam int cno) {
		return eduService.infoCourse(cno);
	}
	
	// 파라미터 값으로 교육과정 조회
	@GetMapping("/course/list")
	public List<CourseResponseDTO> courseList(@RequestParam(value="ecname", 
		    required = false, defaultValue="all") String ecname,
			@RequestParam(value="cstatus", 
		    required = false, defaultValue="all") String cstatus,
			@RequestParam(value="cprofessor", 
		    required = false, defaultValue="all") String cprofessor) {
		
		CourseParameterRequestDTO request = CourseParameterRequestDTO.builder()
				.ecname(ecname)
				.cstatus(cstatus)
				.cprofessor(cprofessor)
				.build();
		
		return eduService.listCourse(request);
	}
	
	// 다운로드
	@GetMapping("/download/attach/{eano}")
	public void download(@PathVariable int eano, HttpServletResponse response) {
		EduAttach attach = eduService.attachDownload(eano);
		
		// 파일 이름이 한글일 경우, 브라우저에서 한글 이름으로 다운로드 받기 위해 헤더에 추가할 내용
		try {
			// 한글 파일의 이름 -> 인코딩 변경
			String fileName = new String(
					attach.getEaattachoname().getBytes("UTF-8"), "ISO-8859-1");
					
			response.setHeader("Content-Disposition", 
					"attachment; filename=\"" + fileName + "\"");
					
			// 파일 타입을 헤더에 추가
			response.setContentType(attach.getEaattachtype());
					
			// 응답 바디에 파일 데이터를 출력
			OutputStream os = response.getOutputStream();
			os.write(attach.getEaattach());
			os.flush();
			os.close();
					
			} catch (IOException e) {
				log.error(e.getMessage()); // error 출력
			}
		}
	
}
