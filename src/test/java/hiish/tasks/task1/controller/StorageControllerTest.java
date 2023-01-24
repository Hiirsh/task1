package hiish.tasks.task1.controller;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Base64Utils;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import hiish.tasks.task1.Task1Application;
import hiish.tasks.task1.dao.StorageRepository;
import hiish.tasks.task1.dao.UserRepository;
import hiish.tasks.task1.dto.user.UserRegisterDto;
import hiish.tasks.task1.model.User;
import hiish.tasks.task1.service.UserService;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = Task1Application.class)
@AutoConfigureMockMvc
@Testcontainers
public class StorageControllerTest {

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserService userService;

  @Autowired
  StorageController storageController;

  @Autowired
  StorageRepository storageRepository;

  @Autowired
  MockMvc mockMvc;

  @Container
  public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:latest"))
      .withUsername("root")
      .withPassword("1234")
      .withDatabaseName("test");

  @Container
  public static LocalStackContainer localStackContainer = new LocalStackContainer(
      DockerImageName.parse("localstack/localstack:latest"))
      .withServices(S3);

  S3Client s3 = S3Client
      .builder()
      .endpointOverride(localStackContainer.getEndpointOverride(S3))
      .credentialsProvider(
          StaticCredentialsProvider.create(
              AwsBasicCredentials.create(localStackContainer.getAccessKey(),
                  localStackContainer.getSecretKey())))
      .region(Region.of(localStackContainer.getRegion()))
      .build();

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
    registry.add("spring.datasource.password", mySQLContainer::getPassword);
    registry.add("spring.datasource.username", mySQLContainer::getUsername);
  }

  static User admin;
  static User moder;
  static User user;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    storageRepository.deleteAll();
    userService.registerUser(new UserRegisterDto("admin", "admin"));
    userService.changeRole("admin", "admin");
    userService.registerUser(new UserRegisterDto("moder", "moder"));
    userService.changeRole("moder", "moder");
    userService.registerUser(new UserRegisterDto("user", "user"));

  }

  @Test
  void testUploadFile() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "hello.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "Hello, World!".getBytes());
    mockMvc.perform(
        multipart("/api/v1/s3")
            .file(file)
            .header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("admin:admin".getBytes())))
        .andExpect(status().isOk())
        .andReturn();
  }

  @Test
  void testGetFilesInfo() {

  }

  @Test
  void testDeleteFile() {

  }

  @Test
  void testDownloadFile() {

  }

}
