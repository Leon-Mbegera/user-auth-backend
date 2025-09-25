package com.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Data
public class Application {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private String fullName;

  @Column(nullable = false)
  private String address;

  @Column(nullable = false)
  private Double loanAmount;

  @Column
  private String documentPath;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ApplicationStatus status;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, updatable = true)
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreated() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
    status = ApplicationStatus.PENDING;
  }

  @PreUpdate
  protected void onUpdated() {
    updatedAt = LocalDateTime.now();
  }
}
