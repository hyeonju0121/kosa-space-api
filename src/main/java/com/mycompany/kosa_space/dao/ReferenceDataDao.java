package com.mycompany.kosa_space.dao;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.ReferenceData;

@Mapper
public interface ReferenceDataDao {
	
	public int insert(ReferenceData dailyNote);

}
