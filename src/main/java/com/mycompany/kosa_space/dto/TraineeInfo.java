package com.mycompany.kosa_space.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraineeInfo {
	private String mid;
	private int cno;
	private boolean tsex;
	private int tage;
	private String tpostcode;
	private String taddress;
	private boolean tfield;  // 전공 여부 true, or false
	private String tacademic;
	private String tschoolname;
	private String tmajor;
	private String tminor;
	private String tgrade;
	private String tstatus;
	
	private MultipartFile tprofiledata;
	private String tprofileoname;
	private String tprofiletype;
	private byte[] tprofileimg;
	
}

