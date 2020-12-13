package twoot.app;

import org.junit.jupiter.api.BeforeEach;
import twoot.ap.database.DatabaseTwootRepository;

import java.io.IOException;

public class DatabaseTwootRepositoryTest extends AbstractTwootRepositoryTest {
  @BeforeEach
  public void setUp() throws IOException {
    repository = new DatabaseTwootRepository();
  }
}
