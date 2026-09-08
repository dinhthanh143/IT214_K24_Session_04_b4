package com.medicare.pharmacy.service;

import com.medicare.pharmacy.dto.MedicineDto;
import com.medicare.pharmacy.model.Medicine;
import com.medicare.pharmacy.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PharmacyService {

    private final MedicineRepository medicineRepository;

    public MedicineDto createMedicine(MedicineDto dto) {
        Medicine medicine = toEntity(dto);
        Medicine saved = medicineRepository.save(medicine);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<MedicineDto> getAllMedicines() {
        return medicineRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MedicineDto getMedicineById(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + id));
        return toDto(medicine);
    }

    public MedicineDto updateMedicine(Long id, MedicineDto dto) {
        Medicine existing = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + id));

        existing.setName(dto.getName());
        existing.setCategory(dto.getCategory());
        existing.setUnit(dto.getUnit());
        existing.setUnitPrice(dto.getUnitPrice());

        Medicine updated = medicineRepository.save(existing);
        return toDto(updated);
    }

    public void deleteMedicine(Long id) {
        if (!medicineRepository.existsById(id)) {
            throw new RuntimeException("Medicine not found with ID: " + id);
        }
        medicineRepository.deleteById(id);
    }

    private MedicineDto toDto(Medicine entity) {
        return MedicineDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .category(entity.getCategory())
                .unit(entity.getUnit())
                .unitPrice(entity.getUnitPrice())
                .build();
    }

    private Medicine toEntity(MedicineDto dto) {
        return Medicine.builder()
                .name(dto.getName())
                .category(dto.getCategory())
                .unit(dto.getUnit())
                .unitPrice(dto.getUnitPrice())
                .build();
    }
}
