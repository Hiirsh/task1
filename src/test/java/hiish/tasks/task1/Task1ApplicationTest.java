package hiish.tasks.task1;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.springframework.test.web.servlet.ResultMatcher;

import hiish.tasks.task1.dao.StorageRepository;
import hiish.tasks.task1.dao.UserRepository;
import hiish.tasks.task1.dto.user.UserRegisterDto;
import hiish.tasks.task1.model.User;
import hiish.tasks.task1.service.StorageService;
import hiish.tasks.task1.service.UserService;
import utils.EncodeToBase64;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = Task1Application.class)
@AutoConfigureMockMvc
@Testcontainers
public class Task1ApplicationTest {

  private final static String BASIC_URI_STORAGE_CONTROLLER = "/api/v1/s3";
  private final static String BASIC_URI_USER_CONTROLLER = "/api/v1/account/";

  private static final String BUCKET_NAME = "test-bucket";

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserService userService;

  @Autowired
  StorageRepository storageRepository;

  @Autowired
  StorageService storageService;

  @Autowired
  MockMvc mockMvc;

  @Container
  public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:latest"))
      .withUsername("root")
      .withPassword("1234")
      .withDatabaseName("test");

  @Container
  public static LocalStackContainer localStack = new LocalStackContainer(
      DockerImageName.parse("localstack/localstack:latest"))
      .withServices(S3)
      .withEnv("HOSTNAME_EXTERNAL", "localhost")
      .withExposedPorts(4567);

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
    registry.add("spring.datasource.password", mySQLContainer::getPassword);
    registry.add("spring.datasource.username", mySQLContainer::getUsername);
    registry.add("aws.s3.endpoint-url", () -> localStack.getEndpointOverride(S3));
    registry.add("aws.s3.bucket-name", () -> BUCKET_NAME);
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
    MockMultipartFile file = createMultipartFile("testFile1.txt", "File for testing - 1");
    mockMvc.perform(
        multipart(BASIC_URI_STORAGE_CONTROLLER)
            .file(file)
            .header(HttpHeaders.AUTHORIZATION,
            EncodeToBase64.createBasicAuthorization("admin")))
        .andExpect(status().isOk())
        .andReturn();
  }

  @Test
  void testGetFilesInfo() throws Exception {
    String key1 = storageService.upload(createMultipartFile("test1.txt", "File for testing - 1"));
    String key2 = storageService.upload(createMultipartFile("test2.txt", "File for testing - 2"));
    String key3 = storageService.upload(createMultipartFile("test3.txt", "File for testing - 3"));
    MvcResult result = mockMvc.perform(
        get(BASIC_URI_STORAGE_CONTROLLER).header(HttpHeaders.AUTHORIZATION, EncodeToBase64.createBasicAuthorization("admin")))
        .andExpect(status().isOk())
        .andReturn();
    String res = result.getResponse().getContentAsString();
    assertTrue(res.contains(key1));
    assertTrue(res.contains(key2));
    assertTrue(res.contains(key3));
  }

  @Test
  void testDeleteFile() throws Exception {
    String key = storageService.upload(createMultipartFile("test.txt", "File for testing"));
    MvcResult result = mockMvc.perform(
        delete(BASIC_URI_STORAGE_CONTROLLER + "/" + key).header(HttpHeaders.AUTHORIZATION, EncodeToBase64.createBasicAuthorization("admin")))
        .andExpect(status().isOk())
        .andReturn();
    assertTrue("true".equals(result.getResponse().getContentAsString()));
    mockMvc.perform(
        delete(BASIC_URI_STORAGE_CONTROLLER + "/" + key).header(HttpHeaders.AUTHORIZATION, EncodeToBase64.createBasicAuthorization("user")))
        .andExpect(status().isForbidden())
        .andReturn();
    mockMvc.perform(
        delete(BASIC_URI_STORAGE_CONTROLLER + "/" + key).header(HttpHeaders.AUTHORIZATION, EncodeToBase64.createBasicAuthorization("admin")))
        .andExpect(status().isNotFound())
        .andReturn();
  }

  @Test
  void testDownloadFile() throws Exception {
    String key = storageService.upload(createMultipartFile("test.txt", "File for testing"));
    MvcResult result = mockMvc.perform(
        get(BASIC_URI_STORAGE_CONTROLLER + "/" + key).header(HttpHeaders.AUTHORIZATION, EncodeToBase64.createBasicAuthorization("admin")))
        .andExpect(status().isOk())
        .andReturn();
    assertEquals(result.getResponse().getContentAsString(), "File for testing");
  }

  @Test
  void testDownloadNotExistingFile() throws Exception {
    mockMvc.perform(
        get(BASIC_URI_STORAGE_CONTROLLER + "/aaaaaaaaaaa").header(HttpHeaders.AUTHORIZATION, EncodeToBase64.createBasicAuthorization("admin")))
        .andExpect(status().isNotFound())
        .andReturn();
  }

  // UserController tests
  @Test
  void testCreateUser() throws Exception {
    mockMvc.perform(
        post(BASIC_URI_USER_CONTROLLER + "register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"login\": \"John_Doe\", \"password\": \"1234\" }")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect((ResultMatcher) jsonPath("$.login").value("John_Doe"))
        .andReturn();
  }

  @Test
  void testLoginUser() throws Exception {
    mockMvc.perform(
        post(BASIC_URI_USER_CONTROLLER + "login").header(HttpHeaders.AUTHORIZATION, EncodeToBase64.createBasicAuthorization("user")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect((ResultMatcher) jsonPath("$.login").value("user"))
        .andReturn();
  }

  @Test
  void testChangeRole() throws Exception {
    mockMvc.perform(
        put(BASIC_URI_USER_CONTROLLER + "user/user/role/moder").header(HttpHeaders.AUTHORIZATION,
            EncodeToBase64.createBasicAuthorization("admin")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect((ResultMatcher) jsonPath("$.login").value("user"))
        .andExpect((ResultMatcher) jsonPath("$.role").value("moder"))
        .andReturn();
    mockMvc.perform(
        put(BASIC_URI_USER_CONTROLLER + "user/user/role/user").header(HttpHeaders.AUTHORIZATION,
            EncodeToBase64.createBasicAuthorization("moder")))
        .andExpect(status().isForbidden())
        .andReturn();
  }

  @Test
  void testRemoveUser() throws Exception {
    mockMvc.perform(
        delete(BASIC_URI_USER_CONTROLLER + "user/user").header(HttpHeaders.AUTHORIZATION,
            EncodeToBase64.createBasicAuthorization("moder")))
        .andExpect(status().isForbidden())
        .andReturn();
    mockMvc.perform(
        delete(BASIC_URI_USER_CONTROLLER + "user/user").header(HttpHeaders.AUTHORIZATION,
            EncodeToBase64.createBasicAuthorization("admin")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect((ResultMatcher) jsonPath("$.login").value("user"))
        .andReturn();

  }

  private MockMultipartFile createMultipartFile(String fileName, String fileContent) {
    return new MockMultipartFile(
        "file",
        fileName,
        MediaType.TEXT_PLAIN_VALUE,
        fileContent.getBytes());
  }

}
