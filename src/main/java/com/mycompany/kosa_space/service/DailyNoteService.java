package com.mycompany.kosa_space.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycompany.kosa_space.dao.CourseDao;
import com.mycompany.kosa_space.dao.ReferenceDataDao;
import com.mycompany.kosa_space.dao.TraineeInfoDao;
import com.mycompany.kosa_space.dto.Course;
import com.mycompany.kosa_space.dto.ReferenceData;
import com.mycompany.kosa_space.dto.TraineeInfo;
import com.mycompany.kosa_space.dto.request.DailyNoteRequestDTO;
import com.mycompany.kosa_space.dto.response.DailyNoteDetailResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DailyNoteService {
	@Autowired
	private ReferenceDataDao referenceDataDao;
	
	@Autowired
	private CourseDao courseDao;
	
	@Autowired TraineeInfoDao traineeInfoDao;
	
	// 데일리 노트 제출 기능 (create)
	@Transactional
	public void createNotice(DailyNoteRequestDTO request, 
			Authentication authentication) throws ParseException {
		String mid = authentication.getName();
		
		// 교육생이 수강하고 있는 교육과정 번호 찾기
		TraineeInfo trainee = traineeInfoDao.selectByMid(mid);
		int cno = trainee.getCno();
		
		// 교육생이 제출한 날짜가 교육과정의 몇주차에 속하는 과제인지 찾기
		int refweekInt = weeks(cno, request.getRefdate());
		String refweek = String.valueOf(refweekInt);
		refweek += "주차";
		
		// refdate String to Date
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String refDateStr = request.getRefdate().substring(0, 10);
					        
		Date refdate = format.parse(refDateStr);
		
		ReferenceData dailyNote = ReferenceData.builder()
				.cno(cno)
				.mid(mid)
				.reftitle(request.getReftitle())
				.refurl(request.getRefurl())
				.refweek(refweek)
				.refdate(refdate)
				.build();
		
		// DB insert
		referenceDataDao.insert(dailyNote);

	}	
	
	// 데일리노트 교육생&해당 주차별 상세조회 기능
	public List<DailyNoteDetailResponseDTO> detailNotice(String mid, 
			String refweek) {
		
		List<DailyNoteDetailResponseDTO> response = referenceDataDao
				.selectByMidAndRefWeek(mid, refweek);
		
		for (DailyNoteDetailResponseDTO data : response) {
			String refdate = data.getRefdate();
			data.setRefdate(refdate.substring(0, 10));
		}
		
		return response;
	}

	
	// Date 객체를 받아 해당 연, 월, 일 을 반환하는 메소드 
	public int[] getMonthFromDate(Date date) {
		int[] result = new int[3];
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		result[0] = calendar.get(Calendar.YEAR);
		result[1] = calendar.get(Calendar.MONTH);
		result[2] = calendar.get(Calendar.DATE);
		
		return result;
	}
	
	
	// refdate 가 해당 교육과정의 몇주차에 해당하는지 구하는 메소드
	public int weeks(int cno, String refdate) throws ParseException {
		// refdate String to Date
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String refDateStr = refdate.substring(0, 10);
			        
	    Date date = format.parse(refDateStr);
		
	    Course course = courseDao.selectByCno(cno);
		
		// cno 에 해당하는 교육과정의 시작일 구하기
		Date startdate = course.getCstartdate();

		// 교육과정 시작일 Calender 객체 생성
		Calendar cstartdate = Calendar.getInstance();
		// 현재 제출하려는 과제의 수업일 날짜 Calender 객체 생성
		Calendar refenddate = Calendar.getInstance();
	    
		int[] result1 = getMonthFromDate(startdate); // [해당연도, 해당월, 해당일]
		int[] result2 = getMonthFromDate(date);

		cstartdate.set(result1[0], result1[1], result1[2]);
		refenddate.set(result2[0], result2[1], result2[2]);
		
		int startDayOfWeek = cstartdate.get(Calendar.DAY_OF_WEEK);
		int endDayOfWeek = refenddate.get(Calendar.DAY_OF_WEEK);

		int periodWeek = 0;

		while( !refenddate.before( cstartdate ) ) {
			cstartdate.add( Calendar.DATE, 7);
			periodWeek++;
		}
		
		if(endDayOfWeek-startDayOfWeek < 0) {
			periodWeek = periodWeek + 1;
		}
		
		// 현재 주차 반환
		return periodWeek;
	}

}
