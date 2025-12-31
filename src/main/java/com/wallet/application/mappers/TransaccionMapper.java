package com.wallet.application.mappers;

import com.wallet.application.dtos.TransaccionDTO;
import com.wallet.domain.entities.Transaccion;
import com.wallet.domain.valueobjects.Dinero;
import com.wallet.domain.valueobjects.TipoTransaccion;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper para convertir entre Transaccion (Entidad) y TransaccionDTO.
 * 
 * Responsabilidades:
 * - Convertir Domain Entity (Transaccion) ↔ DTO (TransaccionDTO)
 * - Manejar conversión de Value Objects (Dinero, TipoTransaccion)
 * - Proporcionar métodos estáticos para facilitar uso
 * 
 * @author Wallet Team
 * @version 1.0.0
 */
public class TransaccionMapper {
    
    /**
     * Convierte una entidad Transaccion a TransaccionDTO.
     * 
     * @param transaccion la entidad Transaccion
     * @return el DTO correspondiente
     */
    public static TransaccionDTO toDTO(Transaccion transaccion) {
        if (transaccion == null) {
            return null;
        }
        
        return new TransaccionDTO(
            transaccion.getId(),
            transaccion.getCuentaOrigenId(),
            transaccion.getTipo().name(),
            transaccion.getMonto().getCantidad(),
            transaccion.getMonto().getMoneda(),
            transaccion.getDescripcion(),
            transaccion.getFecha(),
            transaccion.getCuentaDestinoId()
        );
    }
    
    /**
     * Convierte un TransaccionDTO a entidad Transaccion.
     * 
     * @param dto el DTO
     * @return la entidad Transaccion
     */
    public static Transaccion toEntity(TransaccionDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Dinero monto = new Dinero(dto.getMonto(), dto.getMoneda());
        TipoTransaccion tipo = TipoTransaccion.valueOf(dto.getTipo());
        
        return new Transaccion(
            dto.getId(),
            tipo,
            monto,
            dto.getCuentaId(),
            dto.getCuentaDestinoId(),
            dto.getDescripcion(),
            dto.getFecha(),
            Dinero.CERO,
            monto
        );
    }

    /**
     * Convierte una lista de Transacciones a lista de TransaccionDTOs.
     *
     * @param transacciones lista de entidades Transaccion
     * @return lista de DTOs
     */
    public static List<TransaccionDTO> toDTOList(List<Transaccion> transacciones) {
        List<TransaccionDTO> dtos = new ArrayList<>();
        if (transacciones != null) {
            for (Transaccion transaccion : transacciones) {
                dtos.add(toDTO(transaccion));
            }
        }
        return dtos;
    }

    /**
     * Convierte una lista de TransaccionDTOs a lista de Transacciones.
     *
     * @param dtos lista de DTOs
     * @return lista de entidades Transaccion
     */
    public static List<Transaccion> toEntityList(List<TransaccionDTO> dtos) {
        List<Transaccion> transacciones = new ArrayList<>();
        if (dtos != null) {
            for (TransaccionDTO dto : dtos) {
                transacciones.add(toEntity(dto));
            }
        }
        return transacciones;
    }
}
