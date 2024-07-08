package com.mycompany.kosa_space.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mycompany.kosa_space.dao.CourseResponseDao;
import com.mycompany.kosa_space.dao.EduCenterDao;
import com.mycompany.kosa_space.dao.NoticeDao;
import com.mycompany.kosa_space.dto.EduCenter;
import com.mycompany.kosa_space.dto.Notice;
import com.mycompany.kosa_space.dto.request.CreateCommunityRequestDTO;
import com.mycompany.kosa_space.dto.response.CourseResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommunityService {
	@Autowired
	private NoticeDao noticeDao;
	
	@Autowired
	private EduCenterDao eduCenterDao;
	
	@Autowired
	private CourseResponseDao courseResponseDao;
	
	// 공지사항 등록
	@Transactional
	public void createNotice(CreateCommunityRequestDTO request, 
			Authentication authentication) {
		// mid 세팅
		String mid = authentication.getName();
		log.info("mid: " + mid);
		
		Notice notice = Notice.builder()
				.ncategory(request.getNcategory())
				.ntitle(request.getNtitle())
				.ncontent(request.getNcontent())
				.nhitcount(0)
				.mid(mid)
				.build();
		
		// 첨부가 넘어왔을 경우 처리
		if (request.getNattachdata() != null && !request.getNattachdata().isEmpty()) {
			MultipartFile mf = request.getNattachdata();
			// 파일 이름 설정
			notice.setNattachoname(mf.getOriginalFilename());
			// 파일 종류 설정
			notice.setNattachtype(mf.getContentType());
			try {
				// 파일 데이터 설정
				notice.setNattach(mf.getBytes());
			} catch(IOException e) {
				
			}
		}
		
		if (request.getEcname().equals("전체")) {
			// 교육장명이 전체로 들어온 경우
			// 모든 교육장의 모든 교육과정의 ecno, cno 저장
			List<CourseResponseDTO> data = courseResponseDao.selectAllList();
			for (CourseResponseDTO course : data) {
				int ecno = course.getEcno();
				int cno = course.getCno();
				
				notice.setEcno(ecno);
				notice.setCno(cno);
			
				// DB insert
				noticeDao.insert(notice);
			} 
		} else {
			// 교육장이 전체가 아닌 경우
			String ecname = request.getEcname();
			String cname = request.getCname();
			
			if (cname.equals("전체")) {
				// ecname 이 해당하는 ecno 찾기
				EduCenter center = eduCenterDao.selectByEcname(ecname);
				int ecno = center.getEcno();
				
				// 해당 교육장, 모든 교육과정 공지인 경우
				List<CourseResponseDTO> data = courseResponseDao.selectAllListByEcno(ecno);
				for (CourseResponseDTO course : data) {
					notice.setEcno(ecno);
					notice.setCno(course.getCno());
					
					//log.info("notice: " + notice);
					
					// DB insert
					noticeDao.insert(notice);
				}
			} else {
				// cname 이 전체가 아닌 경우, 특정 교육과정명에 대한 공지
				CourseResponseDTO data = courseResponseDao.selectAllListByCname(cname);
				notice.setEcno(data.getEcno());
				notice.setCno(data.getCno());
				
				//log.info("notice: " + notice);
				
				// DB insert
				noticeDao.insert(notice);
			}
		}
	}

}
