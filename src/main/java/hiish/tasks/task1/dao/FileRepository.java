package hiish.tasks.task1.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import hiish.tasks.task1.model.File;

public interface FileRepository extends CrudRepository<File, String> {
  List<File> findAll();
}
