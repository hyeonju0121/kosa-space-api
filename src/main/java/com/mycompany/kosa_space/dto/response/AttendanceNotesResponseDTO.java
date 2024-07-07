package com.mycompany.kosa_space.dto.response;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import com.mycompany.kosa_space.dto.AttendanceNotes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceNotesResponseDTO {
	private String mid;
	private String adate;
	private String ancategory;
	private String anreason;
	
	private boolean anconfirm;
	
	private MultipartFile anattachdata;
	private String anattachoname; // 파일 원래 이름
	private String anattachtype; // 파일 종류
	private byte[] anattach;
	
	private String ancreatedat;
	private String anupdatedat;
}
