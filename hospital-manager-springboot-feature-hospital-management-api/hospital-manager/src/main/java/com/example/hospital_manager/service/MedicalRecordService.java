package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.*;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.*;
import com.example.hospital_manager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository recordRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public MedicalRecordResponse createRecord(
            MedicalRecordRequest request,
            String username) {

        AppUser doctor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "Doctor not found"));

        if (doctor.getRole() != Role.DOCTOR) {
            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "Only doctors can create medical records");
        }

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "Patient not found"));

        MedicalRecord record = MedicalRecord.builder()
                .patient(patient)
                .doctor(doctor)
                .diagnosis(request.diagnosis())
                .prescription(request.prescription())
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(recordRepository.save(record));
    }

    public List<MedicalRecordResponse> getPatientRecords(Long patientId) {

        if (!patientRepository.existsById(patientId)) {
            throw new ApiException(
                    HttpStatus.NOT_FOUND, "Patient not found");
        }

        return recordRepository.findByPatientId(patientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private MedicalRecordResponse toResponse(MedicalRecord r) {

        return new MedicalRecordResponse(
                r.getId(),
                r.getPatient().getId(),
                r.getPatient().getName(),
                r.getDoctor().getUsername(),
                r.getDiagnosis(),
                r.getPrescription(),
                r.getCreatedAt()
        );
    }
}