package ec.edu.espe.buildtestci.model;

import java.util.UUID;

public class Wallet {
    private final String id;
    private final String ownerEmail;
    private double balance;

    public Wallet(String id, String ownerEmail, double balance) {
        if (id == null) {
            this.id = UUID.randomUUID().toString();
        } else {
            this.id = id;
        }
        this.ownerEmail = ownerEmail;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public double getBalance() {
        return balance;
    }

    //Depositar dinero de la cuenta
    public void deposit(double amount){
        this.balance += amount;
    }

    //Retirar dinero si existe saldo suficiente
    public void withdraw(double amount){
        if (amount > this.balance) {
            throw new IllegalStateException("Insufficient funds");
        }
        this.balance -= amount;
    }
}