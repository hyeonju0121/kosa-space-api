package com.mycompany.kosa_space.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.kosa_space.dto.request.CreateCommunityRequestDTO;
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
	public Map<String, Object> noticeList(@RequestParam String ecname,
			@RequestParam(defaultValue = "1") int pageNo) {
		return communityService.listNotice(ecname, pageNo);
	}
	
	// ecname, cname, ncategory, pageNo 에 따른 공지사항 목록 조회
	@PostMapping("/notice/list")
	public Map<String, Object> noticeAllList(
			@RequestParam(value = "ecname", required = false, defaultValue = "all") String ecname,
			@RequestParam(value = "cname", required = false, defaultValue = "all") String cname,
			@RequestParam(value = "ncategory", required = false, defaultValue = "all") String ncategory,
			@RequestParam int pageNo) {

		log.info("ecname: " + ecname);
		log.info("cname: " + cname);
		log.info("ncategory: " + ncategory);
		
		
		return communityService.listAllNotice(ecname, cname, ncategory, pageNo);
	}
}
