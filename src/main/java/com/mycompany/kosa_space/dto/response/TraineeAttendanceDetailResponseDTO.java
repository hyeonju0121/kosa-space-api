package com.mycompany.kosa_space.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraineeAttendanceDetailResponseDTO {

	private String adate;
	private String acheckin;
	private String acheckout;
	private String astatus;
	
	private boolean aconfirm;
	private boolean anconfirm;
}
