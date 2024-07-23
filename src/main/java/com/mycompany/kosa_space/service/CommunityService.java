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

import com.mycompany.kosa_space.dao.CourseDao;
import com.mycompany.kosa_space.dao.CourseResponseDao;
import com.mycompany.kosa_space.dao.EduCenterDao;
import com.mycompany.kosa_space.dao.NoticeDao;
import com.mycompany.kosa_space.dto.Course;
import com.mycompany.kosa_space.dto.EduCenter;
import com.mycompany.kosa_space.dto.Notice;
import com.mycompany.kosa_space.dto.Pager;
import com.mycompany.kosa_space.dto.request.CreateCommunityRequestDTO;
import com.mycompany.kosa_space.dto.response.CourseResponseDTO;
import com.mycompany.kosa_space.dto.response.DashBoardNoticeDTO;
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
	
	@Autowired
	private CourseDao courseDao;
	
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

	// 공지사항 조회 (운영진 대시보드)
	public Map<String, Object> listNotice(String ecname, int pageNo) {
		EduCenter center = eduCenterDao.selectByEcname(ecname);
		int ecno = center.getEcno();
		
		int totalRows = noticeDao.selectNoticePagerCountByEcno(ecno);
		
		// Pager 객체 생성
		Pager pager = new Pager(10, 5, totalRows, pageNo);
		
		// 해당 페이지의 공지사항 정보 가져오기
		List<Notice> data = noticeDao.selectNoticePagerByEcname(ecno, pager);

		List<DashBoardNoticeDTO> response = new ArrayList<>();
		for (Notice notice : data) {
			
			String name = "";
			if (notice.getEcno() == 0) {
				name = "전체";
			} else {
				name = center.getEcname();
			}
			
			String cname = "";
			if (notice.getCno() == 0) {
				cname = "전체";
			} else {
				Course course = courseDao.selectByCno(notice.getCno());
				cname = course.getCname();
			}
			
			DashBoardNoticeDTO temp = DashBoardNoticeDTO.builder()
					.nno(notice.getNno())
					.ncategory(notice.getNcategory())
					.ntitle(notice.getNtitle())
					.ecname(name)
					.cname(cname)
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
	
	// 공지사항 조회 (교육생 대시보드)
		public Map<String, Object> listTraineeNotice(String ecname, int pageNo) {
			EduCenter center = eduCenterDao.selectByEcname(ecname);
			int ecno = center.getEcno();
			
			int totalRows = noticeDao.selectNoticePagerCountByEcno(ecno);
			
			// Pager 객체 생성
			Pager pager = new Pager(4, 5, totalRows, pageNo);
			
			// 해당 페이지의 공지사항 정보 가져오기
			List<Notice> data = noticeDao.selectNoticePagerByEcname(ecno, pager);

			List<DashBoardNoticeDTO> response = new ArrayList<>();
			for (Notice notice : data) {
				
				String name = "";
				if (notice.getEcno() == 0) {
					name = "전체";
				} else {
					name = center.getEcname();
				}
				
				String cname = "";
				if (notice.getCno() == 0) {
					cname = "전체";
				} else {
					Course course = courseDao.selectByCno(notice.getCno());
					cname = course.getCname();
				}
				
				DashBoardNoticeDTO temp = DashBoardNoticeDTO.builder()
						.nno(notice.getNno())
						.ncategory(notice.getNcategory())
						.ntitle(notice.getNtitle())
						.ecname(name)
						.cname(cname)
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
		
		EduCenter center = eduCenterDao.selectByEcname(ecname);
		
		Course course = courseDao.selectCourseInfoByCname(cname);		
		
		int totalRows = 0;
		
		List<Notice> response = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		
		
		// ecname 이 all 인 경우
		if (ecname.equals("all") && cname.equals("all")) {
			// 교육장과 교육과정 전체 공지 데이터 가져오기 
			if (ncategory.equals("all")) {
				log.info("ecname: " + ecname + ", cname: " + cname + "ncategory: " + ncategory);
				// 모든 교육장에 전체 공지 데이터 가져오기
				// 페이징 대상이 되는 전체 행수 얻기
				totalRows = noticeDao.selectRowsNoticeCategory5();
				
				// 페이지 객체 생성
				Pager pager5 = new Pager(10, 10, totalRows, pageNo);
				
				response = noticeDao.courseNoticeCategory5(ncategory, pager5);
				
				map.put("notice", response);
				map.put("pager",  pager5);

			} else {
				log.info("ecname: " + ecname + ", cname: " + cname + "ncategory: " + ncategory);
				// 모든 교육장에 전체 교육과정의 카테고리에 해당하는 공지 데이터 가져오기
				// 페이징 대상이 되는 전체 행수 얻기
				totalRows = noticeDao.selectRowsNoticeCategory6(ncategory);
				
				// 페이지 객체 생성
				Pager pager6 = new Pager(10, 10, totalRows, pageNo);
				
				response = noticeDao.courseNoticeCategory6(ncategory, pager6);
				
				map.put("notice", response);
				map.put("pager",  pager6);
			}
			
			
		} else if (!ecname.equals("all") && cname.equals("all")) {
			int ecno = center.getEcno();
			
			if (ncategory.equals("all")) {
				log.info("ecname: " + ecname + ", cname: " + cname + "ncategory: " + ncategory);
				// 특정 교육장에 전체 공지 데이터 가져오기
				// 페이징 대상이 되는 전체 행수 얻기
				totalRows = noticeDao.selectRowsNoticeCategory1(ecno);
				
				// 페이지 객체 생성
				Pager pager1 = new Pager(10, 10, totalRows, pageNo);
				
				response = noticeDao.courseNoticeCategory1(ecno, pager1);
				
				map.put("notice", response);
				map.put("pager",  pager1);
			} else {
				log.info("ecname: " + ecname + ", cname: " + cname + "ncategory: " + ncategory);
				// 특정 교육장에 전체 교육과정의 카테고리에 해당하는 공지 데이터 가져오기
				// 페이징 대상이 되는 전체 행수 얻기
				totalRows = noticeDao.selectRowsNoticeCategory2(ecno, ncategory);
				
				// 페이지 객체 생성
				Pager pager2 = new Pager(10, 10, totalRows, pageNo);
								
				response = noticeDao.courseNoticeCategory2(ecno, ncategory, pager2);
				
				map.put("notice", response);
				map.put("pager",  pager2);
			}
			
			
		} else if (!ecname.equals("all") && !cname.equals("all")) {
			int ecno = center.getEcno();
			int cno = course.getCno();
			
			if (ncategory.equals("all")) { // 카테고리가 없는 경우
				log.info("ecname: " + ecname + ", cname: " + cname + "ncategory: " + ncategory);
				// 특정 교육과정에 공지 데이터 가져오기
				
				// 페이징 대상이 되는 전체 행수 얻기
				totalRows = noticeDao.selectRowsNoticeCategory3(ecno, cno);
				log.info("totalRows: " + totalRows);
				
				// 페이지 객체 생성
				Pager pager3 = new Pager(10, 10, totalRows, pageNo);
				
				response = noticeDao.courseNoticeCategory3(ecno, cno, pager3);
				
				map.put("notice", response);
				map.put("pager",  pager3);
			}
			else {
				log.info("ecname: " + ecname + ", cname: " + cname + "ncategory: " + ncategory);
				// 특정 교육과정에 해당하는 카테고리 공지 데이터 가져오기
				// 페이징 대상이 되는 전체 행수 얻기
				totalRows = noticeDao.selectRowsNoticeCategory4(ecno, cno, ncategory);
				
				// 페이지 객체 생성
				Pager pager4 = new Pager(10, 10, totalRows, pageNo);
				
				response = noticeDao.courseNoticeCategory4(ecno, cno, ncategory, pager4);				
				
				map.put("notice", response);
				map.put("pager",  pager4);
			}
		}

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

