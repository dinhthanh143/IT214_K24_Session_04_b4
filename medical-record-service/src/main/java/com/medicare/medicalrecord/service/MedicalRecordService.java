package com.medicare.medicalrecord.service;

import com.medicare.medicalrecord.dto.MedicalRecordDto;
import com.medicare.medicalrecord.model.MedicalRecord;
import com.medicare.medicalrecord.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    public MedicalRecordDto createMedicalRecord(MedicalRecordDto dto) {
        MedicalRecord record = toEntity(dto);
        if (record.getVisitDate() == null) {
            record.setVisitDate(LocalDateTime.now());
        }
        MedicalRecord saved = medicalRecordRepository.save(record);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<MedicalRecordDto> getAllMedicalRecords() {
        return medicalRecordRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MedicalRecordDto getMedicalRecordById(Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical record not found with ID: " + id));
        return toDto(record);
    }

    public MedicalRecordDto updateMedicalRecord(Long id, MedicalRecordDto dto) {
        MedicalRecord existing = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical record not found with ID: " + id));

        existing.setPatientId(dto.getPatientId());
        existing.setDoctorId(dto.getDoctorId());
        existing.setAppointmentId(dto.getAppointmentId());
        existing.setDiagnosis(dto.getDiagnosis());
        existing.setTreatmentPlan(dto.getTreatmentPlan());
        if (dto.getVisitDate() != null) {
            existing.setVisitDate(dto.getVisitDate());
        }

        MedicalRecord updated = medicalRecordRepository.save(existing);
        return toDto(updated);
    }

    public void deleteMedicalRecord(Long id) {
        if (!medicalRecordRepository.existsById(id)) {
            throw new RuntimeException("Medical record not found with ID: " + id);
        }
        medicalRecordRepository.deleteById(id);
    }

    private MedicalRecordDto toDto(MedicalRecord entity) {
        return MedicalRecordDto.builder()
                .id(entity.getId())
                .patientId(entity.getPatientId())
                .doctorId(entity.getDoctorId())
                .appointmentId(entity.getAppointmentId())
                .diagnosis(entity.getDiagnosis())
                .treatmentPlan(entity.getTreatmentPlan())
                .visitDate(entity.getVisitDate())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private MedicalRecord toEntity(MedicalRecordDto dto) {
        return MedicalRecord.builder()
                .patientId(dto.getPatientId())
                .doctorId(dto.getDoctorId())
                .appointmentId(dto.getAppointmentId())
                .diagnosis(dto.getDiagnosis())
                .treatmentPlan(dto.getTreatmentPlan())
                .visitDate(dto.getVisitDate())
                .build();
    }
}
