package com.user_service.dtos.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RoleDto {

    @NotNull
    @Pattern(regexp = "ROLE_CASHIER|ROLE_ADMIN|ROLE_MANAGER", message = "Invalid role name. Valid roles are: ROLE_CASHIER, ROLE_ADMIN, ROLE_MANAGER")
    private String roleName;

}
