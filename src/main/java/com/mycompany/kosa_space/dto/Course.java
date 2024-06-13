package com.mycompany.kosa_space.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class Course {
	private int cno;
	private int trno;
	private String cname;
	private String ccode;
	private int ctotalnum;
	private Date cstartdate;
	private Date cenddate;
	private int crequireddate;
	private boolean cstatus;
	private String cprofessor;
	private String cmanager;
	
	private MultipartFile cattach;
	private String cattachoname;
	private String cattachtype;
	private byte[] cattachdata;
	
	private Date ccreatedat;
	private Date cupdatedat;
	
}
