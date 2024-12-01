package com.user_service.service.concretes;

import com.user_service.config.ModelMapperManager;
import com.user_service.dtos.request.EmployeeAddDto;
import com.user_service.dtos.request.UpdateEmployeeDto;
import com.user_service.dtos.response.GetEmployeeDto;
import com.user_service.entities.Employee;
import com.user_service.entities.Role;
import com.user_service.exceptions.UserAlreadyExistsException;
import com.user_service.exceptions.UserNotFoundException;
import com.user_service.repository.UserRepository;
import com.user_service.rules.UserServiceRules;
import com.user_service.service.abstracts.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceManager implements UserService {

    private final UserRepository userRepository;
    private final UserServiceRules userServiceRules;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapperManager modelMapperManager;

    @Transactional
    public String addUser(List<EmployeeAddDto> listOfEmployeeDto) {
        log.trace("addUser function is called to add {} number of users", listOfEmployeeDto.size());

        listOfEmployeeDto.forEach(employeeAddRequest -> {

            log.info("Validating the existence of username '{}' and email '{}'.", employeeAddRequest.getUsername(), employeeAddRequest.getEmail());
            this.userServiceRules.validateUsernameAndEmailUniqueness(employeeAddRequest.getUsername(), employeeAddRequest.getEmail());

            log.info("Validating existence of {} roles provided in the request.", employeeAddRequest.getRoles().size());
            Set<Role> roles = employeeAddRequest.getRoles().stream()
                    .map(roleDto -> this.userServiceRules.checkIfRoleExistsAndReturn(roleDto.getRoleName()))
                    .collect(Collectors.toSet());

            Employee employee = Employee.builder()
                    .name(employeeAddRequest.getName())
                    .surname(employeeAddRequest.getSurname())
                    .username(employeeAddRequest.getUsername())
                    .password(passwordEncoder.encode(employeeAddRequest.getPassword()))
                    .roles(roles)
                    .email(employeeAddRequest.getEmail())
                    .isDeleted(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            log.info("User is registered with the id {} and username {}", employeeAddRequest.getUsername(), employeeAddRequest.getEmail());
            this.userRepository.save(employee);
        });

        log.trace("{} Number of user has successfully added.", listOfEmployeeDto.size());
        return listOfEmployeeDto.size() + "Number of user are saved successfully.";
    }





    public String deleteUserById(Long id) {
        log.trace("deleteUserById function is called to delete user with id: {}", id);
        Employee employee = this.userServiceRules.checkIfUserExistsByIdAndReturn(id);

        employee.setDeleted(true);

        log.info("User is deleted with the id {}", id);
        this.userRepository.save(employee);

        log.trace("deleteUserById function completed successfully for user with id: {}", id);
        return "User with" + id + "has been deleted successfully.";
    }




    public String reActiveUserById(Long id){
        log.trace("reActiveUserById method is called to reactive user with id: {}", id);

        log.info("Checking if user with id {} exists.", id);
        Employee employee = this.userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User with id {} is not found.", id);
                    return new UserNotFoundException("User with id " + id + " is not found.");
                });

        log.info("Checking if user with id {} is active.", id);
        if (!employee.isDeleted()) {
            log.warn("User with id {} is active.", id);
            throw new UserAlreadyExistsException("User is already active with id: " + id);
        }

        employee.setDeleted(false);
        log.info("User has been activated with the id {}", id);
        this.userRepository.save(employee);

        log.trace("reActiveUserById function completed successfully for user with id: {}", id);
        return "User with" + id + "has been reactivated successfully.";
    }





    @Transactional
    public String updateEmployeeById(Long id, UpdateEmployeeDto updateRequest){
        log.trace("updateEmployeeById function is called to update employee with id: {}", id);

        log.info("Checking process if user with id {} exists.", id);
        Employee repoEmployee = this.userServiceRules.checkIfUserExistsByIdAndReturn(id);

        log.info("Validating if the username '{}' and email '{}' are unique.", updateRequest.getUsername(), updateRequest.getEmail());
        this.userServiceRules.validateUsernameAndEmailUniqueness(updateRequest.getUsername(), updateRequest.getEmail());

        if(!updateRequest.getPassword().isEmpty()){
            log.info("Password is encoding");
            updateRequest.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

        log.info("User credentials update operation is started");
        this.userRepository.updateEmployeeById(
                id,
                updateRequest.getName(),
                updateRequest.getSurname(),
                updateRequest.getUsername(),
                updateRequest.getPassword(),
                updateRequest.getEmail(),
                LocalDateTime.now()
        );

       if(!updateRequest.getRoles().isEmpty()) {
           log.info("Updating roles for user with id: {}", id);
           Set<Role> roles = updateRequest.getRoles().stream()
                   .map(roleDto -> {
                       Role role = this.modelMapperManager.ForRequest().map(roleDto, Role.class);
                       log.info("Checking if role with role name: {} exists.", role.getRoleName());
                       return this.userServiceRules.checkIfRoleExistsAndReturn(role.getRoleName());
                   })
                   .collect(Collectors.toSet());

           repoEmployee.setRoles(roles);

           log.info("User roles update operation is finished");
           this.userRepository.save(repoEmployee);
       }

        log.trace("updateEmployeeById function completed successfully for user with id: {}", id);
        return "User with "+id+" is updated successfully.";
    }

    public Page<GetEmployeeDto> getUsersWithPagination(int pageNumber, int pageSize, String sortBy, String sortDirection, Boolean isDeleted) {
        log.trace("Entering getUsersWithPagination method. Parameters: pageNumber={}, pageSize={}, sortBy={}, sortDirection={}, isDeleted={}", pageNumber, pageSize, sortBy, sortDirection, isDeleted);

        log.info("Validating pagination parameters...");
        this.userServiceRules.checkIfPaginationInputsIsValid(pageNumber, pageSize, sortBy, sortDirection);

        log.debug("Pagination inputs validated successfully. Preparing Pageable object for pagination...");
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(direction, sortBy));

        log.debug("Fetching users from database. Sorting by '{}' in '{}' order and filtering by 'isDeleted={}'", sortBy, sortDirection, isDeleted);
        Page<Employee> repoEmployees = this.userRepository.findAllByisDeleted(isDeleted, pageable);

        log.trace("Exiting getUsersWithPagination method. Returning {} users.", repoEmployees.getTotalElements());
        return repoEmployees.map(employee -> this.modelMapperManager.ForResponse().map(employee, GetEmployeeDto.class));
    }

    @Override
    public GetEmployeeDto getEmployeeById(Long id) {
        log.trace("Entering getEmployeeById method with id: {}.",id);
        log.info("Checking if user with id {} exists.", id);

        Employee existsEmployee = this.userServiceRules.checkIfUserExistsByIdAndReturn(id);

        log.debug("The employee object is being converted to a GetEmployeeDto object.");
        GetEmployeeDto employeeResponse = this.modelMapperManager.ForResponse().map(existsEmployee, GetEmployeeDto.class);

        log.trace("Exiting getEmployeeById method. Returning exists employee with id: {} ", id);
        return employeeResponse;
    }

















    // PAGINATION FONKSIYONU BURASININ GÖREVINI TEK BASINA YAPIYOR ZATEN

    public List<GetEmployeeDto> getAllUsers() {
        List<Employee> repoEmployees = this.userRepository.findAll();
        return repoEmployees.stream()
                .map(employee -> this.modelMapperManager.ForResponse().map(employee, GetEmployeeDto.class))
                .toList();
    }



    public List<GetEmployeeDto> getAllActiveUsers() {

        List<Employee> repoEmployees = this.userRepository.findAll().stream()
                .filter(employee -> !employee.isDeleted())
                .toList();
        return repoEmployees.stream()
                .map(repoEmployee -> this.modelMapperManager.ForResponse().map(repoEmployee, GetEmployeeDto.class))
                .toList();

    }




}
