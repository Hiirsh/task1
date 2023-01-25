package hiish.tasks.task1.controller;

import org.junit.jupiter.api.Test;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
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
import hiish.tasks.task1.service.StorageService;
import hiish.tasks.task1.service.UserService;
import utils.EncodeToBase64;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = Task1Application.class)
@AutoConfigureMockMvc
@Testcontainers

public class UserControllerTest {

  private final static String BASIC_URI = "/api/v1/account/";

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
    registry.add("event-processing.order-event-bucket", () -> BUCKET_NAME);
    registry.add("aws.s3.endpoint-url", () -> localStack.getEndpointOverride(S3));
  }

  static User admin;
  static User moder;
  static User user;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    userService.registerUser(new UserRegisterDto("admin", "admin"));
    userService.changeRole("admin", "admin");
    userService.registerUser(new UserRegisterDto("moder", "moder"));
    userService.changeRole("moder", "moder");
    userService.registerUser(new UserRegisterDto("user", "user"));
  }

  @Test
  void testCreateUser() throws Exception {
    mockMvc.perform(
        post(BASIC_URI + "register")
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
        post(BASIC_URI + "login").header(HttpHeaders.AUTHORIZATION, EncodeToBase64.createBasicAuthorization("user")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect((ResultMatcher) jsonPath("$.login").value("user"))
        .andReturn();
  }

  @Test
  void testChangeRole() throws Exception {
    mockMvc.perform(
        put(BASIC_URI + "user/user/role/moder").header(HttpHeaders.AUTHORIZATION,
            EncodeToBase64.createBasicAuthorization("admin")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect((ResultMatcher) jsonPath("$.login").value("user"))
        .andExpect((ResultMatcher) jsonPath("$.role").value("moder"))
        .andReturn();
    mockMvc.perform(
        put(BASIC_URI + "user/user/role/user").header(HttpHeaders.AUTHORIZATION,
            EncodeToBase64.createBasicAuthorization("moder")))
        .andExpect(status().isForbidden())
        .andReturn();
  }

  @Test
  void testRemoveUser() throws Exception {
    mockMvc.perform(
        delete(BASIC_URI + "user/user").header(HttpHeaders.AUTHORIZATION,
            EncodeToBase64.createBasicAuthorization("moder")))
        .andExpect(status().isForbidden())
        .andReturn();
    mockMvc.perform(
        delete(BASIC_URI + "user/user").header(HttpHeaders.AUTHORIZATION,
            EncodeToBase64.createBasicAuthorization("admin")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect((ResultMatcher) jsonPath("$.login").value("user"))
        .andReturn();

  }
}
