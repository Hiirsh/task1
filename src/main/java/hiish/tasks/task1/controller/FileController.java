package hiish.tasks.task1.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import hiish.tasks.task1.model.DownloadedResource;
import hiish.tasks.task1.service.StorageService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@SuppressWarnings("unused")
@Log4j2
public class FileController {
  StorageService storageService;

  @GetMapping("/download")
  public ResponseEntity<Resource> download(String id) {
    DownloadedResource downloadedResource = storageService.download(id);
    return ResponseEntity
        .ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + downloadedResource.getFileName())
        .contentLength(downloadedResource.getContentLength()).contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(new InputStreamResource(downloadedResource.getInputStream()));
  }

  @PostMapping(value = "/upload", produces = "application/json")
  public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
    String key = storageService.upload(file);
    return new ResponseEntity<>(key, HttpStatus.OK);
  }

}
