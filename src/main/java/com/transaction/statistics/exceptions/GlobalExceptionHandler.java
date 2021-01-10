package com.transaction.statistics.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidTxnException.class)
	public ResponseEntity<Object> handleTransactionInvalidException(InvalidTxnException exception) {
		return new ResponseEntity<>("Old Transaction", HttpStatus.NO_CONTENT);
	}
}
