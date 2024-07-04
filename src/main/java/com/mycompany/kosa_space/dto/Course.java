package com.mycompany.kosa_space.dto;

import java.time.LocalDate;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
	private int cno;
	private int trno;
	private String cname;
	private String ccode;
	private int ctotalnum;
	private Date cstartdate;
	private Date cenddate;
	private int crequireddate;
	private String cstatus;
	private String cprofessor;
	private String cmanager;
	
	private String ctrainingdate;
	private String ctrainingtime;
	
	private Date ccreatedat;
	private Date cupdatedat;
	
}
