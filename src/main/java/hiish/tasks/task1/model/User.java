package hiish.tasks.task1.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@EqualsAndHashCode(of = "login")
@Table(name="users")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
  @Id
  String login;
  @Setter
  String password;
  @Setter
  @ManyToMany
  Set<Role> roles;
  // LocalDateTime passwordExpiresAt;
  // @Value("${password_expire_perion:30}")
  // long passwordPeriod;
  public boolean addRole(String role) {
    Role newRole = Role.valueOf(role.toUpperCase());
    return this.roles.add(newRole);
  }

  public boolean deleteRole(String role) {
    return this.roles.remove(Role.valueOf(role.toUpperCase()));
  }

}
