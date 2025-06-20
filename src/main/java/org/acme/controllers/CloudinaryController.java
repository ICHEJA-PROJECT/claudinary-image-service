package org.acme.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.acme.entitites.dtos.ApiResponse;
import org.acme.entitites.dtos.UploadResponse;
import org.acme.services.CloudinaryService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Path("/api/cloudinary")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Cloudinary", description = "Operaciones con archivos en Cloudinary")
public class CloudinaryController {
    @Inject
    CloudinaryService cloudinaryService;

    public static class FileUploadForm {
        @RestForm("file")
        @PartType(MediaType.APPLICATION_OCTET_STREAM)
        public byte[] file;

        @RestForm("fileName")
        @PartType(MediaType.TEXT_PLAIN)
        public String fileName;

        @RestForm("folder")
        @PartType(MediaType.TEXT_PLAIN)
        public String folder = "uploads";
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Subir archivo", description = "Sube un archivo a Cloudinary")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Archivo subido exitosamente",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @APIResponse(responseCode = "400", description = "Error en la solicitud"),
            @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response uploadFile(
            @RestForm("file") @Parameter(description = "Archivo a subir") InputStream fileStream,
            @RestForm("fileName") @Parameter(description = "Nombre del archivo") String fileName,
            @RestForm("folder") @Parameter(description = "Carpeta destino") String folder) {
        try {
            if (fileStream == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.<String>builder()
                                .success(false)
                                .message("No se proporcionó archivo")
                                .errorCode("NO_FILE")
                                .build())
                        .build();
            }

            // Convertir InputStream a byte array
            byte[] fileBytes = fileStream.readAllBytes();

            if (fileBytes.length == 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.<String>builder()
                                .success(false)
                                .message("El archivo está vacío")
                                .errorCode("EMPTY_FILE")
                                .build())
                        .build();
            }

            UploadResponse uploadResponse = cloudinaryService.uploadFile(
                    fileBytes,
                    fileName,
                    folder != null ? folder : "uploads"
            );

            return Response.ok(ApiResponse.<UploadResponse>builder()
                            .success(true)
                            .message("Archivo subido exitosamente")
                            .data(uploadResponse)
                            .build())
                    .build();

        } catch (IOException e) {
            log.error("Error al subir archivo", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.<String>builder()
                            .success(false)
                            .message("Error al subir archivo: " + e.getMessage())
                            .errorCode("UPLOAD_ERROR")
                            .build())
                    .build();
        }
    }

    @DELETE
    @Path("/{publicId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Eliminar archivo", description = "Elimina un archivo de Cloudinary")
    public Response deleteFile(
            @PathParam("publicId") @Parameter(description = "ID público del archivo") String publicId) {

        try {
            boolean deleted = cloudinaryService.deleteFile(publicId);

            if (deleted) {
                return Response.ok(ApiResponse.<String>builder()
                                .success(true)
                                .message("Archivo eliminado exitosamente")
                                .data("Archivo con ID: " + publicId + " eliminado")
                                .build())
                        .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.<String>builder()
                                .success(false)
                                .message("Archivo no encontrado")
                                .errorCode("FILE_NOT_FOUND")
                                .build())
                        .build();
            }

        } catch (IOException e) {
            log.error("Error al eliminar archivo", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.<String>builder()
                            .success(false)
                            .message("Error al eliminar archivo: " + e.getMessage())
                            .errorCode("DELETE_ERROR")
                            .build())
                    .build();
        }
    }
}
