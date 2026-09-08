package com.medicare.medicalrecord.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordDto {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long appointmentId;
    private String diagnosis;
    private String treatmentPlan;
    private LocalDateTime visitDate;
    private LocalDateTime createdAt;
}
