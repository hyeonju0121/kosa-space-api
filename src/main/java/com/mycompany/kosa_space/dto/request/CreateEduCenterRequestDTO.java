package com.mycompany.kosa_space.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CreateEduCenterRequestDTO {
	private String ecname;
	private String ecpostcode;
	private String ecaddress;
	private String ecdetailaddress; // 상세주소

	private List<MultipartFile> ecattachdata;
	
}
