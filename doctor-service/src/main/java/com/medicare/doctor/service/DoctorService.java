package com.medicare.doctor.service;

import com.medicare.doctor.dto.DoctorDto;
import com.medicare.doctor.model.Doctor;
import com.medicare.doctor.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorDto createDoctor(DoctorDto dto) {
        Doctor doctor = toEntity(dto);
        Doctor saved = doctorRepository.save(doctor);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<DoctorDto> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DoctorDto getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + id));
        return toDto(doctor);
    }

    public DoctorDto updateDoctor(Long id, DoctorDto dto) {
        Doctor existing = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + id));

        existing.setFullName(dto.getFullName());
        existing.setSpecialization(dto.getSpecialization());
        existing.setPhone(dto.getPhone());
        existing.setEmail(dto.getEmail());
        existing.setDeptId(dto.getDeptId());

        Doctor updated = doctorRepository.save(existing);
        return toDto(updated);
    }

    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new RuntimeException("Doctor not found with ID: " + id);
        }
        doctorRepository.deleteById(id);
    }

    private DoctorDto toDto(Doctor entity) {
        return DoctorDto.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .specialization(entity.getSpecialization())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .deptId(entity.getDeptId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private Doctor toEntity(DoctorDto dto) {
        return Doctor.builder()
                .fullName(dto.getFullName())
                .specialization(dto.getSpecialization())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .deptId(dto.getDeptId())
                .build();
    }
}
