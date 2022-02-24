package com.juke.mapper;

import com.juke.dto.PlayerDto;
import com.juke.entity.PlayerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IPlayerMapper extends IBaseMapper<PlayerEntity, PlayerDto> {

}