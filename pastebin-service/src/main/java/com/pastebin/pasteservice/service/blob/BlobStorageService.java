package com.pastebin.pasteservice.service.blob;

public interface BlobStorageService {
    void store(String key, String content);
    String retrieve(String key);
    void delete(String key);
}
