package org.acme.entitites.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta estándar de la API")
public class ApiResponse<T> {

    @Schema(description = "Indica si la operación fue exitosa")
    private boolean success;

    @Schema(description = "Mensaje descriptivo")
    private String message;

    @Schema(description = "Datos de la respuesta")
    private T data;

    @Schema(description = "Código de error (si aplica)")
    private String errorCode;
}
