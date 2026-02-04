package ec.edu.espe.buildtestci.service;

import ec.edu.espe.buildtestci.dto.ReservationResponse;
import ec.edu.espe.buildtestci.model.ReservationStatus;
import ec.edu.espe.buildtestci.model.RoomReservation;
import ec.edu.espe.buildtestci.repository.ReservationRepository;

public class RoomReservationService {
    private final ReservationRepository reservationRepository;
    private final UserPolicyClient userPolicyClient;

    public RoomReservationService(ReservationRepository reservationRepository, UserPolicyClient userPolicyClient) {
        this.reservationRepository = reservationRepository;
        this.userPolicyClient = userPolicyClient;
    }

    public ReservationResponse createReservation(String roomCode, String email, int hours) {
        // roomCode no debe ser nulo
        if (roomCode == null || roomCode.isEmpty()) {
            throw new IllegalArgumentException("codigo no debe ser nulo");
        }

        // Email con formato valido
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Email invalido");
        }

        // regla de las horas entre 0 yu 8 h
        if (hours <= 0 || hours > 8) {
            throw new IllegalArgumentException("Horas mayores a 0 y menores o iguales  a8");
        }

        // reservas no validas si estan ocupados
        if (reservationRepository.existsByRoomCode(roomCode)) {
            throw new IllegalStateException("Cuarto reservado");
        }

        //usuarios bloqueados no pueden crear reservas
        if (userPolicyClient.isBlocked(email)) {
            throw new IllegalStateException("Usuario bloqueado por politicas internas");
        }

        RoomReservation reservation = new RoomReservation(roomCode, email, hours, ReservationStatus.CREATED);
        RoomReservation saved = reservationRepository.save(reservation);

        return new ReservationResponse(saved.getId(), saved.getRoomCode(), saved.getStatus());
    }
}
