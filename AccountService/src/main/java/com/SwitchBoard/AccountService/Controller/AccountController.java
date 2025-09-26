package com.SwitchBoard.AccountService.Controller;

import com.SwitchBoard.AccountService.Dto.AccountDto;
import com.SwitchBoard.AccountService.Dto.ApiResponse;
import com.SwitchBoard.AccountService.Entity.Account;
import com.SwitchBoard.AccountService.Service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@Slf4j
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createUser(@RequestBody AccountDto account) {
        log.info("AccountController : createUser : Received request to create account - {}", account.getEmail());
        log.debug("AccountController : createUser : Account details - {}", account);
        try {
            ApiResponse apiResponse = accountService.createProfile(account);
            if (!apiResponse.isSuccess()) {
                log.warn("AccountController : createUser : Failed to create account - {}", account.getEmail());
                return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
            }
            log.info("AccountController : createUser : Successfully created account - {}", account.getEmail());
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("AccountController : createUser : Exception while creating account - {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Account> getUser(@PathVariable Long id) {
        log.info("AccountController : getUser : Fetching user with id - {}", id);
        try {
            Account user = accountService.getUser(id);
            log.info("AccountController : getUser : Successfully retrieved user - {}", id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("AccountController : getUser : Exception while retrieving user - {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<AccountDto>> getAllUsers() {
        log.info("AccountController : getAllUsers : Fetching all users");
        try {
            List<AccountDto> userDTOS = accountService.getAllUsers();
            log.info("AccountController : getAllUsers : Successfully retrieved {} users", userDTOS.size());
            return ResponseEntity.ok(userDTOS);
        } catch (Exception e) {
            log.error("AccountController : getAllUsers : Exception while retrieving all users - {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @PatchMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id, @RequestBody Account updates) {
        log.info("AccountController : updateUser : Received request to update account - {}", id);
        log.debug("AccountController : updateUser : Update details - {}", updates);
        try {
            ApiResponse apiResponse = accountService.updateProfile(id, updates);
            if (!apiResponse.isSuccess()) {
                log.warn("AccountController : updateUser : Failed to update account - {}", id);
                return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
            }
            log.info("AccountController : updateUser : Successfully updated account - {}", id);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            log.error("AccountController : updateUser : Exception while updating account - {}", e.getMessage(), e);
            throw e;
        }
    }
}
