package com.juke.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "tasks")
public class TaskEntity extends BaseEntity {

  @Builder
  public TaskEntity(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String taskNumber,
      String content, String points) {
    super(id, createdAt, updatedAt);
    this.taskNumber = taskNumber;
    this.content = content;
    this.points = points;
  }

  @Column(name = "task_number")
  private String taskNumber;

  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "points", nullable = false)
  private String points;
}