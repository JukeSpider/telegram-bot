package com.juke.mapper;

import com.juke.dto.TaskDto;
import com.juke.entity.TaskEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ITaskMapper extends IBaseMapper<TaskEntity, TaskDto> {

}