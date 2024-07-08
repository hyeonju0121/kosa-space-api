package com.mycompany.kosa_space.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
	
}
