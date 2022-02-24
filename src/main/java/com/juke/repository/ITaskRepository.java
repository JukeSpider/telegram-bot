package com.juke.repository;

import com.juke.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITaskRepository extends JpaRepository<TaskEntity, Long> {

  TaskEntity findByTaskNumber(String taskNumber);

  void deleteByTaskNumber(String taskNumber);
}
