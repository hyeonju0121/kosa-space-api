package com.mycompany.kosa_space.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraineeAttendanceListResponseDTO {
	private int rnum;
	private String mname;
	private String mid;
	
	private String acheckin;
	private String acheckout; 
	
	private int approvecnt; // 정상출결일수
	private int latenesscnt; // 지각일수
	private int absencecnt; // 결석일수
	
	private String percentage;
	private int crequireddate; // 교육과정 총 훈련일수
	
}
