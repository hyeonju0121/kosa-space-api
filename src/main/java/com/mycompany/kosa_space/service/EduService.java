package com.mycompany.kosa_space.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mycompany.kosa_space.dao.AttendanceDao;
import com.mycompany.kosa_space.dao.CourseDao;
import com.mycompany.kosa_space.dao.CourseResponseDao;
import com.mycompany.kosa_space.dao.EduAttachDao;
import com.mycompany.kosa_space.dao.EduCenterDao;
import com.mycompany.kosa_space.dao.MemberDao;
import com.mycompany.kosa_space.dao.TraineeInfoDao;
import com.mycompany.kosa_space.dao.TrainingRoomDao;
import com.mycompany.kosa_space.dto.Attendance;
import com.mycompany.kosa_space.dto.Course;
import com.mycompany.kosa_space.dto.EduAttach;
import com.mycompany.kosa_space.dto.EduCenter;
import com.mycompany.kosa_space.dto.Member;
import com.mycompany.kosa_space.dto.Pager;
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
import com.mycompany.kosa_space.dto.response.DashBoardAttendanceDTO;
import com.mycompany.kosa_space.dto.response.DashBoardResponseDTO;
import com.mycompany.kosa_space.dto.response.EduCenterResponseDTO;
import com.mycompany.kosa_space.dto.response.TraineeProfileHeaderResposneDTO;
import com.mycompany.kosa_space.dto.response.TraineeResponseDto;
import com.mycompany.kosa_space.dto.response.TrainingRoomListResponseDTO;

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

	@Autowired
	private TraineeInfoDao traineeInfoDao;
	
	@Autowired
	private AttendanceDao attendanceDao;

	// 교육장 관련 ------------------------------------------------
	/*
	 * 교육장 등록 1. 교육장 명 중복 여부 검사 진행 -> 존재한다면, 에러 처리 2. 도로명 주소 + 상세주소 합쳐서 address 변수에
	 * 저장 3. EduCenter 객체 생성 후, request 내에서 값을 꺼내 세팅 후, DB에 저장 3. 첨부파일로 들어온 갯수만큼 반복문
	 * 내에서 EduAttach 객체 생성 후, eaattach, eaattachoname, eaattachtype 세팅하고 DB에 저장하는
	 * 방식으로 구현함
	 */
	@Transactional
	public void createCenter(CreateEduCenterRequestDTO request) {
		// 교육장 명 중복 여부 검사
		validationDuplicatedEcname(request.getEcname());

		// 도로명 주소 + 상세주소 합치기
		String address = request.getEcaddress() + ", " + request.getEcdetailaddress();

		// EduCenter 객체 생성
		EduCenter center = EduCenter.builder().ecname(request.getEcname()).ecpostcode(request.getEcpostcode())
				.ecaddress(address).build();

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
	public EduCenterResponseDTO infoCenter(int ecno) {
		// ecno 유효한지 검증
		validationExistsByEcno(ecno);

		EduCenter center = educenterDao.selectByEcno(ecno);

		List<Integer> eanoList = new ArrayList<>();

		// 각 ecno 에 해당하는 eano 정보 가져오기
		List<EduAttach> list = eduAttachDao.selectEduCenterByEcno(ecno);
		for (EduAttach attach : list) {
			eanoList.add(attach.getEano());
		}

		// 주소 분리
		String[] arr = center.getEcaddress().split(",");
		String ecaddress = arr[0];
		String ecdetailaddress = arr[1].substring(1, arr[1].length());

		// 생성일시, 수정일시 Date to String
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date createdat = center.getEccreatedat();
		Date updatedat = new Date();
		String ecupdatedat = "";
		if (center.getEcupdatedat() != null) {
			updatedat = center.getEcupdatedat();
			ecupdatedat = sdf.format(updatedat);
		}
		String eccreatedat = sdf.format(createdat);

		EduCenterResponseDTO response = EduCenterResponseDTO.builder().ecno(ecno).ecname(center.getEcname())
				.ecpostcode(center.getEcpostcode()).ecaddress(ecaddress).ecdetailaddress(ecdetailaddress)
				.eccreatedat(eccreatedat).ecupdatedat(ecupdatedat).eanoList(eanoList).build();

		return response;
	}

	// 교육장 전체 조회
	public List<EduCenterResponseDTO> listCenter() {

		List<EduCenterResponseDTO> response = new ArrayList<>();

		List<EduCenter> centerList = educenterDao.selectAllCenter();

		for (EduCenter center : centerList) {
			// log.info("center: " + center.toString());

			List<Integer> eanoList = new ArrayList<>();

			int ecno = center.getEcno();

			// 각 ecno 에 해당하는 eano 정보 가져오기
			List<EduAttach> list = eduAttachDao.selectEduCenterByEcno(ecno);
			for (EduAttach attach : list) {
				eanoList.add(attach.getEano());
			}

			// 주소 분리
			String[] arr = center.getEcaddress().split(",");
			String ecaddress = arr[0];
			String ecdetailaddress = arr[1].substring(1, arr[1].length());

			// 생성일시, 수정일시 Date to String
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Date createdat = center.getEccreatedat();
			Date updatedat = new Date();
			String ecupdatedat = "";
			if (center.getEcupdatedat() != null) {
				updatedat = center.getEcupdatedat();
				ecupdatedat = sdf.format(updatedat);
			}
			String eccreatedat = sdf.format(createdat);

			EduCenterResponseDTO result = EduCenterResponseDTO.builder().ecno(ecno).ecname(center.getEcname())
					.ecpostcode(center.getEcpostcode()).ecaddress(ecaddress).ecdetailaddress(ecdetailaddress)
					.eccreatedat(eccreatedat).ecupdatedat(ecupdatedat).eanoList(eanoList).build();

			// log.info("result: " + result.toString());

			response.add(result);
		}

		return response;
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

		// ecno 에 해당하는 첨부파일 전체 가져오기
		List<EduAttach> data = eduAttachDao.selectEduCenterByEcno(ecno);

		if (request.getEcattachdata() == null) { // 첨부파일이 들어오지 않은 경우
			// update
			educenterDao.update(ecno, request);
		} else {
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

				// update
				educenterDao.update(ecno, request);
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

				// update
				educenterDao.update(ecno, request);
			}
		}
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
		List<String> response = new ArrayList<>();
		response.add("전체");

		List<String> data = educenterDao.selectAllCenterName();
		for (String temp : data) {
			response.add(temp);
		}
		return response;
	}

	// 강의실 관련 ------------------------------------------------
	/**
	 * 강의실 등록 ******************** 1) request 로 넘어온 강의실 명이 교육장 내에 중복으로 존재하는 강의실 명일
	 * 경우 에러 반환
	 */
	@Transactional
	public void createRoom(CreateTrainingRoomRequestDTO room) {
		// 강의실 명 중복 여부 검사
		// 교육장 정보 가져오기
		EduCenter center = educenterDao.selectByEcname(room.getEcname());

		int ecno = center.getEcno();

		validationDuplicatedTrname(ecno, room.getTrname());

		// TrainingRoom 객체 생성
		TrainingRoom response = TrainingRoom.builder().ecno(ecno).trname(room.getTrname())
				.trcapacity(room.getTrcapacity()).trenable(room.isTrenable()).build();

		log.info(response.toString());

		// 강의실 객체 DB에 저장
		trainingRoomDao.insert(response);

		// 저장된 TrainingRoom trno 를 찾고, 변수에 저장
		int trno = trainingRoomDao.selectByEcnoAndTrname(response.getEcno(), response.getTrname());
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
	@Transactional
	public List<TrainingRoomListResponseDTO> listRoom(List<String> request) {
		String ecname = request.get(0);
		
		EduCenter center = educenterDao.selectByEcname(ecname);

		// 존재하지 않는 교육장명일 경우 에러 처리
		if (educenterDao.selectByEcname(ecname) == null) {
			throw new RuntimeException("존재하지 않는 교육장 이름입니다.");
		}

		// 전체, 사용중, 사용가능에 대한 조건절을 적용하여 조회한 결과
		List<TrainingRoom> data = new ArrayList<>();
		boolean trenable = false;

		int ecno = center.getEcno();

		// "all", "사용중", "사용가능"
		String trenableStr = request.get(1);
		if (trenableStr.equals("all")) {
			data = trainingRoomDao.selectByEcno(ecno);
		} else if (trenableStr.equals("사용중")) {
			trenable = true;
			data = trainingRoomDao.selectByEcnoAndTrenable(ecno, trenable);
		} else {
			data = trainingRoomDao.selectByEcnoAndTrenable(ecno, trenable);
		}

		List<TrainingRoomListResponseDTO> response = new ArrayList<>();

		log.info("data: " + data.toString());
		for (TrainingRoom temp : data) {
			// 해당 ecnam과 trno 에 해당하는 진행중인 교육과정 정보 가져오기
			CourseResponseDTO course = courseResponseDao.listByEcnameAndTrno(ecname, temp.getTrno());
			
			String cname = "";
			String cstartdate = "";
			String cenddate = "";
			if (course != null) { // 진행중인 교육과정이 존재하는 경우
				log.info("진행중인 교육과정 : " + course.toString());
				cname = course.getCname();
				cstartdate = course.getCstartdate().substring(0, 10);
				cenddate = course.getCenddate().substring(0, 10);
			}

			// trenableResult 세팅 필요
			String trenableResult = "";
			if (temp.isTrenable()) {
				trenableResult = "사용중";
			} else {
				trenableResult = "사용가능";
			}

			// trcreatedat, trupdatedat 세팅 필요
			// 생성일시, 수정일시 Date to String
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Date createdat = temp.getTrcreatedat();
			Date updatedat = new Date();
			String trupdatedat = "";
			if (temp.getTrupdatedat() != null) {
				updatedat = temp.getTrupdatedat();
				trupdatedat = sdf.format(updatedat);
			}
			String trcreatedat = sdf.format(createdat);

			// eanoList 생성
			List<Integer> eanoList = new ArrayList<>();

			int trno = temp.getTrno();

			// 각 ecno 에 해당하는 eano 정보 가져오기
			List<EduAttach> list = eduAttachDao.selectTrainingRoomByTrno(trno);
			for (EduAttach attach : list) {
				eanoList.add(attach.getEano());
			}

			TrainingRoomListResponseDTO roomData = TrainingRoomListResponseDTO.builder()
					.trno(temp.getTrno())
					.trname(temp.getTrname())
					.ecno(center.getEcno())
					.ecname(ecname)
					.cname(cname)
					.cstartdate(cstartdate)
					.cenddate(cenddate)
					.trcapacity(temp.getTrcapacity())
					.trenable(temp.isTrenable())
					.trenableResult(trenableResult)
					.trcreatedat(trcreatedat)
					.trupdatedat(trupdatedat)
					.eanoList(eanoList)
					.build();

			response.add(roomData);
		}

		return response;
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

		if (request.getTrattachdata() == null) { // 첨부파일이 들어오지 않은 경우
			// update
			trainingRoomDao.update(trno, request);
		} else {
			if (attachList.size() > 0) { // 첨부파일이 있는 경우
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
						// int trnoNum = attach.getTrno();
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

					// update
					trainingRoomDao.update(trno, request);
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

					// update
					trainingRoomDao.update(trno, request);
				}
			}
		}
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

	// 교육과정 등록
	@Transactional
	public void createCourse(CreateCourseRequestDTO request) {
		// 교육과정명 중복 검사 (교육장명&교육진행상태 여부가 진행중 및 진행예정이면 에러처리)
		validationDuplicatedCname(request.getEcname(), request.getCname());

		// 존재하는 강의실 이름 인지 검사
		TrainingRoom room = trainingRoomDao.selectByTrname(request.getTrname());
		if (room == null) {
			throw new RuntimeException("존재하지 않는 강의실 명입니다.");
		}
		
		// trno 배정 가능한지 검사
		validationTrenable(request);

		// 운영진, 강사진 존재여부 검사
		validationExistsByMname(request.getCmanager());
		validationExistsByMname(request.getCprofessor());

		// Course 객체 생성
		Course course = Course.builder()
				.trno(room.getTrno())
				.cname(request.getCname())
				.ccode(request.getCcode())
				.ctotalnum(request.getCtotalnum())
				.cstatus("진행예정")
				.cstartdate(request.getCstartdate())
				.cenddate(request.getCenddate())
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
		int cno = courseDao.selectByCnameAndCstatus(room.getTrno(), course.getCstatus(), request.getCname());

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

		// trno 강의실 사용여부 조사
		TrainingRoom room = trainingRoomDao.selectByTrno(request.getTrno());
		boolean trenable = room.isTrenable();

		// cno 에 해당하는 교육과정 정보 가져오기
		Course course = courseDao.selectByCno(cno);

		String cstatus = request.getCstatus();

		switch (cstatus) {
		case "진행중":
			// trno 에 해당하는 강의실 trenable 값 검사 수행
			if (trenable) {
				throw new RuntimeException("해당 강의실을 사용할 수 없습니다." + "이미 교육과정이 진행중입니다.");
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
			/*
			if (trenable) {
				throw new RuntimeException("해당 강의실을 사용할 수 없습니다." + "이미 다른 교육과정이 진행중입니다. 진행완료로 변경하실 수 없습니다.");
			}
			*/

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
		Course courseData = Course.builder().trno(request.getTrno()).cname(request.getCname()).ccode(request.getCcode())
				.ctotalnum(request.getCtotalnum()).cstartdate(request.getCstartdate()).cenddate(request.getCenddate())
				.crequireddate(request.getCrequireddate()).cstatus(request.getCstatus())
				.cprofessor(request.getCprofessor()).cmanager(request.getCmanager())
				.ctrainingdate(request.getCtrainingdate()).ctrainingtime(request.getCtrainingtime()).build();

		log.info(courseData.toString());

		// 첨부파일이 넘어왔을 경우 처리
		List<MultipartFile> attachList = request.getCattachdata();

		// cno 에 해당하는 첨부파일 전체 가져오기
		List<EduAttach> data = eduAttachDao.selectCourseByCno(cno);
		
		if (attachList == null) {
			// Course 객체 DB 업데이트
			courseDao.update(cno, courseData);
		} else {
			if (attachList.size() > 0) { // 첨부파일이 있는 경우
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
					// Course 객체 DB 업데이트
					courseDao.update(cno, courseData);

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
					// Course 객체 DB 업데이트
					courseDao.update(cno, courseData);
				}
			} 
			
		}
	

	}

	// 교육과정 cno 기준으로 단건조회
	public CourseResponseDTO infoCourse(int cno) {
		// cno 가 유효한지 검사
		validationExistsByCno(cno);

		CourseResponseDTO response = courseResponseDao.selectByCno(cno);

		List<Integer> eanoList = new ArrayList<>();

		// 각 cno 에 해당하는 eano 정보 가졍괴
		List<EduAttach> list = eduAttachDao.selectCourseByCno(cno);
		for (EduAttach attach : list) {
			eanoList.add(attach.getEano());
		}

		String cstartdate = response.getCstartdate().substring(0, 10);
		String cenddate = response.getCenddate().substring(0, 10);

		response.setEanoList(eanoList);
		response.setCstartdate(cstartdate);
		response.setCenddate(cenddate);

		log.info(response.toString());

		return response;
	}

	// 파라미터 기준으로 교육과정 조회
	public Map<String, Object> listCourse(CourseParameterRequestDTO request, int pageNo) {
		log.info("request: " + request);

		int totalRows = courseResponseDao.selectCntlistByParameter(request);
		log.info("totalRows: " + totalRows); 
		
		Pager pager = new Pager(3, 10, totalRows, pageNo);
		
		List<CourseResponseDTO> response = courseResponseDao.listByParameter(request, pager);
		log.info("response.size: " + response.size());
		
		for (CourseResponseDTO course : response) {
			log.info("course: " + course.toString());

			EduCenter center = educenterDao.selectByEcno(course.getEcno());
			course.setEcname(center.getEcname());

			List<Integer> eanoList = new ArrayList<>();

			int cno = course.getCno();

			// 각 cno 에 해당하는 eano 정보 가져오기
			List<EduAttach> list = eduAttachDao.selectCourseByCno(cno);
			for (EduAttach attach : list) {
				eanoList.add(attach.getEano());
			}
			course.setEanoList(eanoList);

			// cstartdate, cenddate 세팅
			String cstartdate = course.getCstartdate().substring(0, 10);
			String cenddate = course.getCenddate().substring(0, 10);

			course.setCstartdate(cstartdate);
			course.setCenddate(cenddate);
		}
		log.info("response: " + response);

		Map<String, Object> map = new HashMap<>();
		map.put("courseInfo", response);
		map.put("pager", pager);
		
		return map;
	} 
	
	// ecname 기준으로 존재하는 교육과정 명 리스트 조회
	public List<String> listCnameCourse(String ecname) {
		List<String> response = courseResponseDao.listByEcname(ecname);
		
		return response;
	}
	
	// ecname 기준으로 진행중인 교육과정 명 리스트 조회
	public List<String> listCnameCourseInProgress(String ecname) {
		List<String> response = courseResponseDao.listInProgressByEcname(ecname);
		return response;
	}
	
	// ecname 기준으로 강사진 명 리스트 조회
	public List<String> listCprofessor(String ecname) {
		List<String> response = courseResponseDao.listCprofessorByEcname(ecname);
		return response;
	}

	// ecname 기준으로 강의실 목록 리스트 조회
	public List<String> listNameList(String ecname) {
		List<String> response = trainingRoomDao.listTrnameByEcname(ecname);
		return response;
	}
	
	// --- 첨부파일 다운로드 ---------------------------------------------------------
	public EduAttach attachDownload(int eano) {
		EduAttach attach = eduAttachDao.selectByEano(eano);

		int ecno = attach.getEcno();
		int trno = attach.getTrno();
		int cno = attach.getCno();

		if (ecno > 0) {
			return eduAttachDao.selectEduCenterByEano(eano);
		} else if (trno > 0) {
			return eduAttachDao.selectTrainingRoomByEano(eano);
		} else if (cno > 0) {
			return eduAttachDao.selectCourseByEano(eano);
		}
		return null;
	}

	// 교육과정 관련 ------------------------------------------------

	// 교육생 등록 (성민)
	@Transactional
	public void createTrainee(CreateTraineeRequestDto request) {

		// 프론트 단에서 데이터 유효성을 거쳐 받은 데이터로 가정.
		// 해당 교육생의 mid 만들어주기. --> 등록되어있는 ccode에서 가져오면 됨
		// cname을 넘기고, 현재 등록되어 있는 인원수 세기 --> 현재 교육생을 등록하는 과정이기 때문에 조회를 못함
		int cnt = traineeInfoDao.readTraineeCnt(request.getCname()) + 1;
		log.info(String.valueOf(cnt)); // O
		// cno, ccode, ctotalnum, cstatus 값 가져오기.
		Course course = courseDao.readCourse(request.getCname());
		log.info(course.toString()); // O

		if (cnt < course.getCtotalnum()) {
			// mid생성. "연도 + 과정코드 + 등록순" -> ex) "2024M2001"
			String formatter = String.format("%03d", cnt);
			String mid = course.getCcode() + formatter;
			log.info("mid = " + mid);

			PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

			// member에 값 넣어주기 --> builder()이용
			Member member = Member.builder().mid(mid).mpassword(passwordEncoder.encode("12345"))
					.mname(request.getMname()).mphone(request.getMphone()).memail(request.getMemail())
					.mrole("ROLE_USER").menable(true).build();

			// DB에 값 넣기.
			memberDao.insert(member);
			log.info("member 삽입 성공");

			// traineeinfo에 값 넣어주기
			TraineeInfo traineeInfo = TraineeInfo.builder().mid(mid).cno(course.getCno()).tsex(request.isTsex())
					.tage(request.getTage()).tpostcode(request.getTpostcode())
					.taddress(request.getTaddress() + ", " + request.getTaddressdetail()).tfield(request.isTfield())
					.tacademic(request.getTacademic()).tschoolname(request.getTschoolname()).tmajor(request.getTmajor())
					.tminor(request.getTminor()).tgrade(request.getTgrade()).tstatus(course.getCstatus()).build();

			// 프로필 이미지 첨부는 필수이기 때문에 if문을 쓸 이유가 없다. 추후에 바꿔보자.
			if (request.getTprofiledata() != null && !request.getTprofiledata().isEmpty()) {
				// 첨부파일이 넘어왔을 경우 처리
				MultipartFile mf = request.getTprofiledata();
				// 파일 이름을 설정
				traineeInfo.setTprofileoname(mf.getOriginalFilename());
				// 파일 종류를 설정
				traineeInfo.setTprofiletype(mf.getContentType());
				try {
					// 파일 데이터를 설정
					traineeInfo.setTprofileimg(mf.getBytes());
				} catch (IOException e) {
				}
			}

			// DB에 값 넣기.
			traineeInfoDao.insert(traineeInfo);
			log.info("traineeInfo 삽입 성공");
		}

	}

	// 교육생 단건 조회 (성민)
	public TraineeResponseDto infoTrainee(String mid) {
		log.info("infoTrainee 서비스 실행");
		log.info("mid = " + mid);

		Member member = memberDao.selectByMid(mid);
		log.info("member = " + member.toString());

		TraineeInfo traineeInfo = traineeInfoDao.selectByMid(mid);
		//log.info("traineeInfo = " + traineeInfo.toString());

		// 조인문 만들어서 단번에 TraineeResponseDTO 객체에 삽입하여 반환할 수 있는지?

		TraineeResponseDto response = traineeInfoDao.detailInfo(mid);
		
		// Taddrees와 TaddressDetail을 나누기 위한 임시 문자열 배열 변수
		if(response.getTaddress().contains(",")) {
			String[] address = response.getTaddress().split(",");
			response.setTaddress(address[0].trim());
			response.setTaddressdetail(address[1].trim());
		} else if(response.getTaddress().contains("null")) {
			int temp = response.getTaddress().indexOf("null");
			log.info("temp : " + temp);
			response.setTaddress(response.getTaddress().substring(0, temp));
			response.setTaddressdetail("");
		}
		
		log.info("response 데이터 = " + response.toString());

		return response;
	}

	// 교육생 수정 (성민)
	@Transactional
	public void updateTrainee(String mid, UpdateTraineeRequestDto request) {
		log.info("updateTrainee 실행");
		log.info("mid = " + mid);
		log.info("request = " + request);

		// 값에 예외가 있는지는 우선 프론트에서 유효성을 검사.
		// 7.5 현재는 수정버튼을 눌렀을 때, 조회된 값을 띄워주고 그 부분에 변경된 값을 넣어주기만 한다.

		if (request.getTprofiledata() != null && !request.getTprofiledata().isEmpty()) {
			// 첨부파일이 넘어왔을 경우 처리
			MultipartFile mf = request.getTprofiledata();
			// 파일 이름을 설정
			request.setTprofileoname(mf.getOriginalFilename());
			// 파일 종류를 설정
			request.setTprofiletype(mf.getContentType());
			try {
				// 파일 데이터를 설정
				request.setTprofileimg(mf.getBytes());
			} catch (IOException e) {
			}
		}

		log.info("traineeInfoDao.updateTrainee(request) 실행 전");
		traineeInfoDao.updateTrainee(request);
		log.info("traineeInfoDao.updateTrainee(request) 실행 완료");

		log.info("memberDao.updateTrainee(request) 실행 전");
		memberDao.updateTrainee(request);
		log.info("memberDao.updateTrainee(request) 실행 완료");

	}

	// 교육생 (교육장, 교육과정) 목록 조회 (성민)
	public List<TraineeResponseDto> listTrainee(String ecname, String cname) {

		List<TraineeResponseDto> response = traineeInfoDao.listTraineeByEcnameAndCname(ecname, cname);
		
		// taddress , detailaddress 분리
		log.info("response.size(): " + response.size());
		return response;
	}
	
	// 교육생 이미지 첨부파일 정보 받아오기 (성민)
	public TraineeInfo tattachDownload(String mid) {
		TraineeInfo traineeInfo = traineeInfoDao.selectByMid(mid);
		//log.info("traineeInfo = " + traineeInfo);
		return traineeInfo;
	}
	
	// 교육생 프로필 헤더 (현주)
	public TraineeProfileHeaderResposneDTO getTraineeProfileHeader(String mid) {
		TraineeInfo traineeInfo = traineeInfoDao.selectByMid(mid);
		Member member = memberDao.selectByMid(mid);
		
		int cno = traineeInfo.getCno();
		
		CourseResponseDTO course = courseResponseDao.selectByCno(cno);
		String cname = course.getCname();
		String ecname = course.getEcname();
		String cstartdate = course.getCstartdate().substring(0, 10);
		String cenddate = course.getCenddate().substring(0, 10);
		
		return TraineeProfileHeaderResposneDTO.builder()
				.mid(mid)
				.mname(member.getMname())
				.ecname(ecname)
				.cname(cname)
				.cstartdate(cstartdate)
				.cenddate(cenddate)
				.build();
	}
	
	
	
	// dashboard -------------------------------------------
	// ecname 기준으로 현재 진행중인 교육과정과 완료된 교육과정 카운트 조회 기능
	public CourseDashboardResponseDTO totalCnt(String ecname) {
		
		int inprogresscnt = courseDao.inProgressCountByEcname(ecname).size();
		int scheduledcnt = courseDao.scheduledCountByEcname(ecname).size();
		int completecnt = courseDao.completedCountByEcname(ecname).size();
				
		return CourseDashboardResponseDTO.builder()
				.inprogresscnt(inprogresscnt)
				.scheduledcnt(scheduledcnt)
				.completecnt(completecnt)
				.build();
	}
	
	// ecname 기준으로 교육상태에 따른 교육과정 진행 현황 조회 (페이징 적용)
	public Map<String, Object> getDashboardCourseList(String ecname, 
			String cstatus, int pageNo) {
		
		// 페이징 대상이 되는 전체 행수 얻기
		int totalRows = courseDao.getCountByEcnameAndCstatus(ecname, cstatus);
		
		// 페이지 객체 생성
		Pager pager = new Pager(4, 5, totalRows, pageNo);
		
		// 해당 페이지의 교육과정 정보 가져오기
		List<DashBoardResponseDTO> data = courseDao.getCourseList(ecname, cstatus, pager);
		
		// Map 객체 생성
		Map<String, Object> map = new HashMap<>();
		map.put("course", data);
		map.put("pager", pager);
		
		return map;
	}
	
	// ecname 기준으로 현재 진행중인 교육과정의 교육생 출결 현황 조회
	public Map<String, Object> getDashboardAttendanceList(String ecname, 
			int pageNo, String adate)  throws Exception{
		// ecname 에서 진행하는 교육과정명을 리스트로 가져오기
		List<CourseResponseDTO> courseList = courseResponseDao.listByEcnameAndCstatus(ecname);
		
		// 페이징 대상이 되는 전체 행수 얻기
		int totalRows = courseList.size();
		
		// 페이지 객체 생성
		Pager pager = new Pager(4, 5, totalRows, pageNo);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");        
        Date date= format.parse(adate);
		
        // 데이터 생성 
        List<DashBoardAttendanceDTO> response = new ArrayList<>();
        
		for (CourseResponseDTO data : courseList) {
			int cno = data.getCno();
			
			DashBoardAttendanceDTO dashboard = new DashBoardAttendanceDTO();
			dashboard.setCname(data.getCname());
			dashboard.setTrname(data.getTrname());
			
			// cno 에 해당하는 교육생 전체 출결 정보 가져오기
			List<Attendance> attendanceInfo = attendanceDao.selectAttendanceInfoByAdateAndCno(date, cno);
		
			int totalCheckinCnt = 0;
			int totalCheckoutCnt = 0;
			int totalAbsenceCnt = 0;
			
			for (Attendance temp : attendanceInfo) {
				if (temp.isAcheckinstatus()) {
					totalCheckinCnt++;
				} if (temp.isAcheckoutstatus()) {
					totalCheckoutCnt++;
				} if (!temp.isAcheckinstatus() && !temp.isAcheckoutstatus()){
					totalAbsenceCnt++;
				}
			}
			
			dashboard.setTotalCheckinCnt(totalCheckinCnt);
			dashboard.setTotalCheckoutCnt(totalCheckoutCnt);
			dashboard.setTotalAbsenceCnt(totalAbsenceCnt);
			
			response.add(dashboard);
		}
		
		// Map 객체 생성
		Map<String, Object> map = new HashMap<>();
		map.put("attendanceInfo", response);
		map.put("pager", pager);
		
		return map;
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

		if (val == 1) {
			throw new RuntimeException("이미 존재하는 강의실입니다. ");
		}

		return true;
	}

	// 교육과정명 중복 검사 메소드
	public boolean validationDuplicatedCname(String ecname, String cname) {
		EduCenter center = educenterDao.selectByEcname(ecname);
		int ecno = center.getEcno();

		if (courseResponseDao.selectByEcnoAndCname(ecno, cname) != null) {
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

				String format = "yyyy-MM-dd";
				SimpleDateFormat sdf = new SimpleDateFormat(format);

				// A
				String courseCstartdate = courseData.getCstartdate().substring(0, 10);
				String courseCenddate = courseData.getCenddate().substring(0, 10);

				// B
				String strCstartdate = sdf.format(request.getCstartdate());
				String strCenddate = sdf.format(request.getCenddate());

				// DateTimeFormatter 객체를 생성하여 원하는 출력 포맷 지정
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

				// LocalDate 객체로 변환
				LocalDate dataCstartdate = LocalDate.parse(courseCstartdate, formatter);
				LocalDate dataCenddate = LocalDate.parse(courseCenddate, formatter);

				LocalDate cstartdate = LocalDate.parse(strCstartdate, formatter);
				LocalDate cenddate = LocalDate.parse(strCenddate, formatter);

				log.info("cstartdate: " + cstartdate);
				log.info("cenddate: " + cenddate);

				// cstartdate 와 cenddate 비교
				// isBefore, isAfter: LocalDate 객체가 인수보다 이전, 이후 또는 동일한지 비교하며 Boolean 값을 반환
				boolean result = cstartdate.isBefore(dataCstartdate);

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

		if (memberDao.selectByMname(mname) == null) {
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
