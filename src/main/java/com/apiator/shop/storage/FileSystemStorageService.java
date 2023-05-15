package com.apiator.shop.storage;

import com.apiator.shop.exception.ApiException;
import com.apiator.shop.exception.InternalException;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileSystemStorageService implements StorageService{
    private final Path storageRootPath;
    private final Tika tika;

    public FileSystemStorageService(@Value("${app.storage}") String storageRoot) {
        storageRootPath = Paths.get(storageRoot);
        tika = new Tika();
    }

    @Override
    public String store(MultipartFile file, String path) {
        if(file == null){
            throw new ApiException("File can't be null", HttpStatus.BAD_REQUEST);
        }
        else if (file.isEmpty()) {
            throw new InternalException("Failed to store empty file");
        }

        byte[] fileBytes;

        try{
            fileBytes = file.getBytes();
        }
        catch (Exception e){
            throw new InternalException("Failed to get file bytes", e);
        }

        String fileMimeType = tika.detect(fileBytes);
        if(!fileMimeType.equals("image/png") && !fileMimeType.equals("image/jpeg")){
            throw new ApiException("Bad file type", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        String fileExtension = fileMimeType.equals("image/png") ? ".png" : ".jpg";
        String newFileUUIDName;
        Path destinationPath;

        do{
            newFileUUIDName = UUID.randomUUID() + fileExtension;
            Path relativeFilePath = Paths.get(path, newFileUUIDName);
            destinationPath = storageRootPath.resolve(relativeFilePath).normalize().toAbsolutePath();
        }while (Files.exists(destinationPath));

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationPath);
        }
        catch (IOException e) {
            throw new InternalException("Failed to store the file", e);
        }

        return newFileUUIDName;
    }

    @Override
    public void remove(String name, String path) {
        Path filePath = storageRootPath.resolve(Paths.get(path, name)).normalize().toAbsolutePath();
        if(Files.exists(filePath)){
            try {
                Files.delete(filePath);
            }
            catch (IOException e) {
                throw new InternalException("Failed to delete the file.", e);
            }
        }
    }
}
