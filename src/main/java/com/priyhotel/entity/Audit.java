package com.priyhotel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.LocalDate;

@Data
@MappedSuperclass
public class Audit {

    @Column(nullable = true)
    private LocalDate createdOn;

    @Column(nullable = true)
    private Long createdBy;

    @Column(nullable = true)
    private LocalDate updatedOn;

    @Column(nullable = true)
    private Long updatedBy;
}
