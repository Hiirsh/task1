package hiish.tasks.task1.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import hiish.tasks.task1.dao.UserRepository;
import hiish.tasks.task1.model.User;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException(username));
    // String[] roles = { "Role_" + user.getRole().toUpperCase() };
    return new UserProfile(username, user.getPassword(),
        AuthorityUtils.createAuthorityList("Role_" + user.getRole().toUpperCase()));
    // return new UserProfile(username, user.getPassword(),
    // AuthorityUtils.createAuthorityList("Role_" + user.getRole().toUpperCase()));
  }
}
