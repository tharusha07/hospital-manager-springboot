package com.example.hospital_manager.controller;

import com.example.hospital_manager.dto.*;
import com.example.hospital_manager.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public MedicalRecordResponse create(
            @Valid @RequestBody MedicalRecordRequest request,
            Authentication authentication) {

        return medicalRecordService.createRecord(
                request, authentication.getName());
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE')")
    public List<MedicalRecordResponse> getPatientRecords(
            @PathVariable Long patientId) {

        return medicalRecordService.getPatientRecords(patientId);
    }
}