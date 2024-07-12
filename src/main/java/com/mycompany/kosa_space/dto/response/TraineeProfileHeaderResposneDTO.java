package com.mycompany.kosa_space.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraineeProfileHeaderResposneDTO {

	private String mid;
	private String mname;
	private String ecname;
	private String cname;
	private String cstartdate;
	private String cenddate;
}
