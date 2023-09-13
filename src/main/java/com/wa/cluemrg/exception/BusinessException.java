package com.wa.cluemrg.exception;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class BusinessException extends RuntimeException {
    String message;
    String code;
}
