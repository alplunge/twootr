package twoot.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import twoot.ap.Position;
import twoot.ap.Twoot;
import twoot.ap.TwootQuery;
import twoot.ap.TwootRepository;

import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static twoot.ap.Position.INITIAL_POSITION;
import static twoot.app.TestData.*;

@Disabled("abstract base test")
public abstract class AbstractTwootRepositoryTest {
  @SuppressWarnings("unchecked")
  private Consumer<Twoot> callback = mock(Consumer.class);

  private TwootQuery twootQuery = new TwootQuery();

  protected TwootRepository repository;

  @AfterEach
  public void clear() {
    verifyNoMoreInteractions(callback);

    repository.clear();
  }

  @Test
  public void shouldLoadTwootsFromPosition() {
    final Position position = add("1", TWOOT);
    final Position position2 = add("2", TWOOT_2);

    repository.query(twootQuery.inUsers(USER_ID).lastSeenPosition(position), callback);

    verify(callback).accept(new Twoot("2", USER_ID, TWOOT_2, position2));
  }

  @Test
  public void shouldGetTwootsFromPosition() {
    final String id = "1";

    add(id, TWOOT);

    final Optional<Twoot> result = repository.get(id);
    assertTrue(result.isPresent());
    final Twoot twoot = result.get();
    assertEquals(id, twoot.getId());
    assertEquals(USER_ID, twoot.getSenderId());
    assertEquals(TWOOT, twoot.getContent());
  }

  @Test
  public void shouldDeleteTwootsFromPosition() {
    final String id = "1";

    final Twoot twoot = repository.add(id, USER_ID, TWOOT);

    repository.delete(twoot);

    final Optional<Twoot> result = repository.get(id);
    assertFalse(result.isPresent(), "Twoot wasn't deleted");
  }

  @Test
  public void shouldOnlyLoadTwootsFromFollowedUsers() {
    add("1", TWOOT);

    repository.query(twootQuery.lastSeenPosition(INITIAL_POSITION), callback);
  }

  private Position add(final String id, final String content) {
    final Twoot twoot = repository.add(id, USER_ID, content);
    assertEquals(USER_ID, twoot.getSenderId());
    assertEquals(content, twoot.getContent());
    return twoot.getPosition();
  }
}
