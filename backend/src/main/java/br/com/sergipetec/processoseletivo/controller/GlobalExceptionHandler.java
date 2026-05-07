package br.com.sergipetec.processoseletivo.controller;

import br.com.sergipetec.processoseletivo.dto.ErroResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Trata erros de Regra de Negócio (ex: transição de status inválida)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErroResponseDTO> handleBusinessExceptions(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponseDTO(ex.getMessage()));
    }

    // Trata os erros de status inexistente
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroResponseDTO(ex.getMessage()));
    }

    // Trata os erros de validação do DTO (@NotNull, @NotBlank, etc)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}