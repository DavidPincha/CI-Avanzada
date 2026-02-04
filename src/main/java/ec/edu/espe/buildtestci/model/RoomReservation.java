package ec.edu.espe.buildtestci.model;

import java.util.UUID;

public class RoomReservation {
    private final String id;
    private final String roomCode;
    private final String reservedEmail;
    private final int hours;
    private final ReservationStatus status;

    public RoomReservation(String roomCode, String reservedEmail, int hours, ReservationStatus status) {
        this.id = UUID.randomUUID().toString();
        this.roomCode = roomCode;
        this.reservedEmail = reservedEmail;
        this.hours = hours;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public String getReservedByEmail() {
        return reservedEmail;
    }

    public int getHours() {
        return hours;
    }

    public ReservationStatus getStatus() {
        return status;
    }
}
