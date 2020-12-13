package twoot.app;

import org.junit.jupiter.api.BeforeEach;
import twoot.ap.in_memory.InMemoryTwootRepository;

public class InMemoryTwootRepositoryTest extends AbstractTwootRepositoryTest {
  @BeforeEach
  public void setUp() {
    repository = new InMemoryTwootRepository();
  }
}
