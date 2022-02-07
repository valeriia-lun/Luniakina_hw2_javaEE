package kma.topic2.junit;

import kma.topic2.junit.model.NewUser;

public abstract class UserProvider {

  public static NewUser prepareNewUser() {
    long currentTime = System.currentTimeMillis();

    return NewUser.builder()
        .fullName("user-name")
        .login("login-" + currentTime)
        .password("pAsS123")
        .build();
  }

  public static NewUser prepareNewUserParametrized(String password) {
    long currentTime = System.currentTimeMillis();

    return NewUser.builder()
        .fullName("user-name")
        .login("login-" + currentTime)
        .password(password)
        .build();
  }
}
