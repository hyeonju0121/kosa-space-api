package com.mycompany.kosa_space.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CreateTrainingRoomRequestDTO {
	private String ecname; // 교육장 명
	private String trname;
	private int trcapacity;
	private boolean trenable;
	
	private List<MultipartFile> trattachdata;
	
}
