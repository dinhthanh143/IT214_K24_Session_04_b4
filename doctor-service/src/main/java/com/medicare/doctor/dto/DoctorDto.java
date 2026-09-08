package com.medicare.doctor.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorDto {
    private Long id;
    private String fullName;
    private String specialization;
    private String phone;
    private String email;
    private Long deptId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
