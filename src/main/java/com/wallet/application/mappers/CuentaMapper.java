package com.wallet.application.mappers;

import com.wallet.application.dtos.CuentaDTO;
import com.wallet.domain.entities.Cuenta;
import com.wallet.domain.valueobjects.Dinero;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper para convertir entre Cuenta (Entidad) y CuentaDTO.
 * 
 * Responsabilidades:
 * - Convertir Domain Entity (Cuenta) ↔ DTO (CuentaDTO)
 * - Manejar conversión de Value Objects (Dinero)
 * - Proporcionar métodos estáticos para facilitar uso
 * 
 * @author Wallet Team
 * @version 1.0.0
 */
public class CuentaMapper {
    
    /**
     * Convierte una entidad Cuenta a CuentaDTO.
     * 
     * @param cuenta la entidad Cuenta
     * @return el DTO correspondiente
     */
    public static CuentaDTO toDTO(Cuenta cuenta) {
        if (cuenta == null) {
            return null;
        }
        
        return new CuentaDTO(
            cuenta.getId(),
            cuenta.getNumeroCuenta(),
            cuenta.getUsuarioId(),
            cuenta.getSaldo().getCantidad(),
            cuenta.getSaldo().getMoneda(),
            cuenta.isActiva(),
            cuenta.getFechaCreacion(),
            cuenta.getFechaActualizacion()
        );
    }
    
    /**
     * Convierte un CuentaDTO a entidad Cuenta.
     * 
     * @param dto el DTO
     * @return la entidad Cuenta
     */
    public static Cuenta toEntity(CuentaDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Dinero saldo = new Dinero(dto.getSaldo(), dto.getMoneda());
        
        return new Cuenta(
            dto.getId(),
            dto.getNumeroCuenta(),
            dto.getUsuarioId(),
            saldo,
            dto.getFechaCreacion(),
            dto.getFechaActualizacion(),
            dto.isActiva()
        );
    }
    
    /**
     * Crea una nueva entidad Cuenta desde el ID de usuario.
     * 
     * @param usuarioId ID del usuario propietario
     * @return nueva entidad Cuenta
     */
    public static Cuenta crearCuenta(String usuarioId) {
        return new Cuenta(usuarioId);
    }

    /**
     * Convierte una lista de Cuentas a lista de CuentaDTOs.
     *
     * @param cuentas lista de entidades Cuenta
     * @return lista de DTOs
     */
    public static List<CuentaDTO> toDTOList(List<Cuenta> cuentas) {
        List<CuentaDTO> dtos = new ArrayList<>();
        if (cuentas != null) {
            for (Cuenta cuenta : cuentas) {
                dtos.add(toDTO(cuenta));
            }
        }
        return dtos;
    }

    /**
     * Convierte una lista de CuentaDTOs a lista de Cuentas.
     *
     * @param dtos lista de DTOs
     * @return lista de entidades Cuenta
     */
    public static List<Cuenta> toEntityList(List<CuentaDTO> dtos) {
        List<Cuenta> cuentas = new ArrayList<>();
        if (dtos != null) {
            for (CuentaDTO dto : dtos) {
                cuentas.add(toEntity(dto));
            }
        }
        return cuentas;
    }
}
