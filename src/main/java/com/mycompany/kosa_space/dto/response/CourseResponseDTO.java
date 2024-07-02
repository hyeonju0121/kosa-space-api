package com.mycompany.kosa_space.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDTO {
	private int ecno;
	private int trno;
	private int cno;
	private String trname;
	private String cname;
	private String ccode;
	private int ctotalnum;
	private Date cstartdate;
	private Date cenddate;
	private int crequireddate;
	private String cstatus;
	private String cprofessor;
	private String cmanager;
	private Date ccreatedat;
	private Date cupdatedat;
	private String ctrainingdate;
	private String ctrainingtime;
	private int trcapacity;
	private char trenable;

}
