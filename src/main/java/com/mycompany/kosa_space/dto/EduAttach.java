package com.mycompany.kosa_space.dto;

import java.util.Date;

import lombok.Data;

@Data
public class EduAttach {
	private int eano;
	private int ecno;
	private int trno;
	private int cno;
	
	private byte[] eaattach;
	private String eaattachoname;
	private String eaattachtype;
	
	private Date eacreatedat;
	private Date eaupdatedat;
	
}
