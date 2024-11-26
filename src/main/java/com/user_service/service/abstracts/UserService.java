package com.user_service.service.abstracts;

import com.user_service.dtos.request.EmployeeAddDto;
import com.user_service.dtos.request.UpdateEmployeeDto;
import com.user_service.dtos.response.GetAllEmployeeDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

     String addUser(List<EmployeeAddDto> listOfEmployeeDto);

     List<GetAllEmployeeDto> getAllUsers();

     List<GetAllEmployeeDto> getAllActiveUsers();

     String deleteUserById(Long id);

     String updateEmployeeById(Long id, UpdateEmployeeDto updateRequest);

     String reActiveUserById(Long id);

     Page<GetAllEmployeeDto> getUsersWithPagination(int pageNumber, int pageSize,String sortBy,String sortDirection,Boolean isDeleted);

}
