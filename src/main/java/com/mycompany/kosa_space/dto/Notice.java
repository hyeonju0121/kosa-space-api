package com.mycompany.kosa_space.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class Notice {
	private int nno;
	private int ecno;
	private int cno;
	private String mid;
	private String ncategory;
	private String ntitle;
	private String ncontent;
	private int nhitcount;
	
	private MultipartFile nattach;
	private String nattachoname;
	private String nattachtype;
	private byte[] nattachdata;
	
	private Date ncreatedat;
	private Date nupdatedat;
	
}
