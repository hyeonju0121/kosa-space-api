package com.mycompany.kosa_space.dto;

import java.util.Date;

import lombok.Data;

@Data
public class Member {
	private String mid;
	private String mpassword;
	private String mname;
	private String mphone;
	private String memail;
	private String mrole;
	private boolean menable;
	private Date mcreatedat;
	private Date mupdatedat;
	
}
