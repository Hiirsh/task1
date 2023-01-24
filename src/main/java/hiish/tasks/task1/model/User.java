package hiish.tasks.task1.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(of = "login")
@Table(name="users")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User implements Serializable {
  @Id
  String login;
  @Setter
  String password;
  @Setter
  String role;

}
