package com.mycompany.kosa_space.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mycompany.kosa_space.dao.CourseDao;
import com.mycompany.kosa_space.dao.CourseResponseDao;
import com.mycompany.kosa_space.dao.EduAttachDao;
import com.mycompany.kosa_space.dao.EduCenterDao;
import com.mycompany.kosa_space.dao.MemberDao;
import com.mycompany.kosa_space.dao.TrainingRoomDao;
import com.mycompany.kosa_space.dto.Course;
import com.mycompany.kosa_space.dto.EduAttach;
import com.mycompany.kosa_space.dto.EduCenter;
import com.mycompany.kosa_space.dto.TrainingRoom;
import com.mycompany.kosa_space.dto.request.CourseParameterRequestDTO;
import com.mycompany.kosa_space.dto.request.CreateCourseRequestDTO;
import com.mycompany.kosa_space.dto.request.CreateEduCenterRequestDTO;
import com.mycompany.kosa_space.dto.request.CreateTrainingRoomRequestDTO;
import com.mycompany.kosa_space.dto.response.CourseResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EduService {
	@Autowired
	private EduCenterDao educenterDao;

	@Autowired
	private TrainingRoomDao trainingRoomDao;
	
	@Autowired
	private EduAttachDao eduAttachDao;
	
	@Autowired
	private CourseDao courseDao;
	
	@Autowired
	private CourseResponseDao courseResponseDao;
	
	@Autowired
	private MemberDao memberDao;
	
	
	// 교육장 관련 ------------------------------------------------
	/*
	 * 교육장 등록
	 * 1. 교육장 명 중복 여부 검사 진행 -> 존재한다면, 에러 처리
	 * 2. 도로명 주소 + 상세주소 합쳐서 address 변수에 저장 
	 * 3. EduCenter 객체 생성 후, request 내에서 값을 꺼내 세팅 후, DB에 저장
	 * 3. 첨부파일로 들어온 갯수만큼 반복문 내에서 EduAttach 객체 생성 후,
	 * eaattach, eaattachoname, eaattachtype 세팅하고 DB에 저장하는 방식으로 구현함
	 */
	@Transactional
	public void createCenter(CreateEduCenterRequestDTO request) {
		// 교육장 명 중복 여부 검사
		validationDuplicatedEcname(request.getEcname());
		
		// 도로명 주소 + 상세주소 합치기
		String address = request.getEcaddress() + ", " + request.getEcdetailaddress();
		
		// EduCenter 객체 생성
		EduCenter center = EduCenter.builder()
				.ecname(request.getEcname())
				.ecpostcode(request.getEcpostcode())
				.ecaddress(address)
				.build();
		
		// DB 에 EduCenter 객체 저장
		educenterDao.insert(center);
		
		// 저장된 EduCenter ecno 를 찾고, 변수에 저장
		int ecno = educenterDao.selectByEcname(request.getEcname()).getEcno();
		
		// 첨부파일이 넘어왔을 경우 처리
		List<MultipartFile> attachList = request.getEcattachdata();
		if (attachList.size() > 0) { // 첨부파일이 있는 경우
			for (int i = 0; i < attachList.size(); i++) {
				EduAttach attach = new EduAttach();
				MultipartFile mf = attachList.get(i);
				
				// 파일 이름 설정
				attach.setEaattachoname(mf.getOriginalFilename());
				// 파일 종류 설정
				attach.setEaattachtype(mf.getContentType());
				
				try {
					// 파일 데이터 설정
					attach.setEaattach(mf.getBytes());
				} catch (IOException e) {
				}
				
				// ecno 세팅
				attach.setEcno(ecno);
				// EduAttach 객체 DB 에 저장
				eduAttachDao.insertEduCenter(attach);
			}
		}
	}
	

	// 교육장 ecno 기준으로 단건 조회
	public EduCenter infoCenter(int ecno) {
		// ecno 유효한지 검증
		validationExistsByEcno(ecno);
		
		return educenterDao.selectByEcno(ecno);
	}
	

	// 교육장 전체 조회
	public List<EduCenter> listCenter() {
		
		return educenterDao.selectAllCenter();
	}

	// 교육장 ecno 기준으로 수정
	@Transactional
	public void updateCenter(int ecno, CreateEduCenterRequestDTO request) {
		// ecno 유효한지 검증
		validationExistsByEcno(ecno);
		
		// 기존 교육장 명과 request 로 들어온 교육장명이 다를 경우 교육장 명 중복 여부 검사
		EduCenter center = educenterDao.selectByEcno(ecno);
		
		if (!center.getEcname().equals(request.getEcname())) {
			validationDuplicatedEcname(request.getEcname());
		}
		
		// 도로명 주소 + 상세주소 합치기
	    String address = request.getEcaddress() + ", " + request.getEcdetailaddress();
	    request.setEcaddress(address);
	    
	    // 첨부파일이 넘어왔을 경우 처리
	 	List<MultipartFile> attachList = request.getEcattachdata();
	 	log.info("attachList: " + attachList.toString());
	 	
	 	// ecno 에 해당하는 첨부파일 전체 가져오기
		List<EduAttach> data = eduAttachDao.selectEduCenterByEcno(ecno);
		
		if (attachList.size() > 0) { // 첨부파일이 있는 경우
			if (attachList.size() > data.size()) { 
				// 기존 첨부파일 목록보다 request 로 들어온 첨부파일 사이즈가 더 큰 경우
				for (int i = 0; i < data.size(); i++) {
					// 사이즈가 동일한 만큼, 기존 첨부파일 목록 덮어쓰기 
					EduAttach attach = data.get(i);
					int eano = attach.getEano();
					
					MultipartFile mf = attachList.get(i);
					
					// 파일 이름 설정
					attach.setEaattachoname(mf.getOriginalFilename());
					// 파일 종류 설정
					attach.setEaattachtype(mf.getContentType());
					
					try {
		 				// 파일 데이터 설정
		 				attach.setEaattach(mf.getBytes());
		 			} catch (IOException e) {
		 			}
					
					// 수정된 EduAttach 객체를 DB 에 update
		 			eduAttachDao.updateByEano(eano, attach);
		 			
		 			attachList.remove(i);
				}
				// 남은 첨부파일 목록 DB 에 삽입하기 
				log.info("남은 attachList: " + attachList.toString());
				for (int i = 0; i < attachList.size(); i++) {
					EduAttach attach = new EduAttach();
					MultipartFile mf = attachList.get(i);
					
					// 파일 이름 설정
					attach.setEaattachoname(mf.getOriginalFilename());
					// 파일 종류 설정
					attach.setEaattachtype(mf.getContentType());
					
					try {
						// 파일 데이터 설정
						attach.setEaattach(mf.getBytes());
					} catch (IOException e) {
					}
					
					// ecno 세팅
					attach.setEcno(ecno);
					// EduAttach 객체 DB 에 저장
					eduAttachDao.insertEduCenterNewAttach(attach);
				}
			} else {
				// 기존 첨부파일 목록보다 request 로 들어온 첨부파일 사이즈가 더 작은 경우
				for (int i = 0; i < attachList.size(); i++) {
					// 사이즈가 동일한 만큼, 기존 첨부파일 목록 덮어쓰기 
					EduAttach attach = data.get(i);
					
					MultipartFile mf = attachList.get(i);
					
					// 파일 이름 설정
					attach.setEaattachoname(mf.getOriginalFilename());
					// 파일 종류 설정
					attach.setEaattachtype(mf.getContentType());
					
					try {
		 				// 파일 데이터 설정
		 				attach.setEaattach(mf.getBytes());
		 			} catch (IOException e) {
		 			}
					
					// 수정된 EduAttach 객체를 DB 에 update
		 			eduAttachDao.updateByEano(attach.getEano(), attach);
		 			
		 			data.remove(i);
				}
				// 남은 첨부파일 목록 DB 에서 삭제하기
				for (int i = 0; i < data.size(); i++) {
					EduAttach attach = data.get(i);
					int eano = attach.getEano();
					
					// 기존에 남아있던 첨부파일을 DB 에서 delete 처리
					eduAttachDao.deleteByEano(eano);
				}
			}
		}
		
		// update
		educenterDao.update(ecno, request);
	}
	
	// 교육장 ecno 기준으로 단건 삭제
	@Transactional
	public void deleteCenter(int ecno) {
		// ecno 유효한지 검증
		validationExistsByEcno(ecno);
		
		// EduAttach DB delete 처리
		eduAttachDao.deleteEduCenterByEcno(ecno);
		
		// EduCenter DB delete 처리
		educenterDao.deleteByEcno(ecno);
	}
	

	// 교육장 이름 전체 조회
	public List<String> listCenterName() {
		return educenterDao.selectAllCenterName();
	}
	

	// 강의실 관련 ------------------------------------------------
	/**
	 * 강의실 등록 ********************
	 * 1) request 로 넘어온 강의실 명이
	 * 교육장 내에 중복으로 존재하는 강의실 명일 경우 에러 반환
	 */
	@Transactional
	public void createRoom(CreateTrainingRoomRequestDTO room) {
		// 강의실 명 중복 여부 검사
		// 교육장 정보 가져오기
		EduCenter center = educenterDao.selectByEcname(room.getEcname());
		
		int ecno = center.getEcno();
		
		validationDuplicatedTrname(ecno, room.getTrname());
		
		// TrainingRoom 객체 생성
		TrainingRoom response = TrainingRoom.builder()
				.ecno(ecno)
				.trname(room.getTrname())
				.trcapacity(room.getTrcapacity())
				.trenable(room.isTrenable())
				.build();
		
		log.info(response.toString());

		// 강의실 객체 DB에 저장
		trainingRoomDao.insert(response);
		
		// 저장된 TrainingRoom trno 를 찾고, 변수에 저장 
		int trno = trainingRoomDao.selectByEcnoAndTrname(response.getEcno(),
								response.getTrname());
		log.info("저장된 trno: " + trno);
		
		// 첨부가 넘어왔을 경우 처리
		// 첨부파일이 넘어왔을 경우 처리
		List<MultipartFile> attachList = room.getTrattachdata();
		if (attachList.size() > 0) { // 첨부파일이 있는 경우
			for (int i = 0; i < attachList.size(); i++) {
				EduAttach attach = new EduAttach();
				MultipartFile mf = attachList.get(i);
						
				// 파일 이름 설정
				attach.setEaattachoname(mf.getOriginalFilename());
				// 파일 종류 설정
				attach.setEaattachtype(mf.getContentType());
						
				try {
					// 파일 데이터 설정
					attach.setEaattach(mf.getBytes());
				} catch (IOException e) {
				}
						
				// trno 세팅
				attach.setTrno(trno);
				// EduAttach 객체 DB 에 저장
				eduAttachDao.insertTrainingRoom(attach);
			}
		}
		
	}
	
	// 강의실 단건조회 
	public TrainingRoom infoRoom(int trno) {
		TrainingRoom response = trainingRoomDao.selectByTrno(trno);
		if (response == null) {
			throw new RuntimeException("강의실이 존재하지 않습니다.");
		}
		
		return response;	
	}
	
	// 교육장 이름으로 강의실 목록 조회
	public List<TrainingRoom> listRoom(String ecname) {
		EduCenter center = educenterDao.selectByEcname(ecname);
		
		// 존재하지 않는 교육장명일 경우 에러 처리
		if (educenterDao.selectByEcname(ecname) == null) {
			throw new RuntimeException("존재하지 않는 교육장 이름입니다.");
		}
		
		return trainingRoomDao.selectByEcno(center.getEcno());
	}
	
	
	// 강의실 수정
	@Transactional
	public void updateRoom(int trno, CreateTrainingRoomRequestDTO request) {
		// trno 가 유효한지 검사
		validationExistsByTrno(trno);
		
		log.info("request: " + request);
		
		// 첨부파일이 넘어왔을 경우 처리
		List<MultipartFile> attachList = request.getTrattachdata();
			 	
	    // trno 에 해당하는 첨부파일 전체 가져오기
		List<EduAttach> data = eduAttachDao.selectTrainingRoomByTrno(trno);
		
		if (attachList.size() > 1) { // 첨부파일이 있는 경우
			if (attachList.size() > data.size()) { 
				// 기존 첨부파일 목록보다 request 로 들어온 첨부파일 사이즈가 더 큰 경우
				for (int i = 0; i < data.size(); i++) {
					// 사이즈가 동일한 만큼, 기존 첨부파일 목록 덮어쓰기 
					EduAttach attach = data.get(i);
					
					MultipartFile mf = attachList.get(i);
					
					// 파일 이름 설정
					attach.setEaattachoname(mf.getOriginalFilename());
					// 파일 종류 설정
					attach.setEaattachtype(mf.getContentType());
					
					try {
		 				// 파일 데이터 설정
		 				attach.setEaattach(mf.getBytes());
		 			} catch (IOException e) {
		 			}
					
					// 수정된 EduAttach 객체를 DB 에 update
		 			eduAttachDao.updateByEano(attach.getEano(), attach);
		 			
		 			attachList.remove(i);
				}
				// 남은 첨부파일 목록 DB 에 삽입하기 
				log.info("남은 attachList: " + attachList.toString());
				for (int i = 0; i < attachList.size(); i++) {
					EduAttach attach = new EduAttach();
					//int trnoNum = attach.getTrno();
					MultipartFile mf = attachList.get(i);
					
					// 파일 이름 설정
					attach.setEaattachoname(mf.getOriginalFilename());
					// 파일 종류 설정
					attach.setEaattachtype(mf.getContentType());
					
					try {
						// 파일 데이터 설정
						attach.setEaattach(mf.getBytes());
					} catch (IOException e) {
					}
					
					// trno 세팅
					attach.setTrno(trno);
					// EduAttach 객체 DB 에 저장
					eduAttachDao.insertTrainingRoomNewAttach(attach);
				}
			} else {
				// 기존 첨부파일 목록보다 request 로 들어온 첨부파일 사이즈가 더 작은 경우
				for (int i = 0; i < attachList.size(); i++) {
					// 사이즈가 동일한 만큼, 기존 첨부파일 목록 덮어쓰기 
					EduAttach attach = data.get(i);
					
					MultipartFile mf = attachList.get(i);
					
					// 파일 이름 설정
					attach.setEaattachoname(mf.getOriginalFilename());
					// 파일 종류 설정
					attach.setEaattachtype(mf.getContentType());
					
					try {
		 				// 파일 데이터 설정
		 				attach.setEaattach(mf.getBytes());
		 			} catch (IOException e) {
		 			}
					
					// 수정된 EduAttach 객체를 DB 에 update
		 			eduAttachDao.updateByEano(attach.getEano(), attach);
		 			
		 			data.remove(i);
				}
				// 남은 첨부파일 목록 DB 에서 삭제하기
				for (int i = 0; i < data.size(); i++) {
					EduAttach attach = data.get(i);
					int eano = attach.getEano();
					
					// 기존에 남아있던 첨부파일을 DB 에서 delete 처리
					eduAttachDao.deleteByEano(eano);
				}
			}
		} 
		// update
		trainingRoomDao.update(trno, request);
	}
	
	// 강의실 전체 목록 조회
	public List<TrainingRoom> listAllRoom() {

		return trainingRoomDao.selectAllRoom();
	}
	
	// 강의실 trno 기준으로 단건삭제
	@Transactional
	public void deleteRoom(int trno) {
		// trno 가 유효한지 검사
		validationExistsByTrno(trno);
		
		// EduAttach DB delete 처리
		eduAttachDao.deleteTrainingRoomByTrno(trno);
				
		// TrainingRoom DB delete 처리
		trainingRoomDao.deleteByTrno(trno);
	}
	
	
	// 교육과정 관련 ------------------------------------------------

	//교육과정 등록 
	@Transactional
	public void createCourse(CreateCourseRequestDTO request) {
		int trno = request.getTrno();
		
		// 교육과정명 중복 검사 (교육장명&교육진행상태 여부가 진행중 및 진행예정이면 에러처리)
		validationDuplicatedCname(request.getEcname(), request.getCname());
		
		// trno 배정 가능한지 검사
		validationTrenable(request);
		
		// 운영진, 강사진 존재여부 검사
		validationExistsByMname(request.getCmanager());
		validationExistsByMname(request.getCprofessor());
		
		// cstartdate, cenddate String -> Date 변환
		String startdate = request.getCstartdate().substring(0, 10);
		String enddate = request.getCenddate().substring(0, 10);
				
		// DateTimeFormatter 객체를 생성하여 원하는 출력 포맷 지정
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	    // LocalDate 객체로 변환
		LocalDate cstartdate = LocalDate.parse(startdate, formatter);
		LocalDate cenddate = LocalDate.parse(enddate, formatter);
		
		// Course 객체 생성
		Course course = Course.builder()
				.trno(request.getTrno())
				.cname(request.getCname())
				.ccode(request.getCcode())
				.ctotalnum(request.getCtotalnum())
				.cstatus("진행예정")
				.cstartdate(cstartdate)
				.cenddate(cenddate)
				.crequireddate(request.getCrequireddate())
				.cprofessor(request.getCprofessor())
				.cmanager(request.getCmanager())
				.ctrainingdate(request.getCtrainingdate())
				.ctrainingtime(request.getCtrainingtime())
				.build();

		log.info("course: " + course.toString());
			
		// Course 객체 DB 에 저장
		courseDao.insert(course);
		
		// 저장된 Course cno 를 찾고, 변수에 저장
		int cno = courseDao.selectByCnameAndCstatus(request.getTrno(), 
						course.getCstatus(), request.getCname());
		
		// 첨부파일이 넘어왔을 경우 처리
		List<MultipartFile> attachList = request.getCattachdata();
		if (attachList.size() > 0) { // 첨부파일이 있는 경우
			for (int i = 0; i < attachList.size(); i++) {
				EduAttach attach = new EduAttach();
				MultipartFile mf = attachList.get(i);
						
				// 파일 이름 설정
				attach.setEaattachoname(mf.getOriginalFilename());
				// 파일 종류 설정
				attach.setEaattachtype(mf.getContentType());
						
				try {
					// 파일 데이터 설정
					attach.setEaattach(mf.getBytes());
				} catch (IOException e) {
				}
						
				// ecno 세팅
				attach.setCno(cno);
				// EduAttach 객체 DB 에 저장
				eduAttachDao.insertCourse(attach);
			}
		}
	}
	
	// 교육과정 수정 
	@Transactional
	public void updateCourse(int cno, CreateCourseRequestDTO request) {
		log.info("request: " + request.toString());
		
		// cno 가 유효한지 검사
		validationExistsByCno(cno);

		// 운영진, 강사진 존재여부 검사
		validationExistsByMname(request.getCmanager());
		validationExistsByMname(request.getCprofessor());
				
		// cstartdate, cenddate String -> Date 변환
		String startdate = request.getCstartdate().substring(0, 10);
		String enddate = request.getCenddate().substring(0, 10);
						
		// DateTimeFormatter 객체를 생성하여 원하는 출력 포맷 지정
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	    // LocalDate 객체로 변환
		LocalDate cstartdate = LocalDate.parse(startdate, formatter);
		LocalDate cenddate = LocalDate.parse(enddate, formatter);
		
		// trno 강의실 사용여부 조사 
		TrainingRoom room = trainingRoomDao.selectByTrno(request.getTrno());
		boolean trenable = room.isTrenable();
		
		// cno 에 해당하는 교육과정 정보 가져오기 
		Course course = courseDao.selectByCno(cno);
		
		int trno = request.getTrno();
		
		String cstatus = request.getCstatus();

		switch(cstatus) {
			case "진행중" : 
				// trno 에 해당하는 강의실 trenable 값 검사 수행
				if(trenable) {
					throw new RuntimeException("해당 강의실을 사용할 수 없습니다."
							+ "이미 교육과정이 진행중입니다.");
				} else {
					// trno 배정 가능한지 검사
					validationTrenable(request);
					
					// trneable 사용중으로 변경 처리
					trenable = true;
					trainingRoomDao.updateByTrenable(room.getTrno(), trenable);
				}
				break;
			
			case "진행완료":
				// 해당 강의실 사용여부 검사
				if(trenable) {
					throw new RuntimeException("해당 강의실을 사용할 수 없습니다."
							+ "이미 다른 교육과정이 진행중입니다. 진행완료로 변경하실 수 없습니다.");
				} 
				
				trenable = false;
				trainingRoomDao.updateByTrenable(room.getTrno(), trenable);
				break;

			case "진행예정":
				// 기존에 trno 와 request 로 들어온 trno가 다른 경우
				if (course.getTrno() != request.getTrno()) {
					log.info("기존 trno 와 다릅니다. 기존 trno : " + course.getTrno() + ", request trno : " + request.getTrno());
					// trno 배정 가능한지 검사
					validationTrenable(request);
				}
				break;
		}

		// Course 객체 생성
		Course courseData = Course.builder()
				.trno(request.getTrno())
				.cname(request.getCname())
				.ccode(request.getCcode())
				.ctotalnum(request.getCtotalnum())
				.cstartdate(cstartdate)
				.cenddate(cenddate)
				.crequireddate(request.getCrequireddate())
				.cstatus(request.getCstatus())
				.cprofessor(request.getCprofessor())
				.cmanager(request.getCmanager())
				.ctrainingdate(request.getCtrainingdate())
				.ctrainingtime(request.getCtrainingtime())
				.build();
		
		log.info(courseData.toString());

		// 첨부파일이 넘어왔을 경우 처리
		List<MultipartFile> attachList = request.getCattachdata();
					 	
	    // cno 에 해당하는 첨부파일 전체 가져오기
		List<EduAttach> data = eduAttachDao.selectCourseByCno(cno);
		
		if (attachList.size() > 1) { // 첨부파일이 있는 경우
			if (attachList.size() > data.size()) { 
				// 기존 첨부파일 목록보다 request 로 들어온 첨부파일 사이즈가 더 큰 경우
				for (int i = 0; i < data.size(); i++) {
					// 사이즈가 동일한 만큼, 기존 첨부파일 목록 덮어쓰기 
					EduAttach attach = data.get(i);
					
					MultipartFile mf = attachList.get(i);
					
					// 파일 이름 설정
					attach.setEaattachoname(mf.getOriginalFilename());
					// 파일 종류 설정
					attach.setEaattachtype(mf.getContentType());
					
					try {
		 				// 파일 데이터 설정
		 				attach.setEaattach(mf.getBytes());
		 			} catch (IOException e) {
		 			}
					
					// 수정된 EduAttach 객체를 DB 에 update
		 			eduAttachDao.updateByEano(attach.getEano(), attach);
		 			
		 			attachList.remove(i);
				}
				// 남은 첨부파일 목록 DB 에 삽입하기 
				log.info("남은 attachList: " + attachList.toString());
				for (int i = 0; i < attachList.size(); i++) {
					EduAttach attach = new EduAttach();
					// int cnoNum = attach.getCno();
					MultipartFile mf = attachList.get(i);
					
					// 파일 이름 설정
					attach.setEaattachoname(mf.getOriginalFilename());
					// 파일 종류 설정
					attach.setEaattachtype(mf.getContentType());
					
					try {
						// 파일 데이터 설정
						attach.setEaattach(mf.getBytes());
					} catch (IOException e) {
					}
					
					// trno 세팅
					attach.setCno(cno);
					// EduAttach 객체 DB 에 저장
					eduAttachDao.insertCourseNewAttach(attach);
				}
			} else {
				// 기존 첨부파일 목록보다 request 로 들어온 첨부파일 사이즈가 더 작은 경우
				for (int i = 0; i < attachList.size(); i++) {
					// 사이즈가 동일한 만큼, 기존 첨부파일 목록 덮어쓰기 
					EduAttach attach = data.get(i);
					
					MultipartFile mf = attachList.get(i);
					
					// 파일 이름 설정
					attach.setEaattachoname(mf.getOriginalFilename());
					// 파일 종류 설정
					attach.setEaattachtype(mf.getContentType());
					
					try {
		 				// 파일 데이터 설정
		 				attach.setEaattach(mf.getBytes());
		 			} catch (IOException e) {
		 			}
					
					// 수정된 EduAttach 객체를 DB 에 update
		 			eduAttachDao.updateByEano(attach.getEano(), attach);
		 			
		 			data.remove(i);
				}
				// 남은 첨부파일 목록 DB 에서 삭제하기
				for (int i = 0; i < data.size(); i++) {
					EduAttach attach = data.get(i);
					int eano = attach.getEano();
					
					// 기존에 남아있던 첨부파일을 DB 에서 delete 처리
					eduAttachDao.deleteByEano(eano);
				}
			}
		} 
		
		
		
		// Course 객체 DB 업데이트
		courseDao.update(cno, courseData);
		
	}
	
	// 교육과정 cno 기준으로 단건조회
	public CourseResponseDTO infoCourse(int cno) {
		// cno 가 유효한지 검사
		validationExistsByCno(cno);
		
		CourseResponseDTO response = courseResponseDao.selectByCno(cno);
		
		String cstartdate = response.getCstartdate().substring(0, 10);
		String cenddate = response.getCenddate().substring(0, 10);
	
		response.setCstartdate(cstartdate);
		response.setCenddate(cenddate);
		
        log.info(response.toString());
        
		return response;
	}
	
	// 파라미터 기준으로 교육과정 조회
	public List<CourseResponseDTO> listCourse(CourseParameterRequestDTO request) {
		log.info("request: " + request);
		
		List<CourseResponseDTO> response = courseResponseDao.listByParameter(request);
		
		// cstartdate, cenddate 세팅 
		for (CourseResponseDTO data : response) {
			String cstartdate = data.getCstartdate().substring(0, 10);
			String cenddate = data.getCenddate().substring(0, 10);
		
			data.setCstartdate(cstartdate);
			data.setCenddate(cenddate);
		}
		
		
		log.info("response: " + response);
		log.info("response.size: " + response.size());
			
		return response;
	}
		

	// ---- validation method ----------------------------------------------------
	// ecno 가 유효한지 검사하는 메소드 
	public boolean validationExistsByEcno(int ecno) {
		if (educenterDao.selectByEcno(ecno) == null) {
			throw new RuntimeException("존재하지 않는 교육장입니다. ");
		}
		return true;
	}
	
	// trno 가 유효한지 검사하는 메소드 
	public boolean validationExistsByTrno(int trno) {
		if (trainingRoomDao.selectByTrno(trno) == null) {
			throw new RuntimeException("존재하지 않는 강의실입니다. ");
		}
		return true;
	}
	
	// 교육장 명 중복 검사 메소드
	public boolean validationDuplicatedEcname(String ecname) {
		if (educenterDao.selectByEcname(ecname) != null) {
			throw new RuntimeException("이미 존재하는 교육장 명입니다. ");
		}
		return true;
	}

	
	// 교육장 내에 강의실 명 중복 검사 메소드
	public boolean validationDuplicatedTrname(int ecno, String trname) {
		int val = trainingRoomDao.selectCntByTrname(ecno, trname);
		
		if(val == 1) {
			throw new RuntimeException("이미 존재하는 강의실입니다. ");
		}
		
		return true;
	}
	
	// 교육과정명 중복 검사 메소드 
	public boolean validationDuplicatedCname(String ecname, String cname) {
		EduCenter center = educenterDao.selectByEcname(ecname);
		int ecno = center.getEcno();
		
		if(courseResponseDao.selectByEcnoAndCname(ecno, cname) != null) {
			throw new RuntimeException("이미 존재하는 교육과정입니다.");
		}
		
		return true;
	}
	
	// trno 배정 가능여부 검사 메소드
	public boolean validationTrenable(CreateCourseRequestDTO request) {
		String ecname = request.getEcname();
		int trno = request.getTrno();
		
		EduCenter center = educenterDao.selectByEcname(ecname);
		int ecno = center.getEcno();
		
		// trno 에 해당하는 진행예정 교육과정 조회 
		List<CourseResponseDTO> data = courseResponseDao.selectByEcnoAndTrno(ecno, trno);
		
		boolean answer = false;
		if (data.size() == 0) {
			return true;
		} else {
			// cstartdate 와 cenddate 비교
			for (int i = 0; i < data.size(); i++) {
				CourseResponseDTO courseData = data.get(i);
				
				// String format = "yyyy-MM-dd";
				// SimpleDateFormat sdf = new SimpleDateFormat(format);
				
				// A
				// String courseCstartdate = sdf.format(courseData.getCstartdate());
				// String courseCenddate = sdf.format(courseData.getCenddate());
				String courseCstartdate = courseData.getCstartdate().substring(0, 10);
				String courseCenddate = courseData.getCenddate().substring(0, 10);
				
				// B
				String strCstartdate = request.getCstartdate().substring(0, 10);
				String strCenddate = request.getCenddate().substring(0, 10);
				
				// DateTimeFormatter 객체를 생성하여 원하는 출력 포맷 지정
		        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		        // LocalDate 객체로 변환
		        LocalDate dataCstartdate = LocalDate.parse(courseCstartdate, formatter);
		        LocalDate dataCenddate = LocalDate.parse(courseCenddate, formatter);
		        
		        LocalDate cstartdate = LocalDate.parse(strCstartdate, formatter);
		        LocalDate cenddate = LocalDate.parse(strCenddate, formatter);
				
				// cstartdate 와 cenddate 비교
				// isBefore, isAfter: LocalDate 객체가 인수보다 이전, 이후 또는 동일한지 비교하며 Boolean 값을 반환
				boolean result = cstartdate.isBefore(dataCstartdate);
				boolean result2 = cstartdate.isAfter(dataCstartdate);

				if (result) {
					boolean temp = cenddate.isBefore(dataCstartdate);
					if (temp) {
						answer = true;
					} else {
						throw new RuntimeException("해당 강의실을 사용할 수 없습니다. 진행 예정인 교육과정이 존재합니다.");
					}
				} else {
					boolean temp = dataCenddate.isBefore(cstartdate);
					if (temp) {
						answer = true;
					} else {
						throw new RuntimeException("해당 강의실을 사용할 수 없습니다. 진행 예정인 교육과정이 존재합니다.");
					}
				}
			}
			if (answer) {
				return true;
			}
		}
		return true;
	}
	
	// 사용자 이름 존재 여부 검사 
	public boolean validationExistsByMname(String mname) {
		
		if(memberDao.selectByMname(mname) == null) {
			throw new RuntimeException("존재하지 않는 이름입니다.");
		}
		
		return true;
	}
	
	// cno 가 유효한지 검사하는 메소드
	public boolean validationExistsByCno(int cno) {
		if (courseDao.selectByCno(cno) == null) {
			throw new RuntimeException("존재하지 않는 교육과정입니다. ");
		}
		return true;
	}
	
}
