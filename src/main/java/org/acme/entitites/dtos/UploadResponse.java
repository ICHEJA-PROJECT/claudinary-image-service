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
@Schema(description = "Respuesta de carga de archivo")
public class UploadResponse {
    @Schema(description = "ID público del archivo en Cloudinary")
    private String publicId;

    @Schema(description = "URL del archivo")
    private String url;

    @Schema(description = "URL segura del archivo")
    private String secureUrl;

    @Schema(description = "Formato del archivo")
    private String format;

    @Schema(description = "Tamaño del archivo en bytes")
    private Long bytes;

    @Schema(description = "Ancho de la imagen (si aplica)")
    private Integer width;

    @Schema(description = "Alto de la imagen (si aplica)")
    private Integer height;

    @Schema(description = "Tipo de recurso")
    private String resourceType;

    @Schema(description = "Timestamp de creación")
    private String createdAt;
}
