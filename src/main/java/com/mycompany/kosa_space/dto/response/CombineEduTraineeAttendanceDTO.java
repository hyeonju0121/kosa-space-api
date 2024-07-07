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
public class CombineEduTraineeAttendanceDTO {
	
	private String mid;
	private Date adate;
	
	private String ecname;
	private String cname;
	private int ecno;
	private int cno;
	
	// AttendanceNotes Table
	private String ancategory;
	private String anreason;
	private boolean anconfirm;
	private byte[] anattach;
	private String anattachoname;
	private String anattachtype;
	private Date ancreatedat;
	private Date anupdatedat;
	
	// Attendance Table
	private Date acheckin;
	private Date acheckout;
	private String astatus;
	private boolean aconfirm;
	private boolean acheckinstatus;
	private boolean acheckoutstatus;
	
	private int approvecnt; // 정상 출결 일수
	private int latenesscnt; // 총 지각 일수
	private int absencecnt; // 총 결석 일수
	
	// Course Table
	private String ccode;
	private int ctotalnum;
	private Date cstartdate;
	private Date cenddate;
	private int crequireddate;
	private String cstatus;
	private String cprofessor;
	private String cmanager;
	
	private String ctrainingdate;
	private String ctrainingtime;
	
	private Date ccreatedat;
	private Date cupdatedat;

	// Training Table
	private int trno;
}
