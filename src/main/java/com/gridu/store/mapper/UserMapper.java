package com.gridu.store.mapper;

import com.gridu.store.dto.UserRegistrationRequestDto;
import com.gridu.store.model.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toUserEntity(UserRegistrationRequestDto userRegistrationRequestDto);

    UserRegistrationRequestDto toUserDto(UserEntity userEntity);



}
