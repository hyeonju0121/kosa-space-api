package com.mycompany.kosa_space.dto.response;

import java.util.List;

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
	private String ecname;
	private int trno;
	private int cno;

	private String trname;
	private String cname;
	private String ccode;
	private int ctotalnum;
	private String cstartdate;
	private String cenddate;
	private int crequireddate;
	private String cstatus;
	private String cprofessor;
	private String cmanager;
	private String ccreatedat;
	private String cupdatedat;
	private String ctrainingdate;
	private String ctrainingtime;
	private int trcapacity;
	private boolean trenable;
	
	private List<Integer> eanoList;

}
