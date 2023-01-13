package com.gridu.store.service;

import com.gridu.store.dto.UserDto;
import com.gridu.store.exception.ApiException;
import com.gridu.store.exception.Exceptions;
import com.gridu.store.mapper.UserMapper;
import com.gridu.store.model.UserEntity;
import com.gridu.store.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepo userRepo;
    @Override
    public UserDto register(UserDto userDto) {
        checkIfUserExist(userDto);

        UserEntity userEntity = userMapper.toUserEntity(userDto);
        userEntity = userRepo.save(userEntity);
        return userMapper.toUserDto(userEntity);
    }

    private void checkIfUserExist(UserDto userDto) {
        UserEntity byEmail = userRepo.findByEmail(userDto.getEmail());
        if(byEmail != null) {
            throw new ApiException(Exceptions.USER_EXIST);
        }
    }
}
