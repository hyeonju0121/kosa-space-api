package com.mycompany.kosa_space.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.Notice;
import com.mycompany.kosa_space.dto.Pager;
import com.mycompany.kosa_space.dto.response.NoticeEduCenterCourseCombineDTO;
import com.mycompany.kosa_space.dto.response.NoticeResponseDTO;

@Mapper
public interface NoticeDao {
	
	public int insert(Notice notice);
	
	public NoticeResponseDTO selectByNno(int nno);
	
	public Notice selectNoticeByNno(int nno);
	
	public List<Notice> selectNoticeByEcname(int ecno);
	
	public int selectNoticePagerCountByEcno(int ecno);
	
	public List<Notice> selectNoticePagerByEcname(int ecno, Pager pager);

	public List<NoticeEduCenterCourseCombineDTO> selectNoticeByEcnameAndCnameAndNcategory(
					String ecname, String cname, String ncategory);
	
	public List<NoticeEduCenterCourseCombineDTO> selectPageNoticeByEcnameAndCnameAndNcategory(
					String ecname, String cname, String ncategory, Pager pager);
	
	
	// 특정 교육장에 전체 공지 데이터 가져오기 (ecname: !all, cname: all, ncategory: all)
	public int selectRowsNoticeCategory1(int ecno);
	
	public List<Notice> courseNoticeCategory1(int ecno, Pager pager);
	
	
	// 특정 교육장에 전체 교육과정의 카테고리에 해당하는 공지 데이터 가져오기 (ecname: !all, cname: all, ncategory: !all)
	public int selectRowsNoticeCategory2(int ecno, String ncategory);
	
	public List<Notice> courseNoticeCategory2(int ecno, String ncategory, Pager pager);
	
	// 특정 교육과정에 공지 데이터 조회 ----------------------------------
	public int selectRowsNoticeCategory3(int ecno, int cno);
	
	public List<Notice> courseNoticeCategory3(
			int ecno, int cno, Pager pager);
	// ------------------------------------------------------------
	
	
	// 특정 교육과정에 카테고리에 해당하는 공지 데이터 조회 ----------------------------------
	public int selectRowsNoticeCategory4(int ecno, int cno, String ncategory);
	
	public List<Notice> courseNoticeCategory4(
			int ecno, int cno, String ncategory, Pager pager);
	
	//  모든 교육장에 전체 공지 데이터 가져오기 (ecname: all, cname: all, ncategory: all)
	public int selectRowsNoticeCategory5();
	
	public List<Notice> courseNoticeCategory5(String ncategory, Pager pager);
	
	//  모든 교육장에 전체 공지 데이터 가져오기 (ecname: all, cname: all, ncategory: !all)
	public int selectRowsNoticeCategory6(String ncategory);
	
	public List<Notice> courseNoticeCategory6(String ncategory, Pager pager);
	
	public void deleteByNno(int nno);
	
	
}
