package ec.edu.espe.buildtestci;

import ec.edu.espe.buildtestci.dto.ReservationResponse;
import ec.edu.espe.buildtestci.model.ReservationStatus;
import ec.edu.espe.buildtestci.model.RoomReservation;
import ec.edu.espe.buildtestci.repository.ReservationRepository;
import ec.edu.espe.buildtestci.service.RoomReservationService;
import ec.edu.espe.buildtestci.service.UserPolicyClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserPolicyClient userPolicyClient;

    @InjectMocks
    private RoomReservationService roomReservationService;

    @Test
    void createReservation_Success() {
        // Arrange
        String roomCode = "Hab-1";
        String email = "cdpincha1@espe.edu";
        int hours = 4;

        when(reservationRepository.existsByRoomCode(roomCode)).thenReturn(false);
        when(userPolicyClient.isBlocked(email)).thenReturn(false);
        when(reservationRepository.save(any(RoomReservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ReservationResponse response = roomReservationService.createReservation(roomCode, email, hours);

        // Assert
        assertNotNull(response);
        assertEquals(roomCode, response.getRoomCode());
        assertEquals(ReservationStatus.CREATED, response.getStatus());
        verify(reservationRepository).save(any(RoomReservation.class));
        verify(userPolicyClient).isBlocked(email);
        verify(reservationRepository).existsByRoomCode(roomCode);
    }


    @Test
    void createReservation_InvalidEmail() {
        // Arrange
        String roomCode = "Hab-2";
        String email = "Estecorreonovale.com";
        int hours = 4;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> roomReservationService.createReservation(roomCode, email, hours)
        );
        assertEquals("El correo no sirve", exception.getMessage());
        verifyNoInteractions(reservationRepository);
        verifyNoInteractions(userPolicyClient);
    }

    @Test
    void createReservation_HoursOutOfRange_Zero() {
        // Arrange
        String roomCode = "Hab-3";
        String email = "cdpincha1@espe.edu";
        int hours = 12;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> roomReservationService.createReservation(roomCode, email, hours)
        );
        assertEquals("Las horas deben ser entre 0 y 8h", exception.getMessage());
        verifyNoInteractions(reservationRepository);
        verifyNoInteractions(userPolicyClient);
    }


    @Test
    void createReservation_RoomAlreadyReserved() {

        // Arrange
        String roomCode = "Hab-1";
        String email = "espe@espe.edu";
        int hours = 4;

        when(reservationRepository.existsByRoomCode(roomCode)).thenReturn(true);

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> roomReservationService.createReservation(roomCode, email, hours)
        );
        assertEquals("La habitacion esta reservada", exception.getMessage());
        verify(reservationRepository).existsByRoomCode(roomCode);
        verify(userPolicyClient, never()).isBlocked(email);
        verify(reservationRepository, never()).save(any());
    }
}
