package com.mycompany.kosa_space.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingRoom {
	private int trno;
	private int ecno;
	private String trname;
	private int trcapacity;
	private boolean trenable;
	
	private Date trcreatedat;
	private Date trupdatedat;
	
}
