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
@Table(name = "players")
public class PlayerEntity extends BaseEntity {

  @Builder
  public PlayerEntity(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, boolean admin,
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

  @Column(name = "admin")
  private boolean admin;

  @Column(name = "telegram_id", nullable = false)
  private Long telegramId;

  @Column(name = "user_name")
  private String userName;

  @Column(name = "phone")
  private String phone;

  @Column(name = "java_score")
  private Long javaScore;

  @Column(name = "python_score")
  private Long goScore;

  @Column(name = "data_score")
  private Long dataScore;
}