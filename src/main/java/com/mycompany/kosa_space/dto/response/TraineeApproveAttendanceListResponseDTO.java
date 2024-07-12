package com.mycompany.kosa_space.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraineeApproveAttendanceListResponseDTO {
	private int rnum;
	private String mname;
	private String mid;
	
	private String acheckin;
	private String acheckout; 
	
	private String astatus; // 출결 유형
	
	private boolean aconfirm; // 출결 승인 여부
	private boolean anconfirm; // 출결 사유 승인 여부

}
