package hiish.tasks.task1.service;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import hiish.tasks.task1.dao.UserRepository;
import hiish.tasks.task1.dto.exeptions.UnauthorizedException;
import hiish.tasks.task1.dto.exeptions.UserAlreadyExests;
import hiish.tasks.task1.dto.exeptions.UserNotFoundExeprion;
import hiish.tasks.task1.dto.user.UserChangeRoleDto;
import hiish.tasks.task1.dto.user.UserDto;
import hiish.tasks.task1.dto.user.UserRegisterDto;
import hiish.tasks.task1.model.User;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class UserServiceImpl implements UserService, CommandLineRunner {
  private final ModelMapper modelMapper;
  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDto registerUser(UserRegisterDto registerDto) {
    if (repository.existsById(registerDto.getLogin())) {
      throw new UserAlreadyExests(registerDto.getLogin());
    }
    User newUser = modelMapper.map(registerDto, User.class);
    String password = passwordEncoder.encode(registerDto.getPassword());
    newUser.setPassword(password);
    newUser.setRole("user");
    repository.save(newUser);
    return modelMapper.map(newUser, UserDto.class);
  }

  @Override
  public UserDto loginUser(String[] credentials) {
    User user = findUser(credentials[0]);
    if (!passwordEncoder.matches(credentials[1], user.getPassword())) {
      throw new UnauthorizedException();
    }
    return modelMapper.map(user, UserDto.class);
  }

  @Override
  public UserDto removeUser(String login) {
    User user = findUser(login);
    repository.delete(user);
    return modelMapper.map(user, UserDto.class);
  }

  @Override
  public UserChangeRoleDto changeRole(String login, String role) {
    User user = findUser(login);
    user.setRole(role);
    repository.save(user);
    return modelMapper.map(user, UserChangeRoleDto.class);
  }

  @Override
  public void run(String... args) throws Exception {
    if (!repository.existsById("admin")) {
      String password = BCrypt.hashpw("admin", BCrypt.gensalt());
      User user = new User("admin", password, "admin");
      repository.save(user);
    }

  }

  private User findUser(String login) {
    return repository.findById(login).orElseThrow(() -> new UserNotFoundExeprion(login));
  }
}
