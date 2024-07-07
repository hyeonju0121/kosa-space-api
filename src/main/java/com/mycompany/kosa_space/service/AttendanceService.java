package com.mycompany.kosa_space.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycompany.kosa_space.dao.AttendanceDao;
import com.mycompany.kosa_space.dao.TraineeInfoDao;
import com.mycompany.kosa_space.dto.Attendance;
import com.mycompany.kosa_space.dto.AttendanceNotes;
import com.mycompany.kosa_space.dto.TraineeInfo;
import com.mycompany.kosa_space.dto.request.AttendanceTraineeRequestDTO;
import com.mycompany.kosa_space.dto.response.TraineeResponseDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AttendanceService {
	
	@Autowired
	private AttendanceDao attendanceDao;
	
	@Autowired
	private TraineeInfoDao traineeInfoDao;
	
	
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
	public void createReason(AttendanceNotes request) {
		
		// attendance 테이블 정보 업데이트 필요
		// 교육생이 입력한 출결 사유 카테고리에 따른 astatus 값 업데이트 필요
		String category = request.getAcnategory();
		String astatus = "";
		if (category.equals("결석")) {
			astatus = "결석";
		}
		
		// attendancenotes 테이블 정보 업데이트 필요
	}
	
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
