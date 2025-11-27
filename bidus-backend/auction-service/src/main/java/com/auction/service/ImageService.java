package com.auction.service;

import com.auction.entity.Auction;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Service
@Slf4j
public class ImageService {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.region.static:ap-northeast-2}")
    private String region;

    public String saveImagePath(MultipartFile image, Long auctionId) {
        String originalFilename = image.getOriginalFilename();
        String fileExtension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String imageName = auctionId + fileExtension; // 1.jpg

        try {
            // s3에 업로드
            s3Template.upload(bucketName, imageName, image.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("이미지 파일 저장에 실패했습니다.", e);
        }
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + imageName;
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
        String existingImageUrl = auction.getImagePath();

        if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
            try {
                String splitStr = ".amazonaws.com/";
                int index = existingImageUrl.indexOf(splitStr);

                if (index != -1) {
                    String fileName = existingImageUrl.substring(index + splitStr.length());
                    s3Template.deleteObject(bucketName, fileName);
                    log.info("S3 이미지 제거 완료");
                }
            } catch (Exception e) {
                log.error("기존 파일 삭제 실패");
            }
        }
    }

}
