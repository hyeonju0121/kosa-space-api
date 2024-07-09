package com.mycompany.kosa_space.controller;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.kosa_space.dto.request.DailyNoteRequestDTO;
import com.mycompany.kosa_space.dto.response.DailyNoteDetailResponseDTO;
import com.mycompany.kosa_space.service.DailyNoteService;

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
			Authentication authentication) throws ParseException {

		dailyNoteService.createNotice(request, authentication);
	}
	
	@GetMapping("/detail")
	public List<DailyNoteDetailResponseDTO> noteDetail(@RequestParam String mid,
			@RequestParam String refweek) {
		
		return dailyNoteService.detailNotice(mid, refweek);
	}
}
