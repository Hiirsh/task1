package hiish.tasks.task1.controller;

import java.util.Base64;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hiish.tasks.task1.dto.user.UserChangeRoleDto;
import hiish.tasks.task1.dto.user.UserDto;
import hiish.tasks.task1.dto.user.UserRegisterDto;
import hiish.tasks.task1.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class UserController {
  final UserService userService;

  @PostMapping("/register")
  public UserDto createUser(@RequestBody UserRegisterDto registerDto) {
    return userService.registerUser(registerDto);
  }

  @PostMapping("/login")
  public UserDto loginUser(@RequestHeader("Authorization") String token) {
    String basicAuth = token.split(" ")[1];
    String decode = new String(Base64.getDecoder().decode(basicAuth));
    String[] credentials = decode.split(":");
    return userService.loginUser(credentials);
  }

  @DeleteMapping("/user/{login}")
  public UserDto removeUser(@PathVariable String login) {
    return userService.removeUser(login);
  }

  @PutMapping("/user/{login}/role/{role}")
  public UserChangeRoleDto changeRole(@PathVariable String login, @PathVariable String role) {
    return userService.changeRole(login, role);
  }

}
