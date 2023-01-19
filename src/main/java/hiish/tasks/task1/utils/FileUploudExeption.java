package hiish.tasks.task1.utils;

public class FileUploudExeption extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public FileUploudExeption(String id) {
    super("File not uploaded");
  }
}
