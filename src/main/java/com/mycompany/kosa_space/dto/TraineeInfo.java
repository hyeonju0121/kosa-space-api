package com.mycompany.kosa_space.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class TraineeInfo {
	private String mid;
	private int cno;
	private char tsex;
	private int tage;
	private String taddress;
	private boolean tfield;
	private String tacademic;
	private String tschoolname;
	private String tmajor;
	private String tminor;
	private String tgrade;
	private String tstatus;
	
	private MultipartFile tprofileimg;
	private String tprofileoname;
	private String tprofiletype;
	
}
