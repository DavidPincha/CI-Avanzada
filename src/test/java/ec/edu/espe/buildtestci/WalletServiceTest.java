package ec.edu.espe.buildtestci;

import ec.edu.espe.buildtestci.dto.WalletResponse;
import ec.edu.espe.buildtestci.model.Wallet;
import ec.edu.espe.buildtestci.repository.WalletRepository;
import ec.edu.espe.buildtestci.service.RiskClient;
import ec.edu.espe.buildtestci.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WalletServiceTest {

    private WalletRepository walletRepository;
    private RiskClient riskClient;
    private WalletService walletService;

    @BeforeEach
    public void setUp() {
        walletRepository = Mockito.mock(WalletRepository.class);
        riskClient = Mockito.mock(RiskClient.class);
        walletService = new WalletService(walletRepository, riskClient);
    }

    @Test
    void createWallet_validData_shouldSaveAndReturnResponse() {
        // Arrange
        String email = "cdpincha1@espe.edu.ec";
        double initial = 100.0;

        // Mocking behavior
        when(riskClient.isBlocked(email)).thenReturn(false);
        when(walletRepository.existsByOwnerEmail(email)).thenReturn(false);

        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> {
            Wallet w = i.getArgument(0);
            return new Wallet(w.getOwnerEmail(), w.getBalance());
        });

        // Act
        WalletResponse response = walletService.createWallet(email, initial);

        // Assert
        assertEquals(100.0, response.getBalance());

        // Verify
        verify(riskClient).isBlocked(email);
        verify(walletRepository).existsByOwnerEmail(email);
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void createWallet_invalidEmail_shouldThrow_andNotCallDependencies() {
        // Arrange
        String invalidEmail = "cristian-pincha-sin-arroba";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                walletService.createWallet(invalidEmail, 50.0)
        );

        // Verify
        verifyNoInteractions(walletRepository, riskClient);
    }

    @Test
    void deposit_validAmount_shouldUpdateBalance() {
        // Arrange
        String walletId = "W-123";
        double initialBalance = 100.0;
        double depositAmount = 50.0;
        Wallet wallet = new Wallet("test@espe.edu.ec", initialBalance);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        // Act
        double newBalance = walletService.deposit(walletId, depositAmount);

        // Assert
        assertEquals(150.0, newBalance);
        assertEquals(150.0, wallet.getBalance());
        verify(walletRepository).save(wallet);
    }

    @Test
    void deposit_negativeAmount_shouldThrow() {
        // Arrange
        String walletId = "W-123";
        double depositAmount = -10.0;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                walletService.deposit(walletId, depositAmount)
        );

        verify(walletRepository, never()).findById(anyString());
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void deposit_walletNotFound_shouldThrow() {
        // Arrange
        String walletId = "W-999";
        double depositAmount = 50.0;

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                walletService.deposit(walletId, depositAmount)
        );

        verify(walletRepository).findById(walletId);
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void withdraw_insufucuentFunds_shuoldThrow_andNotSave(){
        // Arrange
        Wallet wallet = new Wallet("cdpincha1@espe.edu.ec",300.0);
        String walletId = "2"; // usar id numérico como string

        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(wallet));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                walletService.withdraw(walletId,500));

        assertEquals("Insufficient funds",exception.getMessage());
        verify(walletRepository,never()).save(any());

    }
}