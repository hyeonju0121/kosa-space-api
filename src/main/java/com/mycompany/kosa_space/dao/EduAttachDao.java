package com.mycompany.kosa_space.dao;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.kosa_space.dto.EduAttach;

@Mapper
public interface EduAttachDao {
	
	public int insertEduCenter(EduAttach attach);

}
