package com.mycompany.kosa_space.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDashboardResponseDTO {
	private int inprogresscnt;
	private int scheduledcnt;
	private int completecnt;
}
