package com.mycompany.kosa_space.controller;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

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

		dailyNoteService.createDailyNote(request, authentication);
	}
	
	@GetMapping("/detail")
	public List<DailyNoteDetailResponseDTO> noteDetail(@RequestParam String mid,
			@RequestParam String refweek) {
		
		return dailyNoteService.detailDailyNote(mid, refweek);
	}
	
	@GetMapping("/info")
	public DailyNoteRequestDTO noteInfo(@RequestParam int refno) {
		return dailyNoteService.infoDailyNote(refno);
	}
	
	@PutMapping("/update")
	public void noteUpdate(@RequestParam int refno, 
			DailyNoteRequestDTO request) throws ParseException {
	
		dailyNoteService.updateDailyNote(refno, request);
	}
	
	@DeleteMapping("/delete/{refno}")
	public void noteDelete(@PathVariable int refno) {
		dailyNoteService.deleteDailyNote(refno);
	}
	
	@GetMapping("/trainee/note/list")
	public Map<String, Object> traineeNoteList(@RequestParam String mid, 
			@RequestParam String adate, 
			@RequestParam(defaultValue = "1") int pageNo) throws ParseException {
		
		return dailyNoteService.traineeNoteList(mid, adate, pageNo);
	}
}
