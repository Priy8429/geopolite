package com.priyhotel.entity;

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

    private int capacityAdult;

    private int capacityChild;

    private double pricePerNight;

    private String description;

    private int roomSizeInSquareFeet;

    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Amenity> amenities;

    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Room> rooms;

    @JsonIgnore
    @ManyToMany(mappedBy = "roomTypes") // Reference to 'roomTypes' in Hotel entity
    private List<Hotel> hotels;

    @ManyToMany
    @JoinTable(
            name = "room_type_assets",
            joinColumns = @JoinColumn(name = "room_type_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id")
    )
    private List<Asset> assets;

}
