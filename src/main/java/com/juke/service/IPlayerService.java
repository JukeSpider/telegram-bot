package com.juke.service;

import com.juke.dto.PlayerDto;
import java.util.List;

public interface IPlayerService {

  List<PlayerDto> findAll();

  PlayerDto save(PlayerDto dto, boolean isAdmin);

  PlayerDto findByTelegramId(Long telegramId);

  boolean hasTelegramId(Long telegramId);
}
