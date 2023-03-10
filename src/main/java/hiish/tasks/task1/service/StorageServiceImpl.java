package hiish.tasks.task1.service;

import java.io.IOException;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import hiish.tasks.task1.dao.StorageRepository;
import hiish.tasks.task1.dto.exeptions.FileNotExist;
import hiish.tasks.task1.dto.file.FileNameAndKeyDto;
import hiish.tasks.task1.model.DownloadedResource;
import hiish.tasks.task1.model.File;

@Component
public class StorageServiceImpl implements StorageService {

  private static final String FILE_EXTENTION = "fileExtention";
  private final AmazonS3 amazonS3;
  private final String bucketName;
  private final StorageRepository repository;

  public StorageServiceImpl(AmazonS3 amazonS3, @Value("${aws.s3.bucket-name}") String bucketName,
      StorageRepository repository) {
    this.amazonS3 = amazonS3;
    this.bucketName = bucketName;
    this.repository = repository;
    if (!amazonS3.doesBucketExistV2(bucketName)) {
      amazonS3.createBucket(bucketName);
    }
  }

  @Override
  public String upload(MultipartFile multipartFile) {
    String key = RandomStringUtils.randomAlphabetic(50) + "_" + multipartFile.getOriginalFilename();
    try {
      amazonS3.putObject(bucketName, key, multipartFile.getInputStream(), expraObjectMetaData(multipartFile));
      File newFile = new File(key, multipartFile.getOriginalFilename());
      repository.save(newFile);
    } catch (SdkClientException | IOException e) {
      e.printStackTrace();
    }
    return key;
  }

  @Override
  public DownloadedResource download(String key) {

    S3Object s3Object;
    try {
      s3Object = amazonS3.getObject(bucketName, key);
    } catch (Exception e) {
      throw new FileNotExist();
    }
    String fileName = getFileName(key) + "." + s3Object.getObjectMetadata().getUserMetadata().get(FILE_EXTENTION);
    Long contentLength = s3Object.getObjectMetadata().getContentLength();
    return DownloadedResource.builder()
        .id(key)
        .fileName(fileName)
        .contentLength(contentLength)
        .inputStream(s3Object.getObjectContent())
        .build();
  }

  @Override
  public Iterable<String> getFileKeys() {
    return amazonS3.listObjectsV2(bucketName)
        .getObjectSummaries()
        .stream()
        .map(o -> o.getKey())
        .collect(Collectors.toList());
  }

  @Override
  public Iterable<String> getFileNames() {
    return repository.findAll().stream().map(f -> f.getName()).collect(Collectors.toList());
  }

  @Override
  public Iterable<FileNameAndKeyDto> getFileNamesAndKeys() {
    return repository.findAll().stream()
        .map(f -> FileNameAndKeyDto.builder().name(f.getName()).key(f.getS3key()).build())
        .collect(Collectors.toList());
  }

  @Override
  public Boolean deleteFile(String key) {
    try {
      amazonS3.deleteObject(bucketName, key);
      repository.deleteById(key);
    } catch (Exception e) {
      throw new FileNotExist(key);
    }
    return true;

  }

  private ObjectMetadata expraObjectMetaData(MultipartFile file) {
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(file.getSize());
    objectMetadata.setContentType(file.getContentType());
    objectMetadata.getUserMetadata().put(FILE_EXTENTION, FilenameUtils.getExtension(file.getOriginalFilename()));
    return objectMetadata;
  }

  private String getFileName(String key) {
    File file = repository.findById(key).orElseThrow(() -> new FileNotExist(key));
    return file.getName();
  }

}
