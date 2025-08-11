package com.fsoft.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.ListSharedLinksResult;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;

import com.fsoft.configuration.DropboxProperties;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DropboxService {

  private final DropboxProperties dropboxProperties;

  private DbxClientV2 getClient() {
    DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-service").build();

    // Dùng Refresh Token để tạo Access Token mới khi cần
    DbxCredential credential = new DbxCredential(
        "",
        -1L, // expiration (không cần dùng nếu dùng refresh token)
        dropboxProperties.getREFRESH_TOKEN_API_DROP_BOX(),
        dropboxProperties.getAPP_KEY(),
        dropboxProperties.getAPP_SECRET());

    return new DbxClientV2(config, credential);
  }

  public Optional<String> uploadImage(
      MultipartFile avatar, UUID userId) throws IOException, DbxException {
    DbxClientV2 client = getClient();
    String path = String.format("/users/%s", userId);

    try (InputStream in = avatar.getInputStream()) {
      FileMetadata metadata = client.files()
          .uploadBuilder(path)
          .withMode(WriteMode.OVERWRITE)
          .uploadAndFinish(in);

      return Optional.of(getOrCreateSharedLink(client, metadata.getPathDisplay()));
    }
  }

  private String getOrCreateSharedLink(DbxClientV2 client, String filePath) throws DbxException {
    // Lấy link cũ nếu đã tồn tại
    ListSharedLinksResult existingLinks = client.sharing()
        .listSharedLinksBuilder()
        .withPath(filePath)
        .withDirectOnly(true)
        .start();

    if (!existingLinks.getLinks().isEmpty()) {
      return existingLinks.getLinks().get(0).getUrl().replace("dl=0", "dl=1");
    }

    // Nếu chưa có link, tạo link mới
    SharedLinkMetadata linkMetadata = client.sharing()
        .createSharedLinkWithSettings(filePath);

    return linkMetadata.getUrl().replace("dl=0", "dl=1");
  }
}
