package com.juke.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BaseDto {

  private Long id;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}