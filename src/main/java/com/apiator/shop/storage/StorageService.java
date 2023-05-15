package com.apiator.shop.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String store(MultipartFile file, String path);

    void remove(String name, String path);
}
