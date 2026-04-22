package com.sergiplca.api_gateway.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "DTO to request an access token")
public class TokenRequestDto {

    @NotNull(message = "username must be present")
    @Schema(example = "Armin van Buuren", description = "Username")
    private String username;

    @NotNull(message = "roles must be present")
    @NotEmpty(message = "At least one role must be requested")
    @Schema(example = "[\"USER\"]", description = "Roles")
    private List<String> roles;
}
