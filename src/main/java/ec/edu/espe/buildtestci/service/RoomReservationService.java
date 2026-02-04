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
            throw new IllegalArgumentException("El campo debe tener un valor");
        }

        // Email con formato valido
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("El correo no sirve");
        }

        // regla de las horas entre 0 y 8 h
        if (hours <= 0 || hours > 8) {
            throw new IllegalArgumentException("Las horas deben ser entre 0 y 8h");
        }

        // reservas no validas si estan ocupados
        if (reservationRepository.existsByRoomCode(roomCode)) {
            throw new IllegalStateException("La habitacion esta reservada");
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
