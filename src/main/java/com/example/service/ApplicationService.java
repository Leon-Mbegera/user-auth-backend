package com.example.service;

import com.example.model.Application;
import com.example.model.ApplicationStatus;
import com.example.model.User;
import com.example.repository.ApplicationRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {
  @Autowired
  private ApplicationRepository applicationRepository;

  @Autowired
  private UserRepository userRepository;

  public Application submitApplication(String username, String fullName, String address, Double loanAmount,
      String documentPath) {
    User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found."));
    Application application = new Application();

    application.setUser(user);
    application.setFullName(fullName);
    application.setAddress(address);
    application.setLoanAmount(loanAmount);
    application.setDocumentPath(documentPath);

    return applicationRepository.save(application);
  }

  public List<Application> getUserApplications(String username) {
    User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found."));

    return applicationRepository.findByUserId(user.getId());
  }

  public List<Application> getAllApplications(String username) {
    User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found."));

    if (!user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
      throw new AccessDeniedException("Admin rights required.");
    }

    return applicationRepository.findAll();
  }

  public Application updateApplicationStatus(Long id, String status, String username) {
    User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found."));

    if (!user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
      throw new AccessDeniedException("Admin rights required.");
    }
    Application application = applicationRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Application not found."));
    application.setStatus(ApplicationStatus.valueOf(status));

    return applicationRepository.save(application);
  }
}
