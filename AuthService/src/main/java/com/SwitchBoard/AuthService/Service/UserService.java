package com.SwitchBoard.AuthService.Service;

import com.SwitchBoard.AuthService.DTO.ApiResponse;
import com.SwitchBoard.AuthService.DTO.UserDto;
import com.SwitchBoard.AuthService.Exception.BadRequestException;
import com.SwitchBoard.AuthService.Kafka.Service.UserEventProducerService;
import com.SwitchBoard.AuthService.Model.User;
import com.SwitchBoard.AuthService.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserEventProducerService userEventProducerService;

    public ApiResponse createUser(UserDto userDto) {
        log.info("UserService : createUser : Creating user with email - {}", userDto.getEmail());

        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            log.warn("UserService : createUser : User with email {} already exists", userDto.getEmail());
            throw new BadRequestException("User with email " + userDto.getEmail() + " already exists");
        }

        try {
            User user = User.builder()
                    .name(userDto.getName())
                    .email(userDto.getEmail())
                    .build();
            
            log.debug("UserService : createUser : Saving user to database");
                   User savedUser = userRepository.save(user);

            log.info("UserService : createUser : User saved to database with ID - {}", savedUser.getId());
            
            log.debug("UserService : createUser : Publishing user created event");
            userEventProducerService.publishUserCreated(savedUser.getName(), savedUser.getEmail(), new HashMap<>());
            log.info("UserService : createUser : User created event published successfully");
            
            return ApiResponse.success("User created successfully with emailId: " + userDto.getEmail(), true);
        } catch (Exception e) {
            log.error("UserService : createUser : Error creating user - {}", e.getMessage());
            throw e;
        }
    }
}
