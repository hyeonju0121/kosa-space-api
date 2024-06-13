package com.mycompany.kosa_space.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ReferenceData {
	private int refno;
	private int cno;
	private String mid;
	private boolean refcategory;
	
	private MultipartFile refattach;
	private String refatthoname;
	private String refattachtype;
	private byte[] refattachdata;
	private String refattachsize;
	
	private Date refcreatedat;
	private Date refupdatedat;	
	
}
