package com.user_service.dtos.request;

import com.user_service.dtos.response.RoleDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmployeeDto {

    @NotBlank(message = "Name cannot be Null or empty.")
    @Size(min = 2, max = 20)
    private String name;

    @NotBlank
    @Size(min = 2, max =20)
    private String surname;

    @NotNull(message = "Username Cannot Be Null")
    @Size(min = 3)
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Username cannot contain spaces or special characters.")
    private String username;

    @NotBlank(message = "Password cannot be Empty or Null")
    @Pattern(regexp = "^\\S+$", message = "Password cannot contain whitespace characters.")
    private String password;

    @NotNull(message = "E-mail Cannot Be Null")
    @Email(message = "Enter a valid E-mail")
    private String email;

    @NotEmpty(message = "Every user must have at least one role")
    private Set<@Valid RoleDto> roles;
}
