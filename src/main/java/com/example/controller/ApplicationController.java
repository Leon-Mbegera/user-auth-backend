package com.example.controller;

import com.example.model.Application;
import com.example.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ApplicationController {
  @Autowired
  private ApplicationService applicationService;

  @PostMapping("/api/applications")
  public ResponseEntity<Application> submitApplication(@RequestBody Map<String, String> body,
      Authentication authentication) {

    Application application = applicationService.submitApplication(authentication.getName(), body.get("fullName"),
        body.get("address"), Double.parseDouble(body.get("loanAmount")), body.get("documentPath"));
    return ResponseEntity.ok(application);
  }

  @GetMapping("/api/applications")
  public ResponseEntity<List<Application>> getUserApplications(Authentication authentication) {

    List<Application> applications = applicationService.getUserApplications(authentication.getName());
    return ResponseEntity.ok(applications);
  }



}
