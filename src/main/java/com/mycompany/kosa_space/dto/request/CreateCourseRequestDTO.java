package com.mycompany.kosa_space.dto.request;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseRequestDTO {
	private String ecname;
	private String cname;
	private String ccode;
	private int trno;
	private String trname;
	private int crequireddate;
	private int ctotalnum;
	private String cstatus;
	private String cprofessor;
	private String cmanager;
	
	//private String cstartdate;
	//private String cenddate;
	private Date cstartdate;
	private Date cenddate;
	private String ctrainingdate;
	private String ctrainingtime;

	private List<MultipartFile> cattachdata;

}
