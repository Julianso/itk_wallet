package com.itk.wallet.api.response;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalletUpdateResponse {

    private UUID valletId;

    private String operationType;

    private Long amount;

}
