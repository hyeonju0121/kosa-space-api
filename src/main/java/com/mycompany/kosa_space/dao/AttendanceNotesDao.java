package com.mycompany.kosa_space.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.AttendanceNotes;
import com.mycompany.kosa_space.dto.response.AttendanceInfoResponseDTO;
import com.mycompany.kosa_space.dto.response.CombineEduTraineeAttendanceDTO;

@Mapper
public interface AttendanceNotesDao {
	
	// 사유 등록
	public int insert(AttendanceNotes reason);
	
	// mid 와 adate 에 해당하는 사유 단건 조회
	public AttendanceNotes selectByMidAndAdate(String mid, Date adate);

	// 사유 수정
	public void update(AttendanceNotes reason);
	
	// (운영진) 교육생 사유 승인 기능
	public void approve(String mid, Date adate);
	
	// mid, adate 기준으로 해당 날짜의 교육생이 사유를 작성한 게 있는지 조회
	public AttendanceInfoResponseDTO selectReasonByMid(String mid, Date adate);

	// ecname, cname 기준으로 해당하는 출결 사유 작성 정보 조회
	public List<CombineEduTraineeAttendanceDTO> selectTotalReasonByEcnameAndCname(
					String ecname, String cname, Date adate);
	
	// ecname, cname 기준으로 해당하는 출결 사유 작성 정보 중에 승인된 건수 조회
	public int selectReasonApproveCnt(String ecname, String cname, Date adate);
	
	// ecname, cname 기준으로 해당하는 출결 사유 작성 정보 중에 미승인된 건수 조회
    public int selectReasonNotApprovedCnt(String ecname, String cname, Date adate);
	
	
}
