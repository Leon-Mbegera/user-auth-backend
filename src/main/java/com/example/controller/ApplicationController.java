package com.example.controller;

import com.example.dto.ApplicationRequest;
import com.example.dto.UpdateStatusRequest;
import com.example.model.Application;
import com.example.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ApplicationController {
    @Autowired
    private ApplicationService applicationService;

    @PostMapping("/api/applications")
    public ResponseEntity<Application> submitApplication(@Valid @RequestBody ApplicationRequest request, Authentication authentication) {
        Application application = applicationService.submitApplication(
            authentication.getName(),
            request.getFullName(),
            request.getAddress(),
            request.getLoanAmount(),
            request.getDocumentPath()
        );
        return ResponseEntity.ok(application);
    }

    @GetMapping("/api/applications")
    public ResponseEntity<List<Application>> getUserApplications(Authentication authentication) {
        List<Application> applications = applicationService.getUserApplications(authentication.getName());
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/api/admin/applications")
    public ResponseEntity<List<Application>> getAllApplications(Authentication authentication) {
        List<Application> applications = applicationService.getAllApplications(authentication.getName());
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/api/admin/applications/{id}")
    public ResponseEntity<Application> updateApplicationStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request, Authentication authentication) {
        Application application = applicationService.updateApplicationStatus(id, request.getStatus(), authentication.getName());
        return ResponseEntity.ok(application);
    }
}