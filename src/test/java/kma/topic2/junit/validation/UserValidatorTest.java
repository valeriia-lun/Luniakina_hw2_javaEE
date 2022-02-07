package kma.topic2.junit.validation;

import kma.topic2.junit.UserProvider;
import kma.topic2.junit.exceptions.ConstraintViolationException;
import kma.topic2.junit.model.NewUser;
import kma.topic2.junit.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private UserValidator userValidator;

  @Test
  void validateNewUser_success() {
    NewUser user = UserProvider.prepareNewUser();
    userValidator.validateNewUser(user);

    verify(userRepository).isLoginExists(user.getLogin());
  }

  @ParameterizedTest
  @MethodSource("verifyUsersPassword")
  void validateUsers_returnsError(String password, List<String> errors) {
    assertThatThrownBy(() -> userValidator.validateNewUser(UserProvider.prepareNewUserParametrized(password)))
        .isInstanceOfSatisfying(ConstraintViolationException.class,
            ex -> assertThat(ex.getErrors()).isEqualTo(errors));

  }

  private static Stream<Arguments> verifyUsersPassword() {
    return Stream.of(
        Arguments.of("12345678", List.of("Password has invalid size")),
        Arguments.of("qwerty@#$-*", List.of("Password has invalid size", "Password doesn't match regex")),
        Arguments.of("aa", List.of("Password has invalid size")),
        Arguments.of("a-", List.of("Password has invalid size", "Password doesn't match regex"))
    );
  }
}
