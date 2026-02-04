package ec.edu.espe.buildtestci.dto;

import ec.edu.espe.buildtestci.model.ReservationStatus;

public class ReservationResponse {
    private final String id;
    private final String roomCode;
    private final ReservationStatus status;

    public ReservationResponse(String id, String roomCode, ReservationStatus status) {
        this.id = id;
        this.roomCode = roomCode;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public ReservationStatus getStatus() {
        return status;
    }
}
