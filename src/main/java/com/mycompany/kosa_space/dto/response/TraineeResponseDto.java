package com.mycompany.kosa_space.dto.response;

import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraineeResponseDto {
	private int rnum;
	
	private String mid;		// 교육생 단건 조회
	private String ecname;
	private String cname;	// 교육생 단건 조회
	private String mname;	// 교육생 단건 조회
	private boolean tsex;	// 교육생 단건 조회
	private int tage;		// 교육생 단건 조회
	private String tpostcode;	// 교육생 단건 조회
	private String taddress;	// 교육생 단건 조회
	private String taddressdetail;	// 교육생 단건 조회
	private boolean tfield;		// 교육생 단건 조회		// 전공, 비전공
	private String tacademic;	// 교육생 단건 조회
	private String tschoolname;	// 교육생 단건 조회
	private String tmajor;		// 교육생 단건 조회
	private String tminor;		// 교육생 단건 조회
	private String tgrade;		// 교육생 단건 조회
	private String tstatus;		// 교육생 단건 조회		// 진행예정, 진행중, 수료 등등의 상태 // -> 교육과정의 상태값 가져오기
	private MultipartFile tprofiledata;	// 교육생 단건 조회
	private byte[] tprofileimg;	// 교육생 단건 조회
	
	private String memail;		// 교육생 단건 조회
	private String mphone;		// 교육생 단건 조회
	private String cstatus;		// 교육생 단건 조회
	
	private Date mcreatedat;
	private Date mupdatedat;
	
	private int cno;
	private String ccode;
	private int trno;
	private int ctotalnum;
	private Date cstartdate;
	private Date cenddate;
	private int crequireddate;
	private String cprofessor;
	private String cmanager;
	private String ctrainingdate;
	private String ctrainingtime;
	private boolean menable;
	
	
}
