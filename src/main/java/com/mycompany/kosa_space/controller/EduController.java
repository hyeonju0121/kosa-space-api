package com.mycompany.kosa_space.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.mycompany.kosa_space.dto.TraineeInfo;
import com.mycompany.kosa_space.dto.TrainingRoom;
import com.mycompany.kosa_space.dto.request.CourseParameterRequestDTO;
import com.mycompany.kosa_space.dto.request.CreateCourseRequestDTO;
import com.mycompany.kosa_space.dto.request.CreateEduCenterRequestDTO;
import com.mycompany.kosa_space.dto.request.CreateTraineeRequestDto;
import com.mycompany.kosa_space.dto.request.CreateTrainingRoomRequestDTO;
import com.mycompany.kosa_space.dto.request.UpdateTraineeRequestDto;
import com.mycompany.kosa_space.dto.response.CourseDashboardResponseDTO;
import com.mycompany.kosa_space.dto.response.CourseResponseDTO;
import com.mycompany.kosa_space.dto.response.EduCenterResponseDTO;
import com.mycompany.kosa_space.dto.response.TraineeProfileHeaderResposneDTO;
import com.mycompany.kosa_space.dto.response.TraineeResponseDto;
import com.mycompany.kosa_space.dto.response.TrainingRoomListResponseDTO;
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
	public void centerUpdate(@RequestParam int ecno, CreateEduCenterRequestDTO request) {
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
	public List<TrainingRoomListResponseDTO> roomList(
			@RequestParam(value = "ecname", required = false, defaultValue = "all") String ecname,
			@RequestParam(value = "trenable", required = false, defaultValue = "all") String trenable) {
		List<String> request = new ArrayList<>();
		request.add(ecname);
		request.add(trenable);

		return eduService.listRoom(request);
	}

	// 강의실 전체 목록 조회
	@GetMapping("/room/list/all")
	public List<TrainingRoom> roomListAll() {

		return eduService.listAllRoom();
	}

	// 강의실 수정
	@PutMapping("/room/update")
	public void roomUpdate(@RequestParam int trno, CreateTrainingRoomRequestDTO room) {
		eduService.updateRoom(trno, room);
	}

	// 강의실 삭제
	@DeleteMapping("/room/delete")
	public void roomDelete(@RequestParam int trno) {
		eduService.deleteRoom(trno);
	}
	
	// 강의실 목록 조회
	@GetMapping("/room/namelist")
	public List<String> roomNameList(@RequestParam String ecname) {
		return eduService.listNameList(ecname);
	}

	// 교육과정 관련 -------------------------------------

	// 교육과정 등록
	@PostMapping("/course/create")
	public void courseCreate(CreateCourseRequestDTO request) {
		log.info("request: " + request.toString());

		eduService.createCourse(request);
	}

	// 교육과정 수정
	@PutMapping("/course/update")
	public void courseUpdate(@RequestParam int cno, CreateCourseRequestDTO request) {
		eduService.updateCourse(cno, request);
	}

	// 교육과정 단건조회
	@GetMapping("/course/info")
	public CourseResponseDTO courseInfo(@RequestParam int cno) {
		return eduService.infoCourse(cno);
	}

	// 파라미터 값으로 교육과정 조회
	@GetMapping("/course/list")
	public Map<String, Object> courseList(
			@RequestParam(value = "ecname", required = false, defaultValue = "all") String ecname,
			@RequestParam(value = "cstatus", required = false, defaultValue = "all") String cstatus,
			@RequestParam(value = "cprofessor", required = false, defaultValue = "all") String cprofessor,
			@RequestParam(defaultValue = "1") int pageNo) {

		CourseParameterRequestDTO request = CourseParameterRequestDTO.builder()
				.ecname(ecname)
				.cstatus(cstatus)
				.cprofessor(cprofessor)
				.build();

		return eduService.listCourse(request, pageNo);
	}
	
	@GetMapping("course/cnamelist")
	public List<String> courseCnameList(@RequestParam(value="ecname", required=false,
								defaultValue = "all")String ecname) {
		
		return eduService.listCnameCourse(ecname);
	}
	
	@GetMapping("course/cnamelist/inprogress")
	public List<String> courseCnameListInProress(@RequestParam(value="ecname", required=false,
			defaultValue = "all")String ecname) {
		return eduService.listCnameCourseInProgress(ecname);
	}
	
	
	// 교육과정 강사진 목록 조회
	@GetMapping("course/cprofessor/list") 
		public List<String> courseCprofessorList(
				@RequestParam(value="ecname", required=false, defaultValue = "all")String ecname) {
		return eduService.listCprofessor(ecname);
	}
	
	
	// 다운로드
	@GetMapping("/download/attach/{eano}")
	public void download(@PathVariable int eano, HttpServletResponse response) {
		EduAttach attach = eduService.attachDownload(eano);

		// 파일 이름이 한글일 경우, 브라우저에서 한글 이름으로 다운로드 받기 위해 헤더에 추가할 내용
		try {
			// 한글 파일의 이름 -> 인코딩 변경
			String fileName = new String(attach.getEaattachoname().getBytes("UTF-8"), "ISO-8859-1");

			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

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

	// 교육생 관련 -------------------------------------

	// 교육생 등록
	@PostMapping("/admin/trainee/register")
	public void traineeRegister(@RequestParam("ecname") String ecname, @RequestParam("cname") String cname,
			CreateTraineeRequestDto request) {
		log.info("traineeRegister 실행");
		log.info("createTraineeDTO = " + request);
		log.info("ecname = " + ecname);
		log.info("cname = " + cname);
		eduService.createTrainee(request);
	}

	// 교육생 단건 조회
	@GetMapping("/admin/trainee/detail")
	public TraineeResponseDto traineeInfo(@RequestParam String mid) {
		log.info("교육생 단건 조회 실행");
		log.info("mid = " + mid);
		return eduService.infoTrainee(mid);
	}

	// 교육생 수정
	@PutMapping("/admin/trainee/update")
	public void traineeUpdate(@RequestParam String mid, UpdateTraineeRequestDto request) {
		log.info("교육생 수정 실행");
		log.info("mid = " + mid);
		log.info("request = " + request);
		eduService.updateTrainee(mid, request);
	}

	// 교육생 (교육장, 교육과정) 목록 조회
	@GetMapping("/admin/trainee/list")
	public List<TraineeResponseDto> traineeList(@RequestParam(defaultValue = "all", required = false) String ecname,
			@RequestParam(defaultValue = "all", required = false) String cname) {
		log.info("traineeList 실행");
		log.info("ecname = " + ecname);
		log.info("cname = " + cname);

		return eduService.listTrainee(ecname, cname);
	}
	
	// 교육생 이미지 첨부파일 다운로드
	@GetMapping("/download/traineeattach/{mid}")
	public void traineeImgDownload(@PathVariable String mid, HttpServletResponse response) {
		log.info("mid = " + mid);
		
		TraineeInfo traineeInfo = eduService.tattachDownload(mid);
		// log.info("traineeInfo = " + traineeInfo.toString());
		
		// 파일 이름이 한글일 경우, 브라우저에서 한글 이름으로 다운로드 받기 위해 헤더에 추가할 내용
		try {
			// 한글 파일의 이름 -> 인코딩 변경
			String fileName = new String(traineeInfo.getTprofileoname().getBytes("UTF-8"), "ISO-8859-1");

			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

			// 파일 타입을 헤더에 추가
			response.setContentType(traineeInfo.getTprofiletype());

			// 응답 바디에 파일 데이터를 출력
			OutputStream os = response.getOutputStream();
			os.write(traineeInfo.getTprofileimg()); // byte 배열 타입을 받아서 저장
			os.flush();
			os.close();

		} catch (IOException e) {
			log.error(e.getMessage()); // error 출력
		}
	}
	 
	
	// 교육생 프로필 헤더
	@GetMapping("/trainee/profile/header")
	public TraineeProfileHeaderResposneDTO getUserProfile(String mid) {
		
		return eduService.getTraineeProfileHeader(mid);
	}
	
	
	
	// 운영진 대시보드 ----------------------------------------------------------
	@GetMapping("/dashboard/course/totalcount")
	public CourseDashboardResponseDTO totalCourseCnt(@RequestParam String ecname) {
		return eduService.totalCnt(ecname);
	}
	
	// ecname 기준으로 교육상태에 따른 교육과정 진행 현황 조회
	@PostMapping("/dashboard/course")
	public Map<String, Object> courseList(
			@RequestParam String ecname,
			@RequestParam String cstatus,
			@RequestParam(defaultValue = "1") int pageNo) {

		return eduService.getDashboardCourseList(ecname, cstatus, pageNo);
	}
	
	// ecname 기준으로 현재 진행중인 교육과정의 교육생 출결 현황 조회
	@PostMapping("/dashboard/attendance")
	public Map<String, Object> attendanceList(
			@RequestParam String ecname,
			@RequestParam(defaultValue = "1") int pageNo, 
			@RequestParam String adate) throws Exception{
		return eduService.getDashboardAttendanceList(ecname, pageNo, adate);
	}
}
