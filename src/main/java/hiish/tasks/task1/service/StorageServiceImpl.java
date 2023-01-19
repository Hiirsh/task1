package hiish.tasks.task1.service;

import java.io.IOException;
import java.util.List;
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
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import hiish.tasks.task1.model.DownloadedResource;

@Component
public class StorageServiceImpl implements StorageService {

  private static final String FILE_EXTENTION = "fileExtention";
  private final AmazonS3 amazonS3;
  private final String bucketName;

  public StorageServiceImpl(AmazonS3 amazonS3, @Value("${aws.s3.bucket-name}") String bucketName) {
    this.amazonS3 = amazonS3;
    this.bucketName = bucketName;

    if (!amazonS3.doesBucketExistV2(bucketName)) {
      amazonS3.createBucket(bucketName);
    }
  }

  @Override
  public String upload(MultipartFile multipartFile) {
    String key = RandomStringUtils.randomAlphabetic(50);
    try {
      amazonS3.putObject(bucketName, key, multipartFile.getInputStream(), expraObjectMetaData(multipartFile));
    } catch (SdkClientException | IOException e) {
      e.printStackTrace();
    }

    return key;
  }

  @Override
  public DownloadedResource download(String id) {
    S3Object s3Object = amazonS3.getObject(bucketName, id);
    String fileName = id + "." + s3Object.getObjectMetadata().getUserMetadata().get(FILE_EXTENTION);
    Long contentLength = s3Object.getObjectMetadata().getContentLength();

    return DownloadedResource.builder()
        .id(id)
        .fileName(fileName)
        .contentLength(contentLength)
        .inputStream(s3Object.getObjectContent())
        .build();
  }

  @Override
  public List<String> getFileList() {
    return amazonS3.listObjectsV2(bucketName)
        .getObjectSummaries()
        .stream()
        .map(o -> o.getKey())
        .collect(Collectors.toList());
  }

  @Override
  public S3ObjectInputStream  deleteFile(String id) {
    S3Object res = amazonS3.getObject(bucketName, id);
    amazonS3.deleteObject(bucketName, id);
    return res.getObjectContent();
  }

  private ObjectMetadata expraObjectMetaData(MultipartFile file) {
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(file.getSize());
    objectMetadata.setContentType(file.getContentType());
    objectMetadata.getUserMetadata().put(FILE_EXTENTION, FilenameUtils.getExtension(file.getOriginalFilename()));
    return objectMetadata;
  }

}
