package kma.topic2.junit.service;

import kma.topic2.junit.UserProvider;
import kma.topic2.junit.exceptions.LoginExistsException;
import kma.topic2.junit.exceptions.UserNotFoundException;
import kma.topic2.junit.model.NewUser;
import kma.topic2.junit.model.User;
import kma.topic2.junit.repository.UserRepository;
import kma.topic2.junit.validation.UserValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class UserServiceTest {

  @Autowired
  private UserService userService;
  @Autowired
  private UserRepository userRepository;
  @SpyBean
  private UserValidator userValidator;

  @Test
  void createNewUser_Success() {
    NewUser userToCreate = UserProvider.prepareNewUser();

    userService.createNewUser(userToCreate);

    User userFromDB = userRepository.getUserByLogin(userToCreate.getLogin());

    assertEquals(userFromDB.getFullName(), userToCreate.getFullName());
    assertEquals(userFromDB.getLogin(), userToCreate.getLogin());
    assertEquals(userFromDB.getPassword(), userToCreate.getPassword());

    verify(userValidator).validateNewUser(userToCreate);
  }

  @Test
  void getExistedUser_Success() {
    NewUser userToGet = UserProvider.prepareNewUser();

    User userFromDB = userRepository.saveNewUser(userToGet);

    assertEquals(userFromDB.getFullName(), userToGet.getFullName());
    assertEquals(userFromDB.getLogin(), userToGet.getLogin());
    assertEquals(userFromDB.getPassword(), userToGet.getPassword());
  }

  @Test
  void getNotExistedUser_returnsError() {
    String wrongLogin = "wrong-login";

    assertThatThrownBy(() -> userService.getUserByLogin(wrongLogin))
        .isInstanceOfSatisfying(UserNotFoundException.class,
            ex -> assertThat(ex.getMessage()).isEqualTo("Can't find user by login: %s", wrongLogin));
  }

  @Test
  void createExistedUser_returnsError() {
    NewUser userToCreate = UserProvider.prepareNewUser();

    userService.createNewUser(userToCreate);

    assertThatThrownBy(() -> userService.createNewUser(userToCreate))
        .isInstanceOfSatisfying(LoginExistsException.class,
            ex -> assertThat(ex.getMessage()).isEqualTo("Login %s already taken", userToCreate.getLogin()));
  }
}
