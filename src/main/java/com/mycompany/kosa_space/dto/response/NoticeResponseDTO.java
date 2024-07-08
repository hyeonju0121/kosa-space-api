package com.mycompany.kosa_space.dto.response;

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
public class NoticeResponseDTO {
	private int nno;
	private int ecno;
	private int cno;
	private String mid;
	private String ncategory;
	private String ntitle;
	private String ncontent;
	private int nhitcount;
	
	private MultipartFile nattachdata;
	private String nattachoname;
	private String nattachtype;
	private byte[] nattach;
	
	private String ncreatedat;
	private String nupdatedat;
}
