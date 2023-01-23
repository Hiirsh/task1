package hiitsh.tasks.task1;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;


import hiish.tasks.task1.dao.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = Task1ApplicationTests.class)
@RunWith(SpringRunner.class)
@Testcontainers
class Task1ApplicationTests {

	@Autowired
	private UserRepository userRepository;

	private static MySQLContainer<?> container = new MySQLContainer<>("mysql:latest")
			.withDatabaseName("goza_task1_test")
			.withUsername("root")
			.withPassword("1234");

	@DynamicPropertySource
	public static void overrideProps(DynamicPropertyRegistry registry){
		registry.add("spring.datasorce.url", container::getJdbcUrl);
	}

	@Test
	void contextLoads() {
		assertNotNull(userRepository);
	}

}
