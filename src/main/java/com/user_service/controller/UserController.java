package com.user_service.controller;


import com.user_service.dtos.request.EmployeeAddDto;
import com.user_service.dtos.request.UpdateEmployeeDto;
import com.user_service.dtos.response.GetAllEmployeeDto;
import com.user_service.service.abstracts.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/User")
public class UserController {

    private final UserService userService;

    @PostMapping("/AddUser")
    public ResponseEntity<String> addUserList(@RequestBody List<EmployeeAddDto> listOfEmployeeDto) {

        log.trace("Registraion request is received for {} users. ", listOfEmployeeDto.size());
        String addedMessage = userService.addUser(listOfEmployeeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedMessage);
    }

    @GetMapping("GetAllUsers")
    public ResponseEntity<List<GetAllEmployeeDto>> getAllUsers() {
        log.trace("GetAllUsers request is received.");
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @GetMapping("GetAllActiveUsers")
    public ResponseEntity<List<GetAllEmployeeDto>> getAllActiveUsers() {
        log.trace("GetAllActiveUsers request is received.");
        return ResponseEntity.ok().body(userService.getAllActiveUsers());
    }


    @DeleteMapping("/DeleteUserById/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        log.trace("DeleteUserById request is received for the User with id {}", id);
        String deletedMessage = userService.deleteUserById(id);
        return ResponseEntity.ok().body(deletedMessage);
    }

    @PutMapping("/UpdateUser/{id}")
    public ResponseEntity<String> updateEmployeeById(@PathVariable Long id, @RequestBody @Valid UpdateEmployeeDto updateRequest) {
        log.trace("UpdateEmployeeById request is received for the User with id {}", id);
        String updatedMessage = this.userService.updateEmployeeById(id, updateRequest);
        return ResponseEntity.ok().body(updatedMessage);
    }

    @PostMapping("/ReactiveUserById/{id}")
    public ResponseEntity<String> reActiveUserById(@PathVariable Long id) {
        log.trace("ReactiveUserById request is received for the User with id {}", id);
        String reActiveResponseMessage = this.userService.reActiveUserById(id);
        return ResponseEntity.ok().body(reActiveResponseMessage);
    }



    @GetMapping("/GetUsersWithPaginationAndSorting")
    public ResponseEntity<Page<GetAllEmployeeDto>> getUsersWithPagination(@RequestParam(defaultValue = "1") int pageNumber,
                                                                          @RequestParam(defaultValue = "3") int pageSize,
                                                                          @RequestParam(defaultValue = "id") String sortBy,
                                                                          @RequestParam(defaultValue = "ASC") String sortDirection,
                                                                          @RequestParam(defaultValue = "false") Boolean isDeleted) {

        log.trace("GetUsersWithPaginationAndSorting endpoint is called with parameters PageNumber: {},PageSize: {},SortBy: {},SortDirection: {},isDeleted: {}"
                , pageNumber, pageSize, sortBy, sortDirection,isDeleted);
        return ResponseEntity.ok().body(this.userService.getUsersWithPagination(pageNumber, pageSize,sortBy,sortDirection,isDeleted));

    }











}
