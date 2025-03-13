package com.priyhotel.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "amenities")
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String amenityName;

    private String amenityIconUrl;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

}
