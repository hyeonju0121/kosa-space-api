package com.mycompany.kosa_space.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycompany.kosa_space.dao.AttendanceDao;
import com.mycompany.kosa_space.dao.TraineeInfoDao;
import com.mycompany.kosa_space.dto.Attendance;
import com.mycompany.kosa_space.dto.TraineeInfo;
import com.mycompany.kosa_space.dto.request.AttendanceTraineeRequestDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AttendanceService {
	
	@Autowired
	private AttendanceDao attendanceDao;
	
	@Autowired
	private TraineeInfoDao traineeInfoDao;
	
	
	private static final String CENTERIP = "125.131.208.230";
	
	public void checkin(String ip, AttendanceTraineeRequestDTO attendance) throws Exception {
		// 교육장 IP 와 클라이언트 IP가 일치하지 않을 경우 에러처리 
		String clientIP = ip;
		
		if (clientIP == null) {
			throw new RuntimeException("클라이언트 IP가 요청되지 않았습니다.");
		} else {
			if (CENTERIP.equals(ip.substring(3))) {
				log.info("교육장 IP 와 클라이언트 IP가 일치합니다.");
				clientIP = ip.substring(3);
			} else {
				throw new RuntimeException("교육장 IP 와 클라이언트 IP가 일치하지 않습니다.");
			}
		}
		
		// 교육생 총 정상출결일, 총 지각일, 총 결석일에 대한 정보 가져오기
		int approveCnt = 0;
		int latenessCnt = 0;
		int absenceCnt = 0;
		
		Attendance attendanceInfo = attendanceDao.selectByMid(attendance.getMid());
		log.info("attendanceInfo: " + attendanceInfo.toString());
		
		if (attendanceInfo != null) {
			approveCnt = attendanceInfo.getApprovecnt();
			latenessCnt = attendanceInfo.getLatenesscnt();
			absenceCnt = attendanceInfo.getAbsencecnt();
		} 
		
		// 시간에 대한 처리 (09: 10 분 이후이면 사용자의 astatus 지각 처리로 업데이트)
		// 09: 10 분 이전이면, 사용자의 astatus 를 정상 출결로 업데이트
		String astatus = "";
		
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
        	astatus = "지각";
        	latenessCnt++; // 총 지각일수 +1 
        	
        } else { // 정상 출결 처리 
        	log.info("정상 출결입니다.");
        	astatus = "정상출결";
        }
        
        // adate 생성
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        String adateStr = attendance.getAttendancetime().substring(0, 10);
        
        Date adate = format2.parse(adateStr);
        
        // checkin 시간 string to date
        SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date acheckin = format3.parse(attendance.getAttendancetime());
        
        // 교육생이 현재 이수중인 교육과정 정보 가져오기 (cno)
        TraineeInfo trainee = traineeInfoDao.selectByMid(attendance.getMid());
        int cno = trainee.getCno();
        
        // Attendance 객체 생성
        Attendance data = Attendance.builder()
        		.mid(attendance.getMid())
        		.adate(adate)
        		.acheckin(acheckin)
        		.astatus(astatus)
        		.aconfirm(false)
        		.cno(cno)
        		.approvecnt(approveCnt)
        		.latenesscnt(latenessCnt)
        		.absencecnt(absenceCnt)
        		.build();
		
        // DB 업데이트
        log.info(data.toString());
        
        attendanceDao.insert(data);
	}
	
	

}
