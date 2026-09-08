package com.medicare.patient.dto;

import com.medicare.patient.model.Patient.Gender;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDto {
    private Long id;
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String phone;
    private String address;
    private String insuranceId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
