package com.juke.mapper;

import com.juke.dto.TaskDto;
import com.juke.dto.TaskDto.TaskDtoBuilder;
import com.juke.entity.TaskEntity;
import com.juke.entity.TaskEntity.TaskEntityBuilder;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-02-21T19:35:48+0700",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.1 (Oracle Corporation)"
)
@Component
public class ITaskMapperImpl implements ITaskMapper {

    @Override
    public TaskDto mapToDto(TaskEntity entity) {
        if ( entity == null ) {
            return null;
        }

        TaskDtoBuilder taskDto = TaskDto.builder();

        taskDto.id( entity.getId() );
        taskDto.createdAt( entity.getCreatedAt() );
        taskDto.updatedAt( entity.getUpdatedAt() );
        taskDto.taskNumber( entity.getTaskNumber() );
        taskDto.content( entity.getContent() );
        taskDto.points( entity.getPoints() );

        return taskDto.build();
    }

    @Override
    public TaskEntity mapToEntity(TaskDto dto) {
        if ( dto == null ) {
            return null;
        }

        TaskEntityBuilder taskEntity = TaskEntity.builder();

        taskEntity.id( dto.getId() );
        taskEntity.createdAt( dto.getCreatedAt() );
        taskEntity.updatedAt( dto.getUpdatedAt() );
        taskEntity.taskNumber( dto.getTaskNumber() );
        taskEntity.content( dto.getContent() );
        taskEntity.points( dto.getPoints() );

        return taskEntity.build();
    }

    @Override
    public List<TaskDto> mapToDtoList(List<TaskEntity> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<TaskDto> list = new ArrayList<TaskDto>( entityList.size() );
        for ( TaskEntity taskEntity : entityList ) {
            list.add( mapToDto( taskEntity ) );
        }

        return list;
    }

    @Override
    public List<TaskEntity> mapToEntityList(List<TaskDto> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<TaskEntity> list = new ArrayList<TaskEntity>( dtoList.size() );
        for ( TaskDto taskDto : dtoList ) {
            list.add( mapToEntity( taskDto ) );
        }

        return list;
    }
}
