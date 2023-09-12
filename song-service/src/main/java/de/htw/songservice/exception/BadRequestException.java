package de.htw.songservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    private String pathVariableName;
    private String pathVariableValue;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String pathVariableName, String pathVariableValue) {
        super(String.format("%s not a valid value for %s", pathVariableValue, pathVariableName));
        this.pathVariableName = pathVariableName;
        this.pathVariableValue = pathVariableValue;
    }
}
