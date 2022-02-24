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
  public PlayerDto(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Long telegramId,
      String userName, String phone, Long javaScore, Long pythonScore, Long dataScore) {
    super(id, createdAt, updatedAt);
    this.telegramId = telegramId;
    this.userName = userName;
    this.phone = phone;
    this.javaScore = javaScore;
    this.pythonScore = pythonScore;
    this.dataScore = dataScore;
  }

  private Long telegramId;

  private String userName;

  private String phone;

  private Long javaScore;

  private Long pythonScore;

  private Long dataScore;

  @Override
  public String toString() {
    return "username: " + userName +
        "\n phone: " + userName +
        "\n java score: " + javaScore +
        "\n python score: " + pythonScore +
        "\n data score:" + dataScore;
  }
}