package com.mycompany.kosa_space.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class AttendanceNotes {
	private String mid;
	private Date adate;
	private String acnategory;
	private String anreason;
	
	private MultipartFile anattachdata;
	private String anattachoname; // 파일 원래 이름
	private String anattachtype; // 파일 종류
	private byte[] anattach;
	
	private Date ancreatedat;
	private Date anupdatedat;
	
}
