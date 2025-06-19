package org.acme.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.acme.entitites.dtos.UploadResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ApplicationScoped
public class CloudinaryService {
    @Inject
    Cloudinary cloudinary;

    public UploadResponse uploadFile(byte[] fileBytes, String fileName, String folder) throws IOException {

        Map<String, Object> params = new HashMap<>();
        params.put("public_id", folder + "/" + extractFileName(fileName));
        params.put("resource_type", "auto");
        params.put("folder", folder);

        Map<String, Object> result = cloudinary.uploader().upload(fileBytes, params);

        return mapToUploadResponse(result);
    }

    public boolean deleteFile(String publicId) throws IOException {
        Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

        return "ok".equals(result.get("result"));
    }

    private UploadResponse mapToUploadResponse(Map<String, Object> result) {
        return UploadResponse.builder()
                .publicId((String) result.get("public_id"))
                .url((String) result.get("url"))
                .secureUrl((String) result.get("secure_url"))
                .format((String) result.get("format"))
                .bytes(((Number) result.get("bytes")).longValue())
                .width((Integer) result.get("width"))
                .height((Integer) result.get("height"))
                .resourceType((String) result.get("resource_type"))
                .createdAt((String) result.get("created_at"))
                .build();
    }

    private String extractFileName(String fileName) {
        if (fileName == null) return "file";

        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }
}
