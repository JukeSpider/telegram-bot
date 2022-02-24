package com.juke.service;

import com.juke.dto.TaskDto;
import java.util.List;

public interface ITaskService {

  TaskDto findByTaskNumber(String taskNumber);

  List<TaskDto> findAll();

  TaskDto save(TaskDto dto);

  void deleteByTaskNumber(String taskNumber);
}