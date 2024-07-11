package com.mycompany.kosa_space.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashBoardNoticeDTO {
	
	private int nno;
	private String ncategory;
	private String ntitle;
	private String ncreatedat;
	
	private String ecname;
	private String cname;
}
