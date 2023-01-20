package hiish.tasks.task1.service;

import hiish.tasks.task1.dto.user.UserChangeRoleDto;
import hiish.tasks.task1.dto.user.UserDto;
import hiish.tasks.task1.dto.user.UserRegisterDto;

public interface UserService {
  UserDto registerUser(UserRegisterDto registerDto);

  UserDto loginUser(String[] credentials);

  UserDto removeUser(String login);

  UserChangeRoleDto changeRole(String login, String role);
}
