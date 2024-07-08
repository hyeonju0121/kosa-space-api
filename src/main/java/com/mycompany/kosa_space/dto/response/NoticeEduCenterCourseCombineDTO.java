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
public class NoticeEduCenterCourseCombineDTO {
	
	private int nno;
	private int ecno;
	private String ecname;
	private int cno;
	private String cname;
	private String ncategory;
	private String ntitleString;
	private String ncontent;
	private int nhitcount;
	
	private byte[] nattach;
	private String nattachoname;
	private String nattachtype;
	
	private Date ncreatedat;
	private Date nupdatedat;
	
}
