package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Data;

@Data
public class ApplicationRequest {
  @NotBlank(message = "fullname is required")
  private String fullName;

  @NotBlank(message = "address is required")
  private String address;

  @NotNull(message = "Loan amount is required")
  @Positive(message = "Loan amount must be a positive figure")
  private Double loanAmount;

  private String documentPath;
}
