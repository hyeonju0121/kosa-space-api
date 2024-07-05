package com.mycompany.kosa_space.dto.request;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CreateTraineeRequestDto {
	private String ecname;
	private String cname;
	private String mname;
	private boolean tsex;
	private int tage;
	private String tpostcode;
	private String taddress;
	private String taddressdetail;
	private boolean tfield;	// 전공, 비전공
	private String tacademic;
	private String tschoolname;
	private String tmajor;
	private String tminor;
	private String tgrade;
	private String tstatus;	// 진행예정, 진행중, 수료 등등의 상태 // -> 교육과정의 상태값 가져오기
	private MultipartFile tprofiledata;
	
	private String memail;
	private String mphone;
	private Date mcreatedat;
	private Date mupdatedat;
	
}
