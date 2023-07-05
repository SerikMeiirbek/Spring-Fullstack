package com.serikscode.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class DuplicateResourseException extends RuntimeException{
    public DuplicateResourseException(String message) {
        super(message);
    }
}
