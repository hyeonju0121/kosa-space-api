package com.mycompany.kosa_space.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAttendanceTimeInfoResponseDTO {
	private String mid;
	private String acheckin;
	private String acheckout;
	private boolean acheckinstatus;
	private boolean acheckoutstatus;
	
	private boolean referencestatus;
}
