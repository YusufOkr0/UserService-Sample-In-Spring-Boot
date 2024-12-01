package com.user_service.service.abstracts;

import com.user_service.dtos.request.EmployeeAddDto;
import com.user_service.dtos.request.UpdateEmployeeDto;
import com.user_service.dtos.response.GetEmployeeDto;
import com.user_service.entities.Employee;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

     String addUser(List<EmployeeAddDto> listOfEmployeeDto);

     List<GetEmployeeDto> getAllUsers();

     List<GetEmployeeDto> getAllActiveUsers();

     String deleteUserById(Long id);

     String updateEmployeeById(Long id, UpdateEmployeeDto updateRequest);

     String reActiveUserById(Long id);

     Page<GetEmployeeDto> getUsersWithPagination(int pageNumber, int pageSize, String sortBy, String sortDirection, Boolean isDeleted);

     GetEmployeeDto getEmployeeById(Long id);
}
