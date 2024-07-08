package com.mycompany.kosa_space.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.Notice;
import com.mycompany.kosa_space.dto.Pager;
import com.mycompany.kosa_space.dto.response.NoticeEduCenterCourseCombineDTO;

@Mapper
public interface NoticeDao {
	
	public int insert(Notice notice);
	
	public List<Notice> selectNoticeByEcname(int ecno);
	
	public List<Notice> selectNoticePagerByEcname(int ecno, Pager pager);

	public List<NoticeEduCenterCourseCombineDTO> selectNoticeByEcnameAndCnameAndNcategory(
					String ecname, String cname, String ncategory);
	
	public List<NoticeEduCenterCourseCombineDTO> selectPageNoticeByEcnameAndCnameAndNcategory(
					String ecname, String cname, String ncategory, Pager pager);
}
