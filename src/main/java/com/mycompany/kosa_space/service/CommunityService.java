package com.mycompany.kosa_space.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.mycompany.kosa_space.dto.Pager;
import com.mycompany.kosa_space.dto.request.CreateCommunityRequestDTO;
import com.mycompany.kosa_space.dto.response.CourseResponseDTO;
import com.mycompany.kosa_space.dto.response.DashBoardNoticeDTO;
import com.mycompany.kosa_space.dto.response.DashBoardResponseDTO;
import com.mycompany.kosa_space.dto.response.NoticeEduCenterCourseCombineDTO;
import com.mycompany.kosa_space.dto.response.NoticeResponseDTO;

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

	// 공지사항 조회
	public Map<String, Object> listNotice(String ecname, int pageNo) {
		EduCenter center = eduCenterDao.selectByEcname(ecname);
		int ecno = center.getEcno();
		
		// 해당 페이지의 공지사항 정보 가져오기
		List<Notice> data = noticeDao.selectNoticeByEcname(ecno);
		int totalRows = data.size();
		
		// Pager 객체 생성
		Pager pager = new Pager(2, 5, totalRows, pageNo);
		
		List<DashBoardNoticeDTO> response = new ArrayList<>();
		for (Notice notice : data) {
			DashBoardNoticeDTO temp = DashBoardNoticeDTO.builder()
					.ncategory(notice.getNcategory())
					.ntitle(notice.getNtitle())
					.ecname(center.getEcname())
					.build();
			
			// 생성일시, 수정일시 Date to String
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date createdat = notice.getNcreatedat();
			String ncreatedat = sdf.format(createdat);
			
			temp.setNcreatedat(ncreatedat);
			
			response.add(temp);
		}
		
		Map<String, Object> map = new HashMap<>();
		map.put("noticeInfo", response);
		map.put("pager", pager);

		return map;
	}
	
	// ecname, cname, ncategory, pageNo 에 따른 공지사항 목록 조회
	public Map<String, Object> listAllNotice(String ecname, String cname, 
			String ncategory, int pageNo) {
		
		// 해당 페이지의 공지사항 정보 가져오기
		List<NoticeEduCenterCourseCombineDTO> data = noticeDao
				.selectNoticeByEcnameAndCnameAndNcategory(ecname, cname, ncategory);
		
		// 페이징 대상이 되는 전체 행수 얻기
		int totalRows = data.size();
		
		// 페이지 객체 생성
		Pager pager = new Pager(10, 10, totalRows, pageNo);
		
		// 해당 페이지의 공지사항 정보 가져오기
		List<NoticeEduCenterCourseCombineDTO> response = noticeDao
				.selectPageNoticeByEcnameAndCnameAndNcategory(ecname, cname, ncategory, pager);
		
		Map<String, Object> map = new HashMap<>();
		map.put("notice", response);
		map.put("pager",  pager);
		
		return map;
	}
	
	// 공지사항 단건조회
	public NoticeResponseDTO detailNotice(int nno) {
		
		NoticeResponseDTO notice = noticeDao.selectByNno(nno);
		// log.info("notice: " + notice);
		
		if (notice.getNattach() != null) {
			notice.setNattach(null);
		}
		
		return notice;
	}
	
	// 공지사항 수정
	public void updateNotice(CreateCommunityRequestDTO request) {
	
	}
	
	// 공지사항 삭제
	public void deleteNotice(int nno) {
		noticeDao.deleteByNno(nno);
	}
	
	
	// 공지사항 첨부파일 다운로드
	public Notice nattachDownload(int nno) {
		return noticeDao.selectNoticeByNno(nno);
	}
}

