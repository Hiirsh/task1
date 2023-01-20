package hiish.tasks.task1.dto.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.NoArgsConstructor;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "File not found")
@NoArgsConstructor
public class FileNotExist extends RuntimeException {
  private static final long serialVersionUID = 2L;

  public FileNotExist(String name) {
    super("File with name " + name + " not found");
  }
}
