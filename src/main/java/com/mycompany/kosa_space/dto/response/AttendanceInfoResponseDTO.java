package com.mycompany.kosa_space.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceInfoResponseDTO {
	private String mid;
	private int cno;
	private Date adate;
	private Date acheckin;
	private Date acheckout;
	private String astatus;
	private boolean aconfirm; // 출결 승인 여부
	private boolean anconfirm; // 출결 사유 승인 여부
	private String ancategory;
	private String anreason;
	private byte[] anattach;
	private String anattachoname;
	private String anattachtype;
	
	private int approvecnt;
	private int latenesscnt;
	private int absencecnt;
	
	private boolean acheckinstatus;
	private boolean acheckoutstatus;
	
	private Date ancreatedat;
	private Date anupdatedat;
	
}
