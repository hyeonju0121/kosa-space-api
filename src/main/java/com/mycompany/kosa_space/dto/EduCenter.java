package com.mycompany.kosa_space.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class EduCenter {
	private int ecno;
	private String ecname;
	private String ecpostcode;
	private String ecaddress;
	
	private MultipartFile ecattach;
	private String ecattachoname;
	private String ecattachtype;
	
	private Date eccreatedat;
	private Date ecupdatedat;
	
}
