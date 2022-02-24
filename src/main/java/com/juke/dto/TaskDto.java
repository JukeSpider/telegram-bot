package com.juke.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TaskDto extends BaseDto {

  @Builder
  public TaskDto(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String taskNumber,
      String content, String points) {
    super(id, createdAt, updatedAt);
    this.taskNumber = taskNumber;
    this.content = content;
    this.points = points;
  }

  private String taskNumber;

  private String content;

  private String points;
}
