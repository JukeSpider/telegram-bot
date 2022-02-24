package com.juke.repository;

import com.juke.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPlayerRepository extends JpaRepository<PlayerEntity, Long> {

  PlayerEntity findByTelegramId(Long telegramId);

  PlayerEntity findByUserName(String username);

  void deleteByUserName(String username);
}