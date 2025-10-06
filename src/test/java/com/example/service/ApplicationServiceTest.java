package com.example.service;

import com.example.model.Application;
import com.example.model.ApplicationStatus;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.ApplicationRepository;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private User user;
    private User admin;
    private Application application;

    @BeforeEach
    void setUp() {
        // Create user with ROLE_USER
        user = new User();
        user.setId(1L);
        user.setUsername("testuser1");

        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_USER");
        user.setRoles(Set.of(userRole));

        // Create admin with ROLE_ADMIN
        admin = new User();
        admin.setId(2L);
        admin.setUsername("testadmin1");

        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName("ROLE_ADMIN");
        admin.setRoles(Set.of(adminRole));

        // Create test application
        application = new Application();
        application.setId(1L);
        application.setUser(user);
        application.setFullName("John Doe");
        application.setAddress("123 Main St");
        application.setLoanAmount(100000.0);
        application.setStatus(ApplicationStatus.PENDING);
    }

    @Test
    void testSubmitApplicationSuccess() {
        when(userRepository.findByUsername("testuser1")).thenReturn(Optional.of(user));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);

        Application result = applicationService.submitApplication("testuser1", "John Doe", "123 Main St", 100000.0, null);

        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        assertEquals("123 Main St", result.getAddress());
        assertEquals(100000.0, result.getLoanAmount());
        verify(applicationRepository, times(1)).save(any(Application.class));
    }

    @Test
    void testSubmitApplicationUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            applicationService.submitApplication("unknown", "John Doe", "123 Main St", 100000.0, null)
        );
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetUserApplicationsSuccess() {
        when(userRepository.findByUsername("testuser1")).thenReturn(Optional.of(user));
        when(applicationRepository.findByUserId(1L)).thenReturn(List.of(application));

        List<Application> result = applicationService.getUserApplications("testuser1");

        assertEquals(1, result.size());
        assertEquals(application, result.get(0));
        verify(applicationRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testGetAllApplicationsAdminSuccess() {
        when(userRepository.findByUsername("testadmin1")).thenReturn(Optional.of(admin));
        when(applicationRepository.findAll()).thenReturn(List.of(application));

        List<Application> result = applicationService.getAllApplications("testadmin1");

        assertEquals(1, result.size());
        assertEquals(application, result.get(0));
        verify(applicationRepository, times(1)).findAll();
    }

    @Test
    void testGetAllApplicationsNonAdmin() {
        when(userRepository.findByUsername("testuser1")).thenReturn(Optional.of(user));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
            applicationService.getAllApplications("testuser1")
        );
        assertEquals("Admin rights required", exception.getMessage());
    }

    @Test
    void testUpdateApplicationStatusSuccess() {
        when(userRepository.findByUsername("testadmin1")).thenReturn(Optional.of(admin));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);

        Application result = applicationService.updateApplicationStatus(1L, "APPROVED", "testadmin1");

        assertEquals(ApplicationStatus.APPROVED, result.getStatus());
        verify(applicationRepository, times(1)).save(application);
    }

    @Test
    void testUpdateApplicationStatusNonAdmin() {
        when(userRepository.findByUsername("testuser1")).thenReturn(Optional.of(user));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
            applicationService.updateApplicationStatus(1L, "APPROVED", "testuser1")
        );
        assertEquals("Admin rights required", exception.getMessage());
    }

    @Test
    void testUpdateApplicationStatusNotFound() {
        when(userRepository.findByUsername("testadmin1")).thenReturn(Optional.of(admin));
        when(applicationRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            applicationService.updateApplicationStatus(1L, "APPROVED", "testadmin1")
        );
        assertEquals("Application not found", exception.getMessage());
    }
}