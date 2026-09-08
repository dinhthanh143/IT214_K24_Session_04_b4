package com.medicare.pharmacy.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineDto {
    private Long id;
    private String name;
    private String category;
    private String unit;
    private BigDecimal unitPrice;
}
