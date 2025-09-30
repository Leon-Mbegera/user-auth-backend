package com.example.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UpdateStatusRequest {
  @NotBlank(message = "status is required")
  private String status;
}
