package hiish.tasks.task1.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3ObjectInputStream;

import hiish.tasks.task1.model.DownloadedResource;

public interface StorageService {

  String upload(MultipartFile multipartFile);

  DownloadedResource download(String id);

  List<String> getFileList();

  S3ObjectInputStream deleteFile(String id);
}
