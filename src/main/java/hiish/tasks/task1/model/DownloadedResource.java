package hiish.tasks.task1.model;

import java.io.InputStream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class DownloadedResource {
  private String id;
  private String fileName;
  private Long contentLength;
  private InputStream inputStream;
}
