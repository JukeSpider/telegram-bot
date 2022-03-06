package com.juke.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
public class PlayerDto extends BaseDto {

  @Builder
  public PlayerDto(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, boolean admin,
      Long telegramId, String userName, String phone, Long javaScore, Long goScore,
      Long dataScore) {
    super(id, createdAt, updatedAt);
    this.admin = admin;
    this.telegramId = telegramId;
    this.userName = userName;
    this.phone = phone;
    this.javaScore = javaScore;
    this.goScore = goScore;
    this.dataScore = dataScore;
  }

  private boolean admin;

  private Long telegramId;

  private String userName;

  private String phone;

  private Long javaScore;

  private Long goScore;

  private Long dataScore;

  public Long getTotalScore() {
    return javaScore + goScore + dataScore;
  }
}