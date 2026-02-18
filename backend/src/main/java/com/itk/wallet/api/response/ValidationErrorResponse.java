package com.itk.wallet.api.response;

import com.itk.wallet.api.response.util.Violation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ValidationErrorResponse {

    private final List<Violation> violations;

}
