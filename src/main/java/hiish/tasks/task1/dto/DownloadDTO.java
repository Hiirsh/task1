package hiish.tasks.task1.dto;

import org.springframework.core.io.InputStreamResource;

import lombok.Builder;

@Builder
public class DownloadDTO {
  InputStreamResource resource;
}
