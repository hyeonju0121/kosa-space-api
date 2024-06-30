package com.mycompany.kosa_space.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mycompany.kosa_space.dao.EduAttachDao;
import com.mycompany.kosa_space.dao.EduCenterDao;
import com.mycompany.kosa_space.dao.TrainingRoomDao;
import com.mycompany.kosa_space.dto.EduAttach;
import com.mycompany.kosa_space.dto.EduCenter;
import com.mycompany.kosa_space.dto.TrainingRoom;
import com.mycompany.kosa_space.dto.request.CreateEduCenterRequestDTO;
import com.mycompany.kosa_space.dto.request.CreateTrainingRoomRequestDTO;

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
	
	/*
	 * 교육장 ecno 기준으로 단건 조회
	 */
	public EduCenter infoCenter(int ecno) {
		// ecno 유효한지 검증
		validationExistsByEcno(ecno);
		
		return educenterDao.selectByEcno(ecno);
	}
	
	/*
	 * 교육장 전체 조회
	 */
	public List<EduCenter> listCenter() {
		
		return educenterDao.selectAllCenter();
	}
	
	/*
	 * 교육장 ecno 기준으로 수정
	 */
	@Transactional
	public void updateCenter(int ecno, CreateEduCenterRequestDTO request) {
		// ecno 유효한지 검증
		validationExistsByEcno(ecno);
		
		// 교육장 명 중복 여부 검사
		validationDuplicatedEcname(request.getEcname());
		
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
		 			eduAttachDao.updateEduCenterByEano(eano, attach);
		 			
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
		 			eduAttachDao.updateEduCenterByEano(attach.getEano(), attach);
		 			
		 			data.remove(i);
				}
				// 남은 첨부파일 목록 DB 에서 삭제하기
				for (int i = 0; i < data.size(); i++) {
					EduAttach attach = data.get(i);
					int eano = attach.getEano();
					
					// 기존에 남아있던 첨부파일을 DB 에서 delete 처리
					eduAttachDao.deleteEduCenterByEano(eano);
				}
			}
		}
		
		// update
		educenterDao.update(ecno, request);
	}
	
	/*
	 * 교육장 ecno 기준으로 단건 삭제
	 */
	@Transactional
	public void deleteCenter(int ecno) {
		// ecno 유효한지 검증
		validationExistsByEcno(ecno);
		
		// EduAttach DB delete 처리
		eduAttachDao.deleteEduCenterByEcno(ecno);
		
		// EduCenter DB delete 처리
		educenterDao.deleteByEcno(ecno);
	}
	
	/*
	 * 교육장 이름 전체 조회
	 */
	public List<String> listCenterName() {
		return educenterDao.selectAllCenterName();
	}
	

	// 강의실 관련 ------------------------------------------------
	/**
	 * 강의실 등록 ********************
	 * 1) request 로 넘어온 강의실 명이
	 * 교육장 내에 중복으로 존재하는 강의실 명일 경우 에러 반환
	 */
	public void createRoom(CreateTrainingRoomRequestDTO room) {
		// 첨부가 넘어왔을 경우 처리
		if (room.getTrattachdata() != null && !room.getTrattachdata().isEmpty()) {
			MultipartFile mf = room.getTrattachdata();
			// 파일 이름 설정
			room.setTrattachoname(mf.getOriginalFilename());
			// 파일 종류 설정
			room.setTrattachtype(mf.getContentType());
			try {
				// 파일 데이터 설정
				room.setTrattach(mf.getBytes());
			} catch (IOException e) {
			}
		}
		
		// 강의실 명 중복 여부 검사 ---------
		// 교육장 정보 가져오기
		EduCenter center = educenterDao.selectByEcname(room.getEcname());
		
		int ecno = center.getEcno();
		
		validationDuplicatedTrname(ecno, room.getTrname());
		
		TrainingRoom response = TrainingRoom.builder()
				.ecno(ecno)
				.trname(room.getTrname())
				.trcapacity(room.getTrcapacity())
				.trenable(room.isTrenable())
				.trattach(room.getTrattach())
				.trattachoname(room.getTrattachoname())
				.trattachtype(room.getTrattachtype())
				.build();
		
		log.info(response.toString());

		// 강의실 객체 DB에 저장
		trainingRoomDao.insert(response);
	}
	
	/**
	 * 강의실 단건조회 ***********************************
	 */
	public TrainingRoom infoRoom(int trno) {
		TrainingRoom response = trainingRoomDao.selectByTrno(trno);
		if (response == null) {
			throw new RuntimeException("강의실이 존재하지 않습니다.");
		}
		
		return response;	
	}
	
	/*
	 * 교육장 이름으로 강의실 목록 조회 ********************
	 */
	public List<TrainingRoom> listRoom(String ecname) {
		EduCenter center = educenterDao.selectByEcname(ecname);
		
		// 존재하지 않는 교육장명일 경우 에러 처리
		if (educenterDao.selectByEcname(ecname) == null) {
			throw new RuntimeException("존재하지 않는 교육장 이름입니다.");
		}
		
		return trainingRoomDao.selectByEcno(center.getEcno());
	}
	
	
	/*
	 * 강의실 수정 ***************************************
	 */
	public void updateRoom(int trno, CreateTrainingRoomRequestDTO requestRoom) {
		// trno 에 해당하는 강의실 정보 가져오기
		TrainingRoom room = trainingRoomDao.selectByTrno(trno);
		
		// 강의실 명이 이미 존재하면 에러 처리
		validationDuplicatedTrname(room.getEcno(), requestRoom.getTrname());
	
		// 첨부가 넘어왔을 경우 처리
		if (requestRoom.getTrattachdata() != null && !requestRoom.getTrattachdata().isEmpty()) {
			MultipartFile mf = requestRoom.getTrattachdata();
			// 파일 이름 설정
			requestRoom.setTrattachoname(mf.getOriginalFilename());
			// 파일 종류 설정
			requestRoom.setTrattachtype(mf.getContentType());
			try {
				// 파일 데이터 설정
				requestRoom.setTrattach(mf.getBytes());
			} catch (IOException e) {
			}
		}
		
		log.info(room.toString());
		
		trainingRoomDao.update(trno, requestRoom);
	}
	
	/*
	 * 강의실 전체 목록 조회 ***************************************
	 */
	public List<TrainingRoom> listAllRoom() {

		return trainingRoomDao.selectAllRoom();
	}
	
	/*
	 * 강의실 trno 기준으로 단건삭제 ***************************************
	 */
	public void deleteRoom(int trno) {
		trainingRoomDao.deleteByTrno(trno);
	}
	
	
	/*
	 * ecno 가 유효한지 검사하는 메소드 
	 */
	public boolean validationExistsByEcno(int ecno) {
		if (educenterDao.selectByEcno(ecno) == null) {
			throw new RuntimeException("존재하지 않는 교육장입니다. ");
		}
		return true;
	}
	
	/**
	 * 교육장 명 중복 검사 메소드
	 * @param ecno 교육장 식별번호
	 * @param ecname 교육장 명
	 * @return 
	 */
	public boolean validationDuplicatedEcname(String ecname) {
		if (educenterDao.selectByEcname(ecname) != null) {
			throw new RuntimeException("이미 존재하는 교육장 명입니다. ");
		}
		return true;
	}
	
	/**
	 * 교육장 내에 강의실 명 중복 검사 메소드
	 * @param ecno 교육장 식별번호
	 * @param trname 강의실 명
	 * @return 
	 */
	public boolean validationDuplicatedTrname(int ecno, String trname) {
		int val = trainingRoomDao.selectCntByTrname(ecno, trname);
		
		if(val == 1) {
			throw new RuntimeException("이미 존재하는 강의실입니다. ");
		}
		
		return true;
	}
	
}
