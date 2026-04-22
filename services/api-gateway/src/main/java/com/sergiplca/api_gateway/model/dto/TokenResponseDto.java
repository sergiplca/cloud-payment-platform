package com.sergiplca.api_gateway.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response DTO with the requested access token")
public class TokenResponseDto {

    @Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTZXJnaSIsInJvbGVzIjpbIlVTRVIiXSwiaWF0IjoxNzc2NzgwODMwLCJleHAi" +
        "OjE3NzY3ODA5NTB9.Y9bBctkVmHGH93XrkEp9LheODkexc6QH51zq2twtSHw", description = "Bearer token")
    private String accessToken;

    @Schema(example = "Bearer", description = "Token type, always Bearer")
    private String tokenType;

    @Schema(example = "120000", description = "Expiration in milliseconds")
    private Long expiresIn;
}
