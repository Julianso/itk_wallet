package com.itk.wallet.api.request;

import com.itk.wallet.utils.OperationType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class WalletUpdateRequest {


    private UUID id;

    private OperationType operationType;

    @NotNull
    @Min(value = 1, message = "Amount must be bigger than 0")
    @Max(value = Long.MAX_VALUE, message = "Amount must be less than " + Long.MAX_VALUE)
    private Long amount;

}
