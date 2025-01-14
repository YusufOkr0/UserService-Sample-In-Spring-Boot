package com.user_service.rules;

import com.user_service.entities.Employee;
import com.user_service.entities.Role;

import com.user_service.exceptions.*;
import com.user_service.repository.RoleRepository;
import com.user_service.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@AllArgsConstructor
public class UserServiceRules { 
                                
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name", "surname", "username", "email");
    private static final List<String> VALID_ROLES = Arrays.asList("ROLE_CASHIER", "ROLE_ADMIN", "ROLE_MANAGER");



    public void validateUsernameAndEmailUniqueness(String username, String email) {
        log.trace("Username and email checking method started with parameters Username : {} and Email : {}", username, email);

        boolean usernameExists = userRepository.existsByUsername(username);
        boolean emailExists = userRepository.existsByEmail(email);
        if(usernameExists && emailExists) {
            log.warn("Registration process blocked. username: {} and email: {} already taken", username, email);
            throw new UserAlreadyExistsException("Username and email already taken.");
        }
        else if (usernameExists) {
            log.warn("Registration process blocked. username: {} already taken", username);
            throw new UserAlreadyExistsException("User is already taken with username: " + username);
        }
        else if (emailExists) {
            log.warn("Registration process blocked. email: {} already taken", email);
            throw new UserAlreadyExistsException("User is already taken with email: " + email);
        }

        log.trace("Username:{} and Email: {} are valid. CheckIfUsernameAndEmailExists method end.",username,email);
    }

    public Role checkIfRoleExistsAndReturn(String roleName) {
        log.trace("Checking if role exists and return role : {}", roleName);
        Optional<Role> validRole = this.roleRepository.findByRoleName(roleName);
        if(validRole.isPresent()) {
            log.debug("Role exists and return role : {}", roleName);
            return validRole.get();
        }else{
            log.warn("Role not found with role name : {}", roleName);
            throw new RoleNotFoundException(roleName);
        }
    }


    public Employee checkIfUserExistsByIdAndReturn(Long id){
        log.trace("checkIfUserExistsByIdAndReturn method started with parameters id : {}", id);

        Optional<Employee> employee = this.userRepository.findById(id);
        if(employee.isEmpty()){
            log.warn("User not found with id : {}", id);
            throw new UserNotFoundException("User with ID: " + id + " cannot be found.");
        }
        if(employee.get().isDeleted()){
            log.warn("User has been deleted with id : {}", id);
            throw new UserAlreadyDeletedException("User with ID: " + id + " has been deleted.");
        }
        log.info("User is active and not deleted, proceeding to return user.");
        log.trace("checkIfUserExistsByIdAndReturn method completed successfully for id: {}", id);
        return employee.get();

    }

    public void checkIfPaginationInputsIsValid(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        log.trace("Entering checkIfPaginationInputsIsValid method. Parameters: pageNumber={}, pageSize={}, sortBy={}, sortDirection={}", pageNumber, pageSize, sortBy, sortDirection);

        if (pageNumber < 1) {
            log.warn("Invalid pageNumber: {}. It must be >= 1.", pageNumber);
            throw new PaginationArgumentNotValidException("Page number must be greater than or equal to 1.");
        }

        if (pageSize < 1) {
            log.warn("Invalid pageSize: {}. It must be >= 1.", pageSize);
            throw new PaginationArgumentNotValidException("Page size must be greater than or equal to 1.");
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            log.warn("Invalid sortBy: '{}'. Allowed sort fields are: {}", sortBy, ALLOWED_SORT_FIELDS);
            throw new PaginationArgumentNotValidException("Invalid sort field: " + sortBy + ". Allowed fields are: " + ALLOWED_SORT_FIELDS);
        }

        if (!sortDirection.equalsIgnoreCase("ASC") && !sortDirection.equalsIgnoreCase("DESC")) {
            log.warn("Invalid sortDirection: '{}'. It must be either 'ASC' or 'DESC'.", sortDirection);
            throw new PaginationArgumentNotValidException("Sort direction must be 'ASC' or 'DESC'.");
        }

        log.trace("Exiting checkIfPaginationInputsIsValid method. All parameters are valid.");
    }




}
