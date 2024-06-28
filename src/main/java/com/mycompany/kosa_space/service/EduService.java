package com.mycompany.kosa_space.service;

import java.io.IOException;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mycompany.kosa_space.dao.EduCenterDao;
import com.mycompany.kosa_space.dao.TrainingRoomDao;
import com.mycompany.kosa_space.dto.EduCenter;
import com.mycompany.kosa_space.dto.TrainingRoom;
import com.mycompany.kosa_space.dto.request.CreateTrainingRoomRequestDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EduService {
	@Autowired
	private EduCenterDao educenterDao;

	@Autowired
	private TrainingRoomDao trainingRoomDao;

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
