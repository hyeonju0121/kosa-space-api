package com.mycompany.kosa_space.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EduCenter {
	private int ecno;
	private String ecname;
	private String ecpostcode;
	private String ecaddress;
	
	private Date eccreatedat;
	private Date ecupdatedat;
	
}
