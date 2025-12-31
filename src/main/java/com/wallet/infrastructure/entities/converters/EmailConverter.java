package com.wallet.infrastructure.entities.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.wallet.domain.valueobjects.Email;

/**
 * Convertidor JPA para transformar entre Email (Value Object) y String en BD.
 *
 * Permite que las entidades JPA almacenen el Value Object Email
 * como texto en la base de datos.
 */
@Converter(autoApply = true)
public class EmailConverter implements AttributeConverter<Email, String> {

    /**
     * Convierte Email a String para almacenar en BD.
     *
     * @param attribute el Email (puede ser null)
     * @return String valor del email, o null
     */
    @Override
    public String convertToDatabaseColumn(Email attribute) {
        return attribute == null ? null : attribute.getValor();
    }

    /**
     * Convierte String de BD a Email.
     *
     * @param dbData el string del email desde BD
     * @return Email object, o null
     */
    @Override
    public Email convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new Email(dbData);
    }
}
