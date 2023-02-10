package hiish.tasks.task1.dto.file;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class FileListDto {
  Iterable<String> files;
}
