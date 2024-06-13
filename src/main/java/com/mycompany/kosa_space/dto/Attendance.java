package com.mycompany.kosa_space.dto;

import java.util.Date;

import lombok.Data;

@Data
public class Attendance {
	private String mid;
	private Date adate;
	private Date acheckin;
	private Date acheckout;
	private String astatus;
	private boolean aconfirm;
	private int cno;
	
}
