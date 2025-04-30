package com.priyhotel.repository;

import com.priyhotel.entity.RoomBookingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomBookingRequestRepository extends JpaRepository<RoomBookingRequest, Long> {

    List<RoomBookingRequest> findByBookingId(Long bookingId);
}
