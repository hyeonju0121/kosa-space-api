package com.mycompany.kosa_space.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CreateTrainingRoomRequestDTO {
	private String ecname; // 교육장 명
	private String trname;
	private int trcapacity;
	private boolean trenable;
	
	private MultipartFile trattachdata;
	private byte[] trattach;
	private String trattachoname;
	private String trattachtype;
}
