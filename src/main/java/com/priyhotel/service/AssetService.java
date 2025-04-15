package com.priyhotel.service;

import com.priyhotel.constants.AssetType;
import com.priyhotel.entity.Asset;
import com.priyhotel.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AssetService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.base-url}")
    private String baseUrl;

    @Autowired
    private AssetRepository assetRepository;

    public List<Asset> uploadAssets(List<MultipartFile> files, String folderName){
        List<Asset> assets = new ArrayList<>();
        files.forEach(file ->{
            Asset newAsset = new Asset();
            newAsset.setAssetType(file.getContentType());
            newAsset.setAssetUrl(this.uploadAsset(file, folderName));
            assets.add(newAsset);
        });

        return this.saveAllAssets(assets);

    }

    public String uploadAsset(MultipartFile file, String folderName) {
        try {
            // Create directory if it doesn't exist
            String dirPath = uploadDir + File.separator + folderName;
            File directory = new File(dirPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate unique filename
            String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            Path filePath = Paths.get(dirPath, fileName);

            // Save file to the directory
            Files.write(filePath, file.getBytes());

            // Return relative path for storage
            return baseUrl + "/" + folderName + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Error saving file", e);
        }
    }

    public Asset saveAsset(Asset asset){
        return assetRepository.save(asset);
    }

    public List<Asset> saveAllAssets(List<Asset> assets){
        return assetRepository.saveAll(assets);
    }

    public List<Asset> getAllAssetsByIds(List<Long> assetIds) {
        return assetRepository.findAllById(assetIds);
    }
}
