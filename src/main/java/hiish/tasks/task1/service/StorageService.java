package hiish.tasks.task1.service;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3ObjectInputStream;

import hiish.tasks.task1.model.DownloadedResource;

public interface StorageService {

  String upload(MultipartFile multipartFile);

  DownloadedResource download(String id);

  Iterable<String> getFileNames();

  Iterable<String> getFileKeys();

  S3ObjectInputStream deleteFile(String id);
}
