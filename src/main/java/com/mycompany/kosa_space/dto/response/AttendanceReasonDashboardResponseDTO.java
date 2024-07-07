package com.mycompany.kosa_space.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceReasonDashboardResponseDTO {
	private int todayReasonCnt;
	private int approveCnt;
	private int notApprovedCnt;

}
