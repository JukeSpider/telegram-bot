package com.juke.service;

import com.juke.dto.TaskDto;
import com.juke.entity.TaskEntity;
import com.juke.mapper.ITaskMapper;
import com.juke.repository.ITaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TaskServiceImpl implements ITaskService {

  private final ITaskMapper mapper;
  private final ITaskRepository repository;


  @Override
  public TaskDto findByTaskNumber(String taskNumber) {
    TaskEntity entity = repository.findByTaskNumber(taskNumber);
    return mapper.mapToDto(entity);
  }

  @Override
  public List<TaskDto> findAll() {
    List<TaskEntity> entityList = repository.findAll();
    return mapper.mapToDtoList(entityList);
  }

  @Override
  public TaskDto save(TaskDto dto) {
    TaskEntity entity = mapper.mapToEntity(dto);
    entity.setCreatedAt(LocalDateTime.now());
    entity.setUpdatedAt(LocalDateTime.now());
    repository.save(entity);
    return mapper.mapToDto(entity);
  }

  @Override
  public void deleteByTaskNumber(String taskNumber) {
    repository.deleteByTaskNumber(taskNumber);
  }
}
