package com.mycompany.kosa_space.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycompany.kosa_space.dao.CourseDao;
import com.mycompany.kosa_space.dao.MemberDao;
import com.mycompany.kosa_space.dao.ReferenceDataDao;
import com.mycompany.kosa_space.dao.TraineeInfoDao;
import com.mycompany.kosa_space.dto.Course;
import com.mycompany.kosa_space.dto.DailyNote;
import com.mycompany.kosa_space.dto.Member;
import com.mycompany.kosa_space.dto.Pager;
import com.mycompany.kosa_space.dto.ReferenceData;
import com.mycompany.kosa_space.dto.TraineeInfo;
import com.mycompany.kosa_space.dto.request.DailyNoteRequestDTO;
import com.mycompany.kosa_space.dto.response.DailyNoteDetailResponseDTO;
import com.mycompany.kosa_space.dto.response.DailyNoteTraineeListResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DailyNoteService {
	@Autowired
	private ReferenceDataDao referenceDataDao;
	
	@Autowired
	private CourseDao courseDao;
	
	@Autowired 
	private TraineeInfoDao traineeInfoDao;
	
	@Autowired
	private MemberDao memberDao;
	
	// 데일리노트 과제 제출 기능 (create)
	@Transactional
	public void createDailyNote(DailyNoteRequestDTO request, 
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
	
	// refno 기준으로 데일리노트 과제 단건조회 
	public DailyNoteRequestDTO infoDailyNote(int refno) {
		
		DailyNoteRequestDTO note = referenceDataDao.selectInfoByRefno(refno);
		
		return note;
	}
	
	
	// 데일리노트 과제 수정 기능
	public void updateDailyNote(int refno, 
			DailyNoteRequestDTO request) throws ParseException {
		ReferenceData note = referenceDataDao.selectByRefno(refno);
		
		// 교육생이 제출한 날짜가 교육과정의 몇주차에 속하는 과제인지 찾기
		int refweekInt = weeks(note.getCno(), request.getRefdate());
		String refweek = String.valueOf(refweekInt);
		refweek += "주차";
		
		// refdate String to Date
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String refDateStr = request.getRefdate().substring(0, 10);
		
		Date refdate = format.parse(refDateStr);
		
		ReferenceData dailyNote = ReferenceData.builder()
				.cno(note.getCno())
				.mid(note.getMid())
				.reftitle(request.getReftitle())
				.refurl(request.getRefurl())
				.refweek(refweek)
				.refdate(refdate)
				.build();
		
		// DB update
		referenceDataDao.update(refno, dailyNote);
	}
	
	// 데일리노트 과제 교육생&해당 주차별 상세조회 기능
	public List<DailyNoteDetailResponseDTO> detailDailyNote(String mid, 
			String refweek) {
		
		List<DailyNoteDetailResponseDTO> response = referenceDataDao
				.selectByMidAndRefWeek(mid, refweek);
		
		for (DailyNoteDetailResponseDTO data : response) {
			String refdate = data.getRefdate();
			data.setRefdate(refdate.substring(0, 10));
		}
		
		return response;
	}
	
	// 데일리노트 과제 삭제 기능
	public void deleteDailyNote(int refno) {
		// DB delete
		referenceDataDao.delete(refno);
	}
	
	
	// 교육생 데일리과제 제출 목록 기능
	public Map<String, Object> traineeNoteList(String mid, 
			String adate, int pageNo) throws ParseException {
		// mname 이름 정보 찾기
		Member member = memberDao.selectByMid(mid);
		String mname = member.getMname();
		
		// 교육생이 수강하고 있는 교육과정 번호 찾기
		TraineeInfo trainee = traineeInfoDao.selectByMid(mid);
		int cno = trainee.getCno();
		
		// 교육생이 제출한 날짜가 교육과정의 몇주차에 속하는 과제인지 찾기
		int refweekInt = weeks(cno, adate);
	
		List<String> weeksList = new ArrayList<>();
		List<DailyNoteTraineeListResponseDTO> response = new ArrayList<>();
		for (int i = 1; i <= refweekInt; i++) {
			String submitstatus = "미제출";
			String refweek = String.valueOf(i);
			refweek += "주차";
			
			weeksList.add(refweek);
			
			// 교육생 과제 목록 가져오기
			List<DailyNoteDetailResponseDTO> noteList = referenceDataDao
					.selectByMidAndRefWeek(mid, refweek);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			int submitCnt = 0;
			for (DailyNoteDetailResponseDTO note : noteList) {
				Calendar cal = Calendar.getInstance();
				String date = note.getRefdate();
				
				cal.setTime(sdf.parse(date)); 
				
				int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

				switch(dayOfWeek) {
					case 2: submitCnt++;
					case 3: submitCnt++;
					case 4: submitCnt++;
					case 5: submitCnt++;
					case 6: submitCnt++;
				}
				
				if (submitCnt >= 5) {
					submitstatus = "제출";
				} else {
					submitstatus = "미제출";
				}
			}

			DailyNoteTraineeListResponseDTO data = DailyNoteTraineeListResponseDTO.builder()
					.mid(mid)
					.mname(mname)
					.refweek(refweek)
					.submitstatus(submitstatus)
					.build();
			
			response.add(data);
			
//			log.info("주차: " +  refweek);
//			log.info("제출여부: " + submitstatus);
//			log.info("data: " + data.toString());
		}
		
		// 페이징 적용하기 위해 역순으로 정렬함 (20주차, 19주차, ... 1주차)
		Collections.reverse(response);
		
		int totalRows = response.size();
		
		// Pager 객체 생성
		Pager pager = new Pager(5, 7, totalRows, pageNo);
		
		// 페이징된 데이터 추출
        int startIndex = pager.getStartRowIndex();
        int endIndex = Math.min(startIndex + pager.getRowsPerPage(), response.size());

        List<DailyNoteTraineeListResponseDTO> result = response.subList(startIndex, endIndex);
		
        // 반환할 Map에 페이징 정보와 데이터 추가
        Map<String, Object> map = new HashMap<>();
        map.put("pager", pager);
        map.put("result", result);

		return map;
	}

	
	// 월 ~ 금 요일을 계산하고, 해당 카운트 반환하는 메소드
	public int calDayOfWeekCnt(Calendar cal) {
		return 0;
		
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
