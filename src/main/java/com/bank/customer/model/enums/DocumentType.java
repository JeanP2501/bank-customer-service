package com.bank.customer.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum DocumentType {

    DNI("DNI", 8),
    RUC("RUC", 11),
    FOREIGNERS_CARD("FOREIGNERS_CARD", 12),
    PASSPORT("PASSPORT", 15);

    private final String code;
    private final int length;

    /**
     * Busca un tipo de documento por su código
     */
    public static Optional<DocumentType> fromCode(String code) {
        return Arrays.stream(values())
                .filter(type -> type.code.equalsIgnoreCase(code))
                .findFirst();
    }

    /**
     * Valida si el código existe
     */
    public static boolean isValid(String code) {
        return fromCode(code).isPresent();
    }

    /**
     * Valida si el número de documento tiene la longitud correcta para el tipo
     */
    public boolean isValidLength(String documentNumber) {
        if (documentNumber == null) {
            return false;
        }
        return documentNumber.length() == this.length;
    }

    /**
     * Valida si el número de documento es numérico (para DNI y RUC)
     */
    public boolean isValidFormat(String documentNumber) {
        if (documentNumber == null || documentNumber.isBlank()) {
            return false;
        }

        // DNI y RUC deben ser solo números
        if (this == DNI || this == RUC) {
            return documentNumber.matches("\\d+");
        }

        // PASSPORT y FOREIGNERS_CARD pueden ser alfanuméricos
        return documentNumber.matches("[A-Za-z0-9]+");
    }

    /**
     * Validación completa: longitud y formato
     */
    public boolean isValidDocument(String documentNumber) {
        return isValidLength(documentNumber) && isValidFormat(documentNumber);
    }
}