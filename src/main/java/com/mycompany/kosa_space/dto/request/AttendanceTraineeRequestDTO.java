package com.mycompany.kosa_space.dto.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceTraineeRequestDTO {
	private String mid;
	//@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String attendancetime;
}
