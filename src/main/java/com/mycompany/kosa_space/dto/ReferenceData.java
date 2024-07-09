package com.mycompany.kosa_space.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceData {
	private int refno;
	private int cno;
	private String mid;
	
	private String reftitle;
	private String refurl;
	private String refweek;
	
	private Date refdate;

	private Date refcreatedat;
	private Date refupdatedat;	
	
}
