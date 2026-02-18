package com.itk.wallet.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class WalletDTO {

    @org.hibernate.validator.constraints.UUID(message = "Incorrect format of the wallet id")
    private UUID id;

    @NotNull
    @Positive(message = "amount must be bigger than 0")
    @Max(value = Long.MAX_VALUE, message = "amount must be less than " + Long.MAX_VALUE)
    private Long amount;

    public void deposit(Long amount) {

        if(amount > 0 && (this.amount + amount) < 0) {
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "funds limit has reached");
        } else {
            this.amount += amount;
        }
    }

    public void withdraw(Long amount) {
        if((this.amount - amount) < 0) {
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "insufficient funds in the account");
        } else {
            this.amount -= amount;
        }
    }

}
