package com.mycompany.kosa_space.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
	private String mid;
	private Date adate;
	private Date acheckin;
	private Date acheckout;
	private String astatus;
	private boolean aconfirm;
	private int cno;

	private boolean acheckinstatus;
	private boolean acheckoutstatus;
	
	private int approvecnt; // 정상 출결 일수
	private int latenesscnt; // 총 지각 일수
	private int absencecnt; // 총 결석 일수
}
