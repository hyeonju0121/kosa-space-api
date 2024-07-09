package com.mycompany.kosa_space.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.kosa_space.dto.request.DailyNoteRequestDTO;
import com.mycompany.kosa_space.service.DailyNoteService;
import com.mycompany.kosa_space.service.EduService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/dailynote")
public class DailyNoteController {
	@Autowired
	private DailyNoteService dailyNoteService;
	
	@PreAuthorize("hasAuthority('ROLE_USER')")
	@PostMapping("/create")
	public void noteCreate(DailyNoteRequestDTO request, 
			Authentication authentication) {
		
		dailyNoteService.createNotice(request, authentication);
	}
	
}
