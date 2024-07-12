package com.mycompany.kosa_space.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyNoteTraineeListResponseDTO {

	private String mid;
	private String mname;
	private String submitstatus;
	private String refweek;

}
