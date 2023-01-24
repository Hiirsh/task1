package hiish.tasks.task1.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import hiish.tasks.task1.model.File;

public interface StorageRepository extends CrudRepository<File, String> {
  List<File> findAll();

  Optional<File> findOneByName(String name);
}
