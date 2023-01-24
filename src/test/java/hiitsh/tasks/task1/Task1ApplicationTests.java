package hiitsh.tasks.task1;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import hiish.tasks.task1.Task1Application;

@Testcontainers
@SpringBootTest(classes = Task1Application.class)
class Task1ApplicationTests {

	@Container
	public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:latest"))
			.withUsername("root")
			.withPassword("1234")
			.withDatabaseName("test");

	@DynamicPropertySource
	static void properties(DynamicPropertyRegistry registry){
		registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
		registry.add("spring.datasource.password", mySQLContainer::getPassword);
		registry.add("spring.datasource.username", mySQLContainer::getUsername);
	}

	@Test
	void contextLoads() {
		System.out.println("Context loaded!");
	}

}
