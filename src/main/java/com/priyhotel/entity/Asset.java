package com.priyhotel.entity;


import com.priyhotel.constants.AssetType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String assetUrl;

    private String assetThumbUrl;

    private String assetType;
}
