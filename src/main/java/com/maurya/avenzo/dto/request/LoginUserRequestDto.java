package com.maurya.avenzo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserRequestDto {

    @Schema(description = "User email address", defaultValue = "saket@example.in")
    @Email(message = "Email must be valid")
    private String email;

    /*@Schema(description = "User password", example = "Password@123")*/

    @Schema(description = "User password", defaultValue = "Password@123")
    @NotBlank(message = "Password must not be empty")
    private String password;
}
