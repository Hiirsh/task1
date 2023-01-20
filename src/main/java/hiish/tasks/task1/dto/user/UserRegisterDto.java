package hiish.tasks.task1.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto {
  String login;
  @Setter
  String password;
  String firstName;
  String lastName;
}
