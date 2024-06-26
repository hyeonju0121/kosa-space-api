package com.mycompany.kosa_space.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class TrainingRoom {
	private int trno;
	private int ecno;
	private String trname;
	private int trcapacity;
	private boolean trenable;
	
	private MultipartFile trattach;
	private String trattachoname;
	private String trattachtype;
	
	private Date trcreatedat;
	private Date trupdatedat;
	
}
