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
public class EduCenterResponseDTO {
	private int ecno;
	private String ecname;
	private String ecpostcode;
	private String ecaddress;
	private String ecdetailaddress;
	
	private String eccreatedat;
	private String ecupdatedat;
	
	private List<Integer> eanoList;

}
