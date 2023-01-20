package hiish.tasks.task1.dao;

import org.springframework.data.repository.CrudRepository;

import hiish.tasks.task1.model.User;

public interface UserRepository extends CrudRepository<User, String> {
}
