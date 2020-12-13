package twoot.app;

import twoot.ap.UserRepository;
import twoot.ap.in_memory.InMemoryUserRepository;

public class InMemoryUserRepositoryTest extends AbstractUserRepositoryTest {
  private InMemoryUserRepository inMemoryUserRepository = new InMemoryUserRepository();

  @Override
  protected UserRepository newRepository() {
    return inMemoryUserRepository;
  }
}
