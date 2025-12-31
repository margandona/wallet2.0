package com.wallet.infrastructure.entities.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.wallet.domain.valueobjects.Dinero;
import java.math.BigDecimal;

/**
 * Convertidor JPA para transformar entre Dinero (Value Object) y BigDecimal en BD.
 *
 * Permite que las entidades JPA almacenen el Value Object Dinero
 * mientras que en BD solo se guardará la cantidad (BigDecimal).
 *
 * Nota: La moneda se almacena por separado en otra columna.
 */
@Converter(autoApply = true)
public class DineroConverter implements AttributeConverter<Dinero, BigDecimal> {

    /**
     * Convierte Dinero a BigDecimal para almacenar en BD.
     *
     * @param attribute el Dinero (puede ser null)
     * @return BigDecimal cantidad, o null
     */
    @Override
    public BigDecimal convertToDatabaseColumn(Dinero attribute) {
        return attribute == null ? null : attribute.getCantidad();
    }

    /**
     * Convierte BigDecimal de BD a Dinero.
     *
     * Nota: Esta conversión crea un Dinero con moneda por defecto (PEN).
     * Si necesitas una moneda diferente, debes manejar eso en el mapper.
     *
     * @param dbData el BigDecimal desde BD
     * @return Dinero object, o null
     */
    @Override
    public Dinero convertToEntityAttribute(BigDecimal dbData) {
        return dbData == null ? null : new Dinero(dbData);
    }
}
