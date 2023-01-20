package hiish.tasks.task1.service;

import org.springframework.web.multipart.MultipartFile;

import hiish.tasks.task1.dto.FileNameAndKeyDto;
import hiish.tasks.task1.model.DownloadedResource;

public interface StorageService {

  String upload(MultipartFile multipartFile);

  DownloadedResource download(String id);

  Iterable<FileNameAndKeyDto> getFileNamesAndKeys();
  
  Iterable<String> getFileNames();

  Iterable<String> getFileKeys();

  Boolean deleteFile(String key);
}
