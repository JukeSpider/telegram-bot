package com.juke.mapper;

import java.util.List;

public interface IBaseMapper<E, D> {

  D mapToDto(E entity);

  E mapToEntity(D dto);

  List<D> mapToDtoList(List<E> entityList);

  List<E> mapToEntityList(List<D> dtoList);
}