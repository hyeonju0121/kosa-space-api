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
public class DashBoardResponseDTO {
	
	private int ecno;
	private String ecname;
	
	private int cno;
	private String cname;
	private String cstatus;
	
	private Date cstartdate;
	private Date cenddate;
	
	private int crequireddate;

}
