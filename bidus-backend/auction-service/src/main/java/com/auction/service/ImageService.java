package com.auction.service;

import com.auction.entity.Auction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {

    @Value("${image.upload.base-dir}")
    private String baseImageDir;

    public String saveImagePath(MultipartFile image, Long auctionId) {
        String originalFilename = image.getOriginalFilename();
        String fileExtension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String imageName = auctionId + fileExtension; // 1.jpg
        Path imageFilePath = Paths.get(baseImageDir, imageName);

        try {
            image.transferTo(imageFilePath);
        } catch (IOException e) {
            throw new RuntimeException("이미지 파일 저장에 실패했습니다.", e);
        }
        return "/images/" + imageName;
    }

    public String updateImagePath(MultipartFile image, Auction auction) {
        // 1. 기존 파일 제거
        if (image != null && !image.isEmpty()) {
            deleteImagePath(auction);
            return saveImagePath(image, auction.getId());
        }
        return null;
    }

    public void deleteImagePath(Auction auction) {
        String existingImageName = auction.getImagePath();
        if (existingImageName != null && !existingImageName.isEmpty()) {
            try {
                Path existingFilePath = Paths.get(baseImageDir, existingImageName.replace("/images/", ""));
                if (Files.exists(existingFilePath)) {
                    Files.delete(existingFilePath);
                }
            } catch (IOException e) {
                System.out.println("기존 파일 삭제 실패");
            }
        }
    }

}
