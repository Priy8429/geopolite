package com.priyhotel.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "room_types")
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String typeName;

    private Integer capacityAdult;

    @Column(nullable = true)
    private Integer capacityChild;

    @Column(nullable = true)
    private Double pricePerNight;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private Integer roomSizeInSquareFeet;

    @ManyToMany
    @JoinTable(
            name = "room_type_amenities",
            joinColumns = @JoinColumn(name = "room_type_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private List<Amenity> amenities;

//    @JsonIgnore
//    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//    private List<Room> rooms;

    @ManyToMany
    @JoinTable(
            name = "room_type_assets",
            joinColumns = @JoinColumn(name = "room_type_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id")
    )
    private List<Asset> assets;

}
