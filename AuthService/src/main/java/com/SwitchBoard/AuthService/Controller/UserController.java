package com.SwitchBoard.AuthService.Controller;

import com.SwitchBoard.AuthService.DTO.ApiResponse;
import com.SwitchBoard.AuthService.DTO.UserDto;
import com.SwitchBoard.AuthService.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/user")
    public ResponseEntity<ApiResponse> createUser(@RequestBody UserDto userDto) {
        log.info("UserController : createUser : Request received for creating user with email - {}", userDto.getEmail());
        ApiResponse apiResponse = userService.createUser(userDto);
        log.info("UserController : createUser : User created successfully with email - {}", userDto.getEmail());
        return ResponseEntity.ok().body(apiResponse);
    }
}
