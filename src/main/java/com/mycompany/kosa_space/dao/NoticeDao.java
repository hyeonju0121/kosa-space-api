package com.mycompany.kosa_space.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.Notice;
import com.mycompany.kosa_space.dto.Pager;

@Mapper
public interface NoticeDao {
	
	public int insert(Notice notice);
	
	public List<Notice> selectNoticeByEcname(int ecno);
	
	public List<Notice> selectNoticePagerByEcname(int ecno, Pager pager);
}
