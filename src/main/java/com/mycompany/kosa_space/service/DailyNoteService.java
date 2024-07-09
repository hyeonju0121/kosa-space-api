package com.mycompany.kosa_space.service;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.mycompany.kosa_space.dao.CourseDao;
import com.mycompany.kosa_space.dto.Course;
import com.mycompany.kosa_space.dto.request.DailyNoteRequestDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DailyNoteService {
	@Autowired
	private CourseDao courseDao;
	
	// 데일리 노트 제출 기능 (create)
	public void createNotice(DailyNoteRequestDTO request, 
			Authentication authentication) {
		String mid = authentication.getName();
		
		// startdate ~ enddate 총 주차 구하기
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		startDate.set(2024,Calendar.FEBRUARY, 26);
		endDate.set(2024,Calendar.JULY,26);

		int startDayOfWeek = startDate.get(Calendar.DAY_OF_WEEK);
		int endDayOfWeek = endDate.get(Calendar.DAY_OF_WEEK);

		int periodWeek = 0;

		while( !endDate.before( startDate ) ) {
			startDate.add( Calendar.DATE, 7);
			periodWeek++;
		}
				
		if(endDayOfWeek-startDayOfWeek < 0) {
			periodWeek = periodWeek + 1;
		}
				
		log.info("periodWeek: " + periodWeek);

	}
	
	
	// refdate 가 해당 교육과정의 몇주차에 해당하는지 구하는 메소드
	public int weeks(int cno, String refdate) {
		Course course = courseDao.selectByCno(cno);
		
		// cno 에 해당하는 교육과정의 시작일 구하기
		Date startdate = course.getCstartdate();
		
		// Date to Calendar
		// Calendar 객체 생성        
		Calendar calendar = Calendar.getInstance();        
		// Date 객체를 Calendar로 변환        
		calendar.setTime(startdate);   

		
		return 0;
	}
}
