package com.gridu.store.mapper;

import com.gridu.store.dto.UserDto;
import com.gridu.store.model.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toUserEntity(UserDto userDto);

    UserDto toUserDto(UserEntity userEntity);



}
