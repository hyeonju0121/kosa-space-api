package com.mycompany.kosa_space.dao;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.Notice;

@Mapper
public interface NoticeDao {
	
	public int insert(Notice notice);
}
