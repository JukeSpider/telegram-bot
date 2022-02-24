package com.juke.mapper;

import com.juke.dto.PlayerDto;
import com.juke.dto.PlayerDto.PlayerDtoBuilder;
import com.juke.entity.PlayerEntity;
import com.juke.entity.PlayerEntity.PlayerEntityBuilder;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-02-21T19:35:48+0700",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.1 (Oracle Corporation)"
)
@Component
public class IPlayerMapperImpl implements IPlayerMapper {

    @Override
    public PlayerDto mapToDto(PlayerEntity entity) {
        if ( entity == null ) {
            return null;
        }

        PlayerDtoBuilder playerDto = PlayerDto.builder();

        playerDto.id( entity.getId() );
        playerDto.createdAt( entity.getCreatedAt() );
        playerDto.updatedAt( entity.getUpdatedAt() );
        playerDto.telegramId( entity.getTelegramId() );
        playerDto.userName( entity.getUserName() );
        playerDto.phone( entity.getPhone() );
        playerDto.javaScore( entity.getJavaScore() );
        playerDto.pythonScore( entity.getPythonScore() );
        playerDto.dataScore( entity.getDataScore() );

        return playerDto.build();
    }

    @Override
    public PlayerEntity mapToEntity(PlayerDto dto) {
        if ( dto == null ) {
            return null;
        }

        PlayerEntityBuilder playerEntity = PlayerEntity.builder();

        playerEntity.id( dto.getId() );
        playerEntity.createdAt( dto.getCreatedAt() );
        playerEntity.updatedAt( dto.getUpdatedAt() );
        playerEntity.telegramId( dto.getTelegramId() );
        playerEntity.userName( dto.getUserName() );
        playerEntity.phone( dto.getPhone() );
        playerEntity.javaScore( dto.getJavaScore() );
        playerEntity.pythonScore( dto.getPythonScore() );
        playerEntity.dataScore( dto.getDataScore() );

        return playerEntity.build();
    }

    @Override
    public List<PlayerDto> mapToDtoList(List<PlayerEntity> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<PlayerDto> list = new ArrayList<PlayerDto>( entityList.size() );
        for ( PlayerEntity playerEntity : entityList ) {
            list.add( mapToDto( playerEntity ) );
        }

        return list;
    }

    @Override
    public List<PlayerEntity> mapToEntityList(List<PlayerDto> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<PlayerEntity> list = new ArrayList<PlayerEntity>( dtoList.size() );
        for ( PlayerDto playerDto : dtoList ) {
            list.add( mapToEntity( playerDto ) );
        }

        return list;
    }
}
