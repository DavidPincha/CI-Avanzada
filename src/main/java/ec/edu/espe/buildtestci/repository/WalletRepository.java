package ec.edu.espe.buildtestci.repository;

import ec.edu.espe.buildtestci.model.Wallet;

import java.util.Optional;


public interface WalletRepository {

    Wallet save(Wallet wallet);
    Optional<Wallet> findById(Long id);
    boolean existsByOwnerEmail(String ownerEmail);

    Optional<Wallet> findById(String walletId);
}
