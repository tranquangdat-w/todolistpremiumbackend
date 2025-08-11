package com.fsoft.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import com.fsoft.exceptions.ApiException;

public class ImageValidator {
  public static void validateAvatar(MultipartFile image) {
    if (image == null || image.isEmpty()) {
      throw new ApiException(
          "You not upload any image?",
          HttpStatus.BAD_REQUEST.value());
    }

    String contentType = image.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new ApiException("File is not an image", HttpStatus.BAD_REQUEST.value());
    }

    long fileSize = image.getSize();
    if (fileSize > 10 * 1024 * 1024) {
      throw new ApiException("File size exceeds 10MB", HttpStatus.BAD_REQUEST.value());
    }
  }
}
