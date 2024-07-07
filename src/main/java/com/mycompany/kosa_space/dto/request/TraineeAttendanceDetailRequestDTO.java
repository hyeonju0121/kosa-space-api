package com.mycompany.kosa_space.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraineeAttendanceDetailRequestDTO {
	private String mid;
	private String startdate;
	private String enddate;

}
