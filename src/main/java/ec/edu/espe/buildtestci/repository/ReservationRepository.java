package ec.edu.espe.buildtestci.repository;

import ec.edu.espe.buildtestci.model.RoomReservation;

import java.util.Optional;

public interface ReservationRepository {
    RoomReservation save(RoomReservation reservation);
    Optional<RoomReservation> findById(String id);
    boolean existsByRoomCode(String roomCode);
}
