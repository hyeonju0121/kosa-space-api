package com.mycompany.kosa_space.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mycompany.kosa_space.dao.AttendanceDao;
import com.mycompany.kosa_space.dao.AttendanceNotesDao;
import com.mycompany.kosa_space.dao.CourseDao;
import com.mycompany.kosa_space.dao.ReferenceDataDao;
import com.mycompany.kosa_space.dao.TraineeInfoDao;
import com.mycompany.kosa_space.dto.Attendance;
import com.mycompany.kosa_space.dto.AttendanceNotes;
import com.mycompany.kosa_space.dto.Course;
import com.mycompany.kosa_space.dto.ReferenceData;
import com.mycompany.kosa_space.dto.TraineeInfo;
import com.mycompany.kosa_space.dto.request.AttendanceNotesRequestDTO;
import com.mycompany.kosa_space.dto.request.AttendanceTraineeRequestDTO;
import com.mycompany.kosa_space.dto.request.TraineeAttendanceDetailRequestDTO;
import com.mycompany.kosa_space.dto.response.AttendanceInfoResponseDTO;
import com.mycompany.kosa_space.dto.response.AttendanceNotesResponseDTO;
import com.mycompany.kosa_space.dto.response.AttendanceReasonDashboardResponseDTO;
import com.mycompany.kosa_space.dto.response.CombineEduTraineeAttendanceDTO;
import com.mycompany.kosa_space.dto.response.TraineeApproveAttendanceListResponseDTO;
import com.mycompany.kosa_space.dto.response.TraineeAttendanceDetailResponseDTO;
import com.mycompany.kosa_space.dto.response.TraineeAttendanceListResponseDTO;
import com.mycompany.kosa_space.dto.response.TraineeResponseDto;
import com.mycompany.kosa_space.dto.response.UserAttendanceTimeInfoResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AttendanceService {
	
	@Autowired
	private AttendanceDao attendanceDao;
	
	@Autowired
	private AttendanceNotesDao attendanceNotesDao;
	
	@Autowired
	private TraineeInfoDao traineeInfoDao;
	
	@Autowired
	private CourseDao courseDao;
	
	@Autowired
	private ReferenceDataDao referenceDataDao;
	
	
	private static final String CENTERIP = "125.131.208.230";
	
	
	// (운영진) 교육생 출결 활성화 기능 
	@Transactional
	public void active(String adate) throws Exception{
		// adate 세팅 String to Date
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String adateStr = adate.substring(0, 10);
			        
	    Date date = format.parse(adateStr);
		
		// 이미 활성화 되어있는 경우는 에러 처리
	    // (attendance 테이블 내에 adate 기준으로 count 값 조사)
	    int cnt =  attendanceDao.selectCntByAdate(date);

		if (cnt > 0) {
			throw new RuntimeException(
					"이미 오늘 날짜 기준으로 모든 교육생의 출결이 활성화 되어 있습니다.");
		}
		
		// 모든 교육과정의 수강생들의 cno, mid 조사 
		List<TraineeResponseDto> traineeList = traineeInfoDao.allTraineeList();
		
		for (TraineeResponseDto trainee : traineeList) {
			//log.info("trainee: " + trainee.toString());
			String mid = trainee.getMid();
			int cno = trainee.getCno();
			
			// 교육생 총 정상출결일, 총 지각일, 총 결석일에 대한 정보 가져오기
			int approveCnt = 0;
			int latenessCnt = 0;
			int absenceCnt = 0;
			
			Attendance attendanceInfo = attendanceDao.selectByMid(mid);

			if (attendanceInfo != null) {
				approveCnt = attendanceInfo.getApprovecnt();
				latenessCnt = attendanceInfo.getLatenesscnt();
				absenceCnt = attendanceInfo.getAbsencecnt();
			} 
			
			Attendance attendance = Attendance.builder()
					.mid(mid)
					.adate(date)
					.aconfirm(false)
					.cno(cno)
					.acheckinstatus(false)
					.acheckoutstatus(false)
					.approvecnt(approveCnt)
					.latenesscnt(latenessCnt)
					.absencecnt(absenceCnt)
					.build();
			//log.info("attendance: " + attendance.toString());

			// DB insert 
			attendanceDao.active(attendance);
		}
	}
	
	
	// (교육생) 입실 기능
	@Transactional
	public void checkin(String clientIP, 
			AttendanceTraineeRequestDTO attendance) throws Exception {
		// 교육장 IP 와 클라이언트 IP가 일치하지 않을 경우 에러처리 
		validationIPMatch(clientIP);
		
		clientIP = clientIP.substring(3);
		
		// 교육생 총 정상출결일, 총 지각일, 총 결석일에 대한 정보 가져오기
		int approveCnt = 0;
		int latenessCnt = 0;
		int absenceCnt = 0;
		
		Attendance attendanceInfo = attendanceDao.selectByMid(attendance.getMid());
		//log.info("attendanceInfo: " + attendanceInfo.toString());
		
		if (attendanceInfo != null) {
			approveCnt = attendanceInfo.getApprovecnt();
			latenessCnt = attendanceInfo.getLatenesscnt();
			absenceCnt = attendanceInfo.getAbsencecnt();
		} 
		
		// 시간에 대한 처리 (09: 10 분 이후이면 사용자의 astatus 지각 처리로 업데이트)
		// 09: 10 분 이전이면, 사용자의 astatus 를 정상 출결로 업데이트
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");

		log.info("request: " + attendance.toString());
		
        String absenceTimeStr = "09:10";
        //String requestTimeStr = "15:12"; // 임의로 시간 지정해서 테스트함
        String requestTimeStr = attendance.getAttendancetime().substring(11, 16);
        
        log.info("absenceTimeStr: " + absenceTimeStr);
        log.info("requestTimeStr: " + requestTimeStr);
        
        Date absenceTime = format.parse(absenceTimeStr);
        Date requestTime = format.parse(requestTimeStr);

        // 지각 시간 기준과 입실 시간 차이 구하기 
        long difference = requestTime.getTime() - absenceTime.getTime();

        int hours = (int) (difference / (1000 * 60 * 60));
        int minutes = (int) ((difference / (1000 * 60)) % 60);
        
        if (minutes > 0 && hours >= 0) { // 지각 처리
        	log.info("지각입니다.");
        	latenessCnt++; // 총 지각일수 +1 
        	
        } else { // 정상 출결 처리 
        	log.info("정상 출결입니다.");
        }
        
        // adate 생성
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        String adateStr = attendance.getAttendancetime().substring(0, 10);
        
        Date adate = format2.parse(adateStr);
        
        // checkin 시간 string to date
        SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date acheckin = format3.parse(attendance.getAttendancetime());

        // Attendance 객체 생성
        Attendance data = Attendance.builder()
        		.mid(attendance.getMid())
        		.adate(adate)
        		.acheckinstatus(true)
        		.acheckin(acheckin)
        		.approvecnt(approveCnt)
        		.latenesscnt(latenessCnt)
        		.absencecnt(absenceCnt)
        		.build();
		
        // DB 업데이트
        log.info(data.toString());
        
        attendanceDao.checkin(data);
	}
	
	// (교육생) 퇴실 기능
	@Transactional
	public void checkout(String clientIP, 
			AttendanceTraineeRequestDTO attendance) throws Exception{
		// 교육장 IP 와 클라이언트 IP가 일치하지 않을 경우 에러처리 
		validationIPMatch(clientIP);
				
		clientIP = clientIP.substring(3);

		// 시간에 대한 처리 (17: 50 분 이후에만 정상 출결 기준이 허용됨 -> 17: 50분 이후인지만 체크하기)
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");

		String absenceTimeStr = "17:50";
		//String requestTimeStr = "17:55"; // 임의로 시간 지정해서 테스트함
		String requestTimeStr = attendance.getAttendancetime().substring(11, 16);
		        
		log.info("absenceTimeStr: " + absenceTimeStr);
		log.info("requestTimeStr: " + requestTimeStr);
		        
		Date absenceTime = format.parse(absenceTimeStr);
		Date requestTime = format.parse(requestTimeStr);

		// 지각 시간 기준과 입실 시간 차이 구하기 
		long difference = requestTime.getTime() - absenceTime.getTime();

		int hours = (int) (difference / (1000 * 60 * 60));
		int minutes = (int) ((difference / (1000 * 60)) % 60);
	
		//log.info("hours: " + hours + ", minites: " + minutes);
		
		if (minutes >= 0 && hours >= 0) { // 정상 출결 처리 (17: 50분 ~)
		      log.info("정상으로 퇴실 완료됐습니다.");
		} else { 
			// 17시 50분 이전인 경우
			log.info("오후 5시 50분 이전에는 퇴실 하실 수 없습니다. ");
			throw new RuntimeException("오후 5시 50분 이전에는 퇴실 하실 수 없습니다. ");
		}
		
		// adate 생성
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        String adateStr = attendance.getAttendancetime().substring(0, 10);
        
        Date adate = format2.parse(adateStr);
        
        // checkin 시간 string to date
        SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date acheckout = format3.parse(attendance.getAttendancetime());
        
        Attendance data = Attendance.builder()
        		.mid(attendance.getMid())
        		.adate(adate)
        		.acheckout(acheckout)
        		.acheckoutstatus(true)
        		.build();
        
		// DB 업데이트
        attendanceDao.checkout(data);
		
	}
	
	// 교육생 사유 작성 기능
	@Transactional
	public void createReason(AttendanceNotesRequestDTO request) throws Exception {
		// adate 생성
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String adateStr = request.getAdate().substring(0, 10);
        
        Date adate = format.parse(adateStr);
		
		AttendanceNotes reason = AttendanceNotes.builder()
				.mid(request.getMid())
				.adate(adate)
				.anconfirm(false)
				.ancategory(request.getAncategory())
				.anreason(request.getAnreason())
				.build();
		
		// 첨부가 넘어왔을 경우
		if (request.getAnattachdata() != null && !request.getAnattachdata().isEmpty()) {
			MultipartFile mf = request.getAnattachdata();
			// 파일 이름 설정
			reason.setAnattachoname(mf.getOriginalFilename());
			// 파일 종류 설정
			reason.setAnattachtype(mf.getContentType());
			try {
				// 파일 데이터 설정
				reason.setAnattach(mf.getBytes());
			} catch(IOException e) {
				
			}
		}

		// DB insert
		attendanceNotesDao.insert(reason);
		
	}
	
	// 교육생 사유 단건 조회 
	@Transactional
	public AttendanceNotesResponseDTO detailReason(String mid, String adate) throws Exception{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");        
        Date date= format.parse(adate);
		
        //  DB에서 mid, adate 에 해당하는 사유 정보 가져오기
        AttendanceNotes notes = attendanceNotesDao.selectByMidAndAdate(mid, date);
        
        // 생성일시, 수정일시 Date to String
     	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

     	Date createdat = notes.getAncreatedat();
     	Date updatedat = new Date();
     	String anupdatedat = "";
     	if (notes.getAnupdatedat() != null) {
     		updatedat = notes.getAnupdatedat();
     		anupdatedat = sdf.format(updatedat);
     	}
     	String ancreatedat = sdf.format(createdat);
        
        AttendanceNotesResponseDTO response = AttendanceNotesResponseDTO.builder()
        		.mid(notes.getMid())
        		.adate(adate)
        		.ancategory(notes.getAncategory())
        		.anreason(notes.getAnreason())
        		// JSON 으로 변환되지 않는 필드는 NULL 처리
             	// byte[]랑 MultipartFile 은 JSON 으로 변환 X -> NULL 처리 필요
        		.anattachdata(null)
        		.anattach(null)
        		.anattachoname(notes.getAnattachoname())
        		.anattachtype(notes.getAnattachtype())
        		.ancreatedat(ancreatedat)
        		.anupdatedat(anupdatedat)
        		.build();
        
		return response;
	}
	
	// 교육생 사유 수정 기능
	@Transactional
	public void updateReason(AttendanceNotesRequestDTO request) throws Exception{
		// adate 생성
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String adateStr = request.getAdate().substring(0, 10);
        
        Date adate = format.parse(adateStr);

     	AttendanceNotes reason = AttendanceNotes.builder()
     			.mid(request.getMid())
     			.adate(adate)
     			.ancategory(request.getAncategory())
     			.anreason(request.getAnreason())
     			.build();
     	
     	 // 첨부가 넘어왔을 경우
     	if (request.getAnattachdata() != null && !request.getAnattachdata().isEmpty()) {
     		MultipartFile mf = request.getAnattachdata();
     		// 파일 이름 설정
     		reason.setAnattachoname(mf.getOriginalFilename());
     		// 파일 종류 설정
     		reason.setAnattachtype(mf.getContentType());
     		try {
     			// 파일 데이터 설정
     			reason.setAnattach(mf.getBytes());
     		} catch(IOException e) {
     		}
     	}
     	
     	// DB update
     	attendanceNotesDao.update(reason);
	}
	
	// (운영진) 교육생이 등록한 사유에 대한 승인 기능
	public void approveReason(String mid, String adate) throws Exception{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");        
        Date date= format.parse(adate);
		
        //  DB update
        attendanceNotesDao.approve(mid, date);
	}
	
	// (운영진) 교육생 출결 승인 기능
	@Transactional
	public void approveAttendance(String mid, String adate) throws Exception{
		log.info("mid: " + mid);	
		log.info("adate: " + adate);
		
		// 사유를 작성한 교육생이 있다면 먼저 사유에 대한 승인 처리가 있어야 함.
		// 사유 미승인시 교육생의 출결을 승인 기능을 수행할 수 없음
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");        
        Date date= format.parse(adate);
		
		// mid 가 adate 에 사유를 작성한게 있는지 조회
		AttendanceInfoResponseDTO attendance = attendanceNotesDao
				.selectReasonByMid(mid, date);
		
		// log.info("attendance: " + attendance.toString());
		
		
		// 교육생의 adate 에 해당하는 출결 정보 가져오기 
		//Attendance userAttendanceInfo = attendanceDao.selectByMid(mid);
		
		Attendance userAttendanceInfo = attendanceDao.selectByMidAndAdate(mid, date);
		
		
		//log.info("userAttendanceInfo: " + userAttendanceInfo.toString());

		// 교육생 총 정상출결일, 총 지각일, 총 결석일에 대한 정보 가져오기
		int approveCnt = userAttendanceInfo.getApprovecnt();
		int absenceCnt = userAttendanceInfo.getAbsencecnt();
			
		if (attendance != null) { // 사유 작성를 작성한 교육생인 경우
			// 사유가 승인 됐는지 여부 조사
			if (!attendance.isAnconfirm()) {
				throw new RuntimeException(
						"조퇴 및 결석 사유에 대한 승인을 먼저 진행해주세요. 출결을 승인할 수 없습니다.");
			} 
			
			if (attendance.getAncategory().equals("지각")) {
				// acheckinstatus 와 acheckoutstatus 비교 
				boolean checkinStatus = userAttendanceInfo.isAcheckinstatus();
				boolean checkoutStatus = userAttendanceInfo.isAcheckoutstatus();
				// 사유가 승인된 경우 (사유 카테고리 : 지각)
				if ((checkinStatus && !checkoutStatus) || 
						(!checkinStatus && !checkoutStatus)) { 
					// 퇴실을 안찍은 경우, 결석인 경우 -> 결석처리
					absenceCnt++;
					
					userAttendanceInfo.setAstatus("결석");
					userAttendanceInfo.setApprovecnt(approveCnt);
					userAttendanceInfo.setAbsencecnt(absenceCnt);
					userAttendanceInfo.setAconfirm(true);
					log.info("userAttendanceInfo: " + userAttendanceInfo.toString());	
				}
			} else {
				// 사유가 승인된 경우 (사유 카테고리 : 결석)
				approveCnt++;
				userAttendanceInfo.setAstatus("정상출결");
				userAttendanceInfo.setAconfirm(true);
				userAttendanceInfo.setApprovecnt(approveCnt); 
			}

		} else {
			// 사유가 작성되지 않은 교육생인 경우
			// acheckinstatus 와 acheckoutstatus 비교 
			boolean checkinStatus = userAttendanceInfo.isAcheckinstatus();
			boolean checkoutStatus = userAttendanceInfo.isAcheckoutstatus();
			if ((checkinStatus && !checkoutStatus) || 
					(!checkinStatus && !checkoutStatus)) { 
				// 퇴실을 안찍은 경우, 사유 작성 없이 결석인 경우 -> 결석처리
				absenceCnt++;
				
				userAttendanceInfo.setAstatus("결석");
				userAttendanceInfo.setApprovecnt(approveCnt);
				userAttendanceInfo.setAbsencecnt(absenceCnt);
				userAttendanceInfo.setAconfirm(true);
				log.info("userAttendanceInfo: " + userAttendanceInfo.toString());	
			} else {
				approveCnt++;
				userAttendanceInfo.setAstatus("정상출결");
				userAttendanceInfo.setApprovecnt(approveCnt);
				userAttendanceInfo.setAconfirm(true);
			}
			//log.info("userAttendanceInfo: " + userAttendanceInfo.toString());
		}
		// DB 업데이트
		//log.info("userAttendanceInfo: " + userAttendanceInfo.toString());
		attendanceDao.approveAttendance(userAttendanceInfo);
	}
	
	
	// ecname, cname 에 해당하는 출결 사유 대시보드 기능
	@Transactional
	public AttendanceReasonDashboardResponseDTO dashboard(String ecname, String cname, String adate) throws Exception{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");        
        Date date= format.parse(adate);
		
		List<CombineEduTraineeAttendanceDTO> data = attendanceNotesDao.selectTotalReasonByEcnameAndCname(ecname, cname, date);
		
		// 오늘 제출된 출결 사유 건수
		int todayReasonCnt = 0;
		// 출결 사유 승인 건수
		int approveCnt = 0;
		// 출결 사유 미승인 건수
		int notApprovedCnt = 0;
		
		if (data.size() == 0) {
			AttendanceReasonDashboardResponseDTO response = AttendanceReasonDashboardResponseDTO.builder()
					.todayReasonCnt(todayReasonCnt)
					.approveCnt(approveCnt)
					.notApprovedCnt(notApprovedCnt)
					.build();
			return response;
		} else {
			for (CombineEduTraineeAttendanceDTO info : data) {
				// 오늘 제출된 출결 사유 건수 구하기
				todayReasonCnt = data.size();
				
				// 출결 사유 승인 건수 
				approveCnt = attendanceNotesDao.selectReasonApproveCnt(ecname, cname, date);
				
				// 출결 사유 미승인 건수
				notApprovedCnt = attendanceNotesDao.selectReasonNotApprovedCnt(ecname, cname, date);
				
				AttendanceReasonDashboardResponseDTO response = AttendanceReasonDashboardResponseDTO.builder()
						.todayReasonCnt(todayReasonCnt)
						.approveCnt(approveCnt)
						.notApprovedCnt(notApprovedCnt)
						.build();
				return response;
			}
		}
		return null;
	}
	
	// 교육생 출결 상세 조회 
	@Transactional
	public List<TraineeAttendanceDetailResponseDTO> detailTraineeAttendance(
			String mid, String startdate, String enddate) {
		
		mid = mid.substring(0, 9);
			
		List<Attendance> data = new ArrayList<>();
		List<TraineeAttendanceDetailResponseDTO> response = new ArrayList<>();
		
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd"); 
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		// startdate 와 enddate 를 선택하지 않은 경우, 교육생의 모든 출결 현황 조회
		if (startdate.equals("all") && enddate.equals("all")) {
			data = attendanceDao.selectTotalAttendanceByMid(mid);
			
			log.info("data: " + data.toString());
			
			for (Attendance info : data) {        
				String adate = format1.format(info.getAdate());
				
				String acheckin = "";
				String acheckout = "";
				if (info.getAcheckin() != null) {
					acheckin = format2.format(info.getAcheckin()).substring(11, 16);
				} 
				if (info.getAcheckout() != null) {
					acheckout = format2.format(info.getAcheckout()).substring(11, 16);
				}
				
				String astatus = "";
				boolean aconfirm = false;
				
				if (!info.isAconfirm()) {
					astatus = "출결 승인 전";
				} else {
					aconfirm = true;
					astatus = info.getAstatus();
				}
				
				boolean anconfirm = false; // 사유작성여부
				// mid 가 adate 에 사유를 작성한게 있는지 조회
				AttendanceInfoResponseDTO reason = attendanceNotesDao
						.selectReasonByMid(mid, info.getAdate());
				
				if (reason != null) { // 사유를 작성한 경우
					anconfirm = true;
				} 
				
				TraineeAttendanceDetailResponseDTO attendance = TraineeAttendanceDetailResponseDTO.builder()
						.adate(adate)
						.acheckin(acheckin)
						.acheckout(acheckout)
						.astatus(astatus)
						.aconfirm(aconfirm)
						.anconfirm(anconfirm)
						.build();
				
				response.add(attendance);
			} 
		} else {
			// startdate 와 enddate 를 선택한 경우, 기간에 맞는 교육생의 출결 현황 조회
			startdate = startdate.substring(0, 10);
			enddate = enddate.substring(0, 10);
			
			data = attendanceDao.selectTotalAttendanceByMidAndAdate(mid, startdate, enddate);
			
			for (Attendance info : data) {   

				String adate = format1.format(info.getAdate());
				
				// log.info("info: " + info.toString());
				
				String acheckin = "";
				String acheckout = "";
				
				// checkin, checkout -> date to string
				if (info.getAcheckin() != null) {
					acheckin = format2.format(info.getAcheckin()).substring(11, 16);
				} 
				
				if (info.getAcheckout() != null) {
					acheckout = format2.format(info.getAcheckout()).substring(11, 16);
				}
			
				String astatus = "";
				boolean aconfirm = false;
				
				if (!info.isAconfirm()) {
					astatus = "출결 승인 전";
				} else {
					aconfirm = true;
					astatus = info.getAstatus();
				}
				
				boolean anconfirm = false;
				// mid 가 adate 에 사유를 작성한게 있는지 조회
				AttendanceInfoResponseDTO reason = attendanceNotesDao
						.selectReasonByMid(mid, info.getAdate());
						
				// log.info("reason: " + reason.toString());
				
				if (reason != null) { // 사유를 작성한 경우
					log.info("사유 작성한게 없음");
					anconfirm = true;
				} 
				
				TraineeAttendanceDetailResponseDTO attendance = TraineeAttendanceDetailResponseDTO.builder()
						.adate(adate)
						.acheckin(acheckin)
						.acheckout(acheckout)
						.astatus(astatus)
						.aconfirm(aconfirm)
						.anconfirm(anconfirm)
						.build();
				
				response.add(attendance);
			} 
		}
		return response;
	}
	
	// ecname, cname, adate 기준으로 교육생 출결 목록 조회
	@Transactional
	public List<TraineeAttendanceListResponseDTO> listAttendance(
			String ecname, String cname, String adate) throws ParseException {
		// adate 세팅 String to Date
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String adateStr = adate.substring(0, 10);
					        
		Date date = format.parse(adateStr);
		
		// cname 에 해당하는 course 정보 가져오기
		Course course = courseDao.selectCourseInfoByCname(cname);
		// 교육과정의 총 훈련일수
		int crequireddate = course.getCrequireddate();
		
		// 교육생 출결 정보 가져오기 
		List<TraineeAttendanceListResponseDTO> data = 
				attendanceDao.selectAttendanceList(course.getCno(), date);
		
		log.info("crequireddate: " + crequireddate);
		
		for (TraineeAttendanceListResponseDTO temp : data) {
			// log.info("temp: " + temp.toString());
			double percentage = calPercentage(temp.getApprovecnt(), 
					course.getCrequireddate());
			
			String result = String.format("%.1f", percentage);
			
			String acheckin = "";
			String acheckout = "";
			
			if (temp.getAcheckin() != null) {
				acheckin = temp.getAcheckin();
				acheckin = acheckin.substring(11, 16);
				temp.setAcheckin(acheckin);
			}
			
			if (temp.getAcheckout() != null) {
				acheckout = temp.getAcheckout();
				acheckout = acheckout.substring(11, 16);
				temp.setAcheckout(acheckout);
			}
			
			temp.setPercentage(result);
			temp.setCrequireddate(course.getCrequireddate());
			
			
		}
		
		return data;
	}
	
	// 교육생 출결 정보 상세 조회
	public TraineeAttendanceListResponseDTO dashboardDetail(String mid, 
			String adate) throws ParseException{
		// adate 세팅 String to Date
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String adateStr = adate.substring(0, 10);
							        
		Date date = format.parse(adateStr);
		
		TraineeInfo trainee = traineeInfoDao.selectByMid(mid);
		
		
		// cname 에 해당하는 course 정보 가져오기
		Course course = courseDao.selectByCno(trainee.getCno());
		// 교육과정의 총 훈련일수
		int crequireddate = course.getCrequireddate();
		
		// 교육생 출결 정보 가져오기 
		List<TraineeAttendanceListResponseDTO> data = 
				attendanceDao.selectAttendanceList(course.getCno(), date);
		
		TraineeAttendanceListResponseDTO result = new TraineeAttendanceListResponseDTO();
		
		for (TraineeAttendanceListResponseDTO temp : data) {
			if (temp.getMid().equals(mid)) {
				result.setMname(temp.getMname());
				result.setMid(mid);
				result.setCrequireddate(crequireddate);

				// log.info("temp: " + temp.toString());
				double percentage = calPercentage(temp.getApprovecnt(), 
						course.getCrequireddate());
				
				String result2 = String.format("%.1f", percentage);
				
				String acheckin = "";
				String acheckout = "";
				
				if (temp.getAcheckin() != null) {
					acheckin = temp.getAcheckin();
					acheckin = acheckin.substring(11, 16);
					result.setAcheckin(acheckin);
				}
				
				if (temp.getAcheckout() != null) {
					acheckout = temp.getAcheckout();
					acheckout = acheckout.substring(11, 16);
					result.setAcheckout(acheckout);
				}
				
				result.setApprovecnt(temp.getApprovecnt());
				result.setLatenesscnt(temp.getLatenesscnt());
				result.setAbsencecnt(temp.getAbsencecnt());
				
				result.setPercentage(result2);
				result.setCrequireddate(course.getCrequireddate());
			}

		}
				
		return result;
	}
	
	
	// ecname, cname 파라미터 기준으로 출결 승인 조회
	public List<TraineeApproveAttendanceListResponseDTO> listApproveAttendnace(
			String ecname, String cname, String adate) throws ParseException{
		// adate 세팅 String to Date
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String adateStr = adate.substring(0, 10);
							        
		Date date = format.parse(adateStr);
		
		// cname 에 해당하는 course 정보 가져오기
		Course course = courseDao.selectCourseInfoByCname(cname);
		
		// 교육생 출결 정보 가져오기 
		List<TraineeApproveAttendanceListResponseDTO> data = 
				attendanceDao.selectAttendanceApproveList(course.getCno(), date);
		
		for (TraineeApproveAttendanceListResponseDTO info : data) {
			String acheckin = "";
			String acheckout = "";
			
			log.info("info: " + info);
			
			if (info.getAcheckin() != null) {
				acheckin = info.getAcheckin();
				acheckin = acheckin.substring(11, 16);
				info.setAcheckin(acheckin);
			} else {
				info.setAcheckin("-");
			}
			
			if (info.getAcheckout() != null) {
				acheckout = info.getAcheckout();
				acheckout = acheckout.substring(11, 16);
				info.setAcheckout(acheckout);
			} else {
				info.setAcheckout("-");
			}
			
			
			if (info.getAstatus() == null) {
				info.setAstatus("출결 승인 전");
			} 
			
			// 사유 
			boolean reasonable = false;
			boolean anconfirm = false;
			// mid 가 adate 에 사유를 작성한게 있는지 조회
			AttendanceInfoResponseDTO reason = attendanceNotesDao
					.selectReasonByMid(info.getMid(), date);
					
			// log.info("reason: " + reason.toString());
			
			if (reason != null) { // 사유를 작성한 경우
				log.info("사유 작성한게 있음");
				
				reasonable = true;
				anconfirm = reason.isAnconfirm();
			} 
			info.setReasonable(reasonable);	
			info.setAnconfirm(anconfirm);
		}
		
		return data;	
	}

	// 교육생 입실 시간, 퇴실 시간, 과제 작성 여부 조회 기능
	public UserAttendanceTimeInfoResponseDTO getAttendanceTime(
			String mid, String adate) throws ParseException {
		// adate 세팅 String to Date
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String adateStr = adate.substring(0, 10);
									        
		Date date = format.parse(adateStr);
				
		log.info("mid: " + mid);
		log.info("adate: " + adate);
		
		Attendance info = attendanceDao.selectByMidAndAdate(mid, date);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

     	Date checkin = new Date();
     	Date checkout = new Date();
     	
		UserAttendanceTimeInfoResponseDTO response = new UserAttendanceTimeInfoResponseDTO();
		response.setMid(mid);
	 
		if (info.getAcheckin() != null) {
			checkin = info.getAcheckin();
			response.setAcheckin(sdf.format(checkin));
		}
		if (info.getAcheckout() != null) {
			checkout = info.getAcheckout();
			response.setAcheckout(sdf.format(checkout));
		}
		
		response.setAcheckinstatus(info.isAcheckinstatus());
		response.setAcheckoutstatus(info.isAcheckoutstatus());
		
		// 과제 작성 여부 조회
     	ReferenceData reference = referenceDataDao.selectByMidAndRefdate(mid, date);
     	if (reference != null) {
     		response.setReferencestatus(true);
     	} else {
     		response.setReferencestatus(false);
     	}
		
		return response;
	}
	
	
	// 교육생의 정상출결일수 기준으로 출석률 구하는 메소드
	public double calPercentage(int approvecnt, int crequireddate) {
		double percentage = 1.0;
		
		percentage = ((double) approvecnt / crequireddate) * 100;

		//percentage = Math.round(percentage); 
		return percentage;
	}
	
	
	// 검증 메소드 ------------------------------------------------------
	// 클라이언트 IP 와 교육장 IP 비교하는 메소드
	public boolean validationIPMatch(String clientIP) {
		if (clientIP == null) {
			throw new RuntimeException("클라이언트 IP가 요청되지 않았습니다.");
		} else {
			if (CENTERIP.equals(clientIP.substring(3))) {
				log.info("교육장 IP 와 클라이언트 IP가 일치합니다.");
				clientIP = clientIP.substring(3);
			} else {
				throw new RuntimeException("교육장 IP 와 클라이언트 IP가 일치하지 않습니다.");
			}
		}
		return true;
	}

}
