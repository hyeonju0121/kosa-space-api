package com.mycompany.kosa_space.dto.response;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingRoomListResponseDTO {
	
	private int trno;
	private String trname;
	
	private int ecno;
	private String ecname;
	
	private String cname;
	private String cstartdate;
	private String cenddate;
	
	private int trcapacity;
	private boolean trenable;
	private String trenableResult; // 사용중, 사용가능
	
	private String trcreatedat;
	private String trupdatedat;
	
	private List<Integer> eanoList;
	

}
