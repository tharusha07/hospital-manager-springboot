package com.example.hospital_manager.service;

import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository recordRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public List<Patient> searchPatients(String name) {
        return patientRepository.findByNameContainingIgnoreCase(name);
    }

    public Patient getPatient(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "Patient not found"));
    }

    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public Patient updatePatient(Long id, Patient details) {

        Patient patient = getPatient(id);

        patient.setName(details.getName());
        patient.setEmail(details.getEmail());
        patient.setPhone(details.getPhone());
        patient.setAddress(details.getAddress());
        patient.setDateOfBirth(details.getDateOfBirth());

        return patientRepository.save(patient);
    }

    public void deletePatient(Long id) {

        Patient patient = getPatient(id);

        if (appointmentRepository.existsByPatientId(id)
                || recordRepository.existsByPatientId(id)) {

            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Cannot delete patient with appointments or medical records"
            );
        }

        patientRepository.delete(patient);
    }
}