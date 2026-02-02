package ec.edu.espe.buildtestci.service;

import ec.edu.espe.buildtestci.dto.WalletResponse;
import ec.edu.espe.buildtestci.model.Wallet;
import ec.edu.espe.buildtestci.repository.WalletRepository;

import java.util.Optional;

public class WalletService {
    private final WalletRepository walletRepository;
    private final RiskClient riskClient;

    public WalletService(WalletRepository walletRepository, RiskClient riskClient) {
        this.walletRepository = walletRepository;
        this.riskClient = riskClient;
    }

    public WalletResponse createWallet(String ownerEmail, double initialBalance) {
        if (ownerEmail == null || !ownerEmail.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }

        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }

        if (riskClient.isBlocked(ownerEmail)) {
            throw new IllegalStateException("User blocked");
        }

        if (walletRepository.existsByOwnerEmail(ownerEmail)) {
            throw new IllegalStateException("Wallet already exists");
        }

        Wallet wallet = new Wallet(null, ownerEmail, initialBalance);
        Wallet saved = walletRepository.save(wallet);

        return new WalletResponse(saved.getId(), saved.getBalance());
    }

    public double deposit(String walletId, double amount){
        if(amount < 0){
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        Optional<Wallet> found = walletRepository.findById(walletId);
        if(found.isEmpty()){
            throw new IllegalStateException("Wallet not found");
        }

        Wallet wallet = found.get();
        wallet.deposit(amount);

        walletRepository.save(wallet);
        return wallet.getBalance();
    }
}