package hiish.tasks.task1.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UploadDto {
  String key;
  String name;
}
