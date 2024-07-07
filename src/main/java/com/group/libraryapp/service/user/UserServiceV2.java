package com.group.libraryapp.service.user;

import com.group.libraryapp.domain.user.User;
import com.group.libraryapp.domain.user.UserRepository;
import com.group.libraryapp.dto.user.request.UserCreateRequest;
import com.group.libraryapp.dto.user.request.UserUpdateRequest;
import com.group.libraryapp.dto.user.response.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceV2 {

    private final UserRepository userRepository;

    public UserServiceV2(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveUser(UserCreateRequest request) {
        userRepository.save(new User(request.getName(), request.getAge()));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsers(){
        return userRepository.findAll().stream() //자동으로 해당 테이블의 모든 데이터 가져옴
        .map(UserResponse::new)
                .collect(Collectors.toList()); //UserReponse 로 붙여서, 다시 List 로 반환
    }

    @Transactional
    public void updateUser(UserUpdateRequest request) {
        //결과 : Optional<User> , 결과가 있으면 User 객체로 데이터가 들어옴 , 없으면 x
        User user = userRepository.findById(request.getId())
                .orElseThrow(IllegalAccessError::new); //아이디 가 없으면 예외를 던져버림

        user.updateName(request.getName());
        //userRepository.save(user); //영속성 컨텍스트
    }

    @Transactional
    public void deleteUser(String name) {
        //이름 기준 조회는 없다.
        User user = userRepository.findByName(name).orElseThrow(IllegalAccessError::new);
        if(user == null) {
            throw new IllegalArgumentException();
        }

        userRepository.delete(user);
    }


}
