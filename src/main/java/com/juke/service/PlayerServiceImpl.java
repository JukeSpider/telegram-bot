package com.juke.service;

import com.juke.dto.PlayerDto;
import com.juke.entity.PlayerEntity;
import com.juke.mapper.IPlayerMapper;
import com.juke.repository.IPlayerRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter
@Getter
@RequiredArgsConstructor
@Service
public class PlayerServiceImpl implements IPlayerService {

  private final IPlayerRepository repository;
  private final IPlayerMapper mapper;

  @Override
  public List<PlayerDto> findAll() {
    return mapper.mapToDtoList(repository.findAll());
  }

  @Override
  public PlayerDto save(PlayerDto dto, boolean admin) {
    PlayerEntity entity = mapper.mapToEntity(dto);
    entity.setCreatedAt(LocalDateTime.now());
    entity.setUpdatedAt(LocalDateTime.now());
    entity.setJavaScore(0L);
    entity.setGoScore(0L);
    entity.setDataScore(0L);
    entity.setAdmin(admin);
    repository.save(entity);
    return mapper.mapToDto(entity);
  }

  @Override
  public PlayerDto findByTelegramId(Long telegramId) {
    if (repository.findByTelegramId(telegramId) == null) {
      return null;
    }

    PlayerEntity entity = repository.findByTelegramId(telegramId);
    return mapper.mapToDto(entity);
  }

  @Override
  public boolean hasTelegramId(Long telegramId) {
    return findByTelegramId(telegramId) != null;
  }
}