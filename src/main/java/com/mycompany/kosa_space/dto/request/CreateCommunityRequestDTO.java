package com.mycompany.kosa_space.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommunityRequestDTO {
	private String ncategory;
	private String ecname;
	private String cname;
	
	private String ntitle;
	private String ncontent;
	
	private MultipartFile nattachdata;
	
}
