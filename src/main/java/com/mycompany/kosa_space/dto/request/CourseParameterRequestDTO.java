package com.mycompany.kosa_space.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseParameterRequestDTO {
	private String ecname;
	private String cstatus;
	private String cprofessor;
}
