package com.mycompany.kosa_space.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashBoardAttendanceDTO {
	
	private String cname;
	
	private String trname; // 강의실 이름

	private int totalCheckinCnt;
	private int totalCheckoutCnt;
	private int totalAbsenceCnt;
}
