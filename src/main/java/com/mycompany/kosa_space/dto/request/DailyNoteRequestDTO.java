package com.mycompany.kosa_space.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyNoteRequestDTO {
	
	private String reftitle;
	private String refurl;
	private String refdate;
}
