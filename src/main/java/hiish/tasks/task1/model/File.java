package hiish.tasks.task1.model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@EqualsAndHashCode(of = "s3key")
@Table(name="files")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class File {
  @Id
  String s3key;
  String name;
}
