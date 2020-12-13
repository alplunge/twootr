package twoot.app;

import twoot.ap.UserRepository;
import twoot.ap.database.DatabaseUserRepository;

public class DatabaseUserRepositoryTest extends AbstractUserRepositoryTest {
  @Override
  protected UserRepository newRepository() {
    return new DatabaseUserRepository();
  }
}
