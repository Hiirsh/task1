package hiish.tasks.task1.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import hiish.tasks.task1.dto.FileNameAndKeyDto;
import hiish.tasks.task1.dto.UploadDto;
import hiish.tasks.task1.model.DownloadedResource;
import hiish.tasks.task1.service.StorageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/s3")
@RequiredArgsConstructor
public class StorageController {
  final StorageService storageService;

  @PostMapping(produces = "application/json")
  public ResponseEntity<UploadDto> uploadFile(@RequestParam("file") MultipartFile file) {
    String key = storageService.upload(file);
    UploadDto response = UploadDto.builder().key(key).name(file.getOriginalFilename()+file.getContentType()).build();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/{key}")
  public ResponseEntity<Resource> downloadFile(@PathVariable String key) {
    DownloadedResource downloadedResource = storageService.download(key);
    return ResponseEntity
        .ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + downloadedResource.getFileName())
        .contentLength(downloadedResource.getContentLength()).contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(new InputStreamResource(downloadedResource.getInputStream()));
  }

  @GetMapping()
  public Iterable<FileNameAndKeyDto> getFilesInfo() {
    return storageService.getFileNamesAndKeys();
  }

  @DeleteMapping("/{key}")
  public Boolean deleteFile(@PathVariable String key) {
    return storageService.deleteFile(key);
  }
}
