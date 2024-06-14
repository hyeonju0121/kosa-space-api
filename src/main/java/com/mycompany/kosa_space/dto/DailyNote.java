package com.mycompany.kosa_space.dto;

import java.util.Date;

import lombok.Data;

@Data
public class DailyNote {
	private int dno;
	private String mid;
	private String dtitle;
	private String dnotionurl;
	private String dsize;
	private String dhashtag;
	private String status;
	private Date dcreatedat;
	private Date dupdatedat;
	
}
