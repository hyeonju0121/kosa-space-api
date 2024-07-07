package com.mycompany.kosa_space.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceNotesRequestDTO {
	private String mid;
	private String adate;
	private String ancategory;
	private String anreason;
	
	private MultipartFile anattachdata;
}
