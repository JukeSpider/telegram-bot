package com.juke.service;

import com.juke.dto.PlayerDto;
import java.util.List;

public interface IPlayerService {

  PlayerDto findByUsername(String username);

  List<PlayerDto> findAll();

  void deleteByUsername(String username);

  PlayerDto save(PlayerDto dto);

  PlayerDto findByTelegramId(Long telegramId);

  boolean hasTelegramId(Long telegramId);
}
