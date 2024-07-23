package com.mycompany.kosa_space.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.kosa_space.dto.Notice;
import com.mycompany.kosa_space.dto.request.CreateCommunityRequestDTO;
import com.mycompany.kosa_space.dto.response.NoticeResponseDTO;
import com.mycompany.kosa_space.service.CommunityService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/community")
public class CommunityController {
	@Autowired
	private CommunityService communityService;

	// 공지사항 등록
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@PostMapping("/notice/create")
	public void noticeCreate(CreateCommunityRequestDTO request, Authentication authentication) {
		log.info("request: " + request.toString());
		communityService.createNotice(request, authentication);
	}

	// (운영진 대시보드) ecname 기준으로 공지사항 조회
	@GetMapping("/dashboard/notice/list")
	public Map<String, Object> noticeList(@RequestParam String ecname, @RequestParam(defaultValue = "1") int pageNo) {
		return communityService.listNotice(ecname, pageNo);
	}

	// (교육생 대시보드) ecname 기준으로 공지사항 조회
	@GetMapping("/dashboard/trainee/notice/list")
	public Map<String, Object> traineeNoticeList(@RequestParam String ecname, @RequestParam(defaultValue = "1") int pageNo) {
		return communityService.listTraineeNotice(ecname, pageNo);
	}

	
	// ecname, cname, ncategory, pageNo 에 따른 공지사항 목록 조회
	@PostMapping("/notice/list")
	public Map<String, Object> noticeAllList(
			@RequestParam(value = "ecname", required = false, defaultValue = "all") String ecname,
			@RequestParam(value = "cname", required = false, defaultValue = "all") String cname,
			@RequestParam(value = "ncategory", required = false, defaultValue = "all") String ncategory,
			@RequestParam(defaultValue = "1") int pageNo) {

		log.info("ecname: " + ecname);
		log.info("cname: " + cname);
		log.info("ncategory: " + ncategory);

		return communityService.listAllNotice(ecname, cname, ncategory, pageNo);
	}

	// 공지사항 단건조회
	@GetMapping("/notice/detail/{nno}")
	public NoticeResponseDTO noticeDetail(@PathVariable int nno) {

		return communityService.detailNotice(nno);
	}

	
	// 공지사항 수정
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@PutMapping("/notice/update")
	public void noticeUpdate(CreateCommunityRequestDTO request) {
		
		communityService.updateNotice(request);
	}
	
	// 공지사항 삭제
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@DeleteMapping("/notice/delete/{nno}")
	public void noticeDelete(@PathVariable int nno) {
		communityService.deleteNotice(nno);
	}
	
	// 공지사항 첨부파일 다운로드
	@GetMapping("/notice/download/attach/{nno}")
	public void noticeAttachDownload(@PathVariable int nno, HttpServletResponse response) {
		Notice notice = communityService.nattachDownload(nno);

		// 파일 이름이 한글일 경우, 브라우저에서 한글 이름으로 다운로드 받기 위해 헤더에 추가할 내용
		try {
			// 한글 파일의 이름 -> 인코딩 변경
			String fileName = new String(notice.getNattachoname().getBytes("UTF-8"), "ISO-8859-1");

			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

			// 파일 타입을 헤더에 추가
			response.setContentType(notice.getNattachtype());

			// 응답 바디에 파일 데이터를 출력
			OutputStream os = response.getOutputStream();
			os.write(notice.getNattach()); // byte 배열 타입을 받아서 저장
			os.flush();
			os.close();

		} catch (IOException e) {
			log.error(e.getMessage()); // error 출력
		}
	}
}
