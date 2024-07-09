package com.mycompany.kosa_space.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyNoteDetailResponseDTO {
	private int refno;
	private String reftitle;
	private String refurl;
	private String refweek;
	private String refdate;
	private String refcreatedat;
	private String refupdatedat;
}
