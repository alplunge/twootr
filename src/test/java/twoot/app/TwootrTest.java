package twoot.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import twoot.ap.*;
import twoot.ap.in_memory.InMemoryTwootRepository;
import twoot.ap.in_memory.InMemoryUserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static twoot.ap.FollowStatus.ALREADY_FOLLOWING;
import static twoot.app.TestData.TWOOT;
import static twoot.app.TestData.twootAt;

public class TwootrTest {

  private static final Position POSITION_1 = new Position(0);

  private final ReceiverEndPoint receiverEndPoint = mock(ReceiverEndPoint.class);
  private final TwootRepository twootRepository = spy(new InMemoryTwootRepository());
  private final UserRepository userRepository = new InMemoryUserRepository();

  private Twootr twootr;
  private SenderEndPoint endPoint;

  @BeforeEach
  public void setUp() {
    twootr = new Twootr(userRepository, twootRepository);

    assertEquals(
        RegistrationStatus.SUCCESS, twootr.onRegisterUser(TestData.USER_ID, TestData.PASSWORD));
    assertEquals(
        RegistrationStatus.SUCCESS,
        twootr.onRegisterUser(TestData.OTHER_USER_ID, TestData.PASSWORD));
  }

  @Test
  public void shouldNotRegisterDuplicateUsers() {
    assertEquals(
        RegistrationStatus.DUPLICATE, twootr.onRegisterUser(TestData.USER_ID, TestData.PASSWORD));
  }

  @Test
  public void shouldBeAbleToAuthenticateUser() {
    logon();
  }

  @Test
  public void shouldNotAuthenticateUserWithWrongPassword() {
    final Optional<SenderEndPoint> endPoint =
        twootr.onLogon(TestData.USER_ID, "bad password", receiverEndPoint);

    assertFalse(endPoint.isPresent());
  }

  @Test
  public void shouldNotAuthenticateUnknownUser() {
    final Optional<SenderEndPoint> endPoint =
        twootr.onLogon(TestData.NOT_A_USER, TestData.PASSWORD, receiverEndPoint);

    assertFalse(endPoint.isPresent());
  }

  @Test
  public void shouldFollowValidUser() {
    logon();

    final FollowStatus followStatus = endPoint.onFollow(TestData.OTHER_USER_ID);

    assertEquals(FollowStatus.SUCCESS, followStatus);
  }

  @Test
  public void shouldNotDuplicateFollowValidUser() {
    logon();

    endPoint.onFollow(TestData.OTHER_USER_ID);

    final FollowStatus followStatus = endPoint.onFollow(TestData.OTHER_USER_ID);
    assertEquals(ALREADY_FOLLOWING, followStatus);
  }

  @Test
  public void shouldNotFollowInValidUser() {
    logon();

    final FollowStatus followStatus = endPoint.onFollow(TestData.NOT_A_USER);

    assertEquals(FollowStatus.INVALID_USER, followStatus);
  }

  @Test
  public void shouldReceiveTwootsFromFollowedUser() {
    final String id = "1";

    logon();

    endPoint.onFollow(TestData.OTHER_USER_ID);

    final SenderEndPoint otherEndPoint = otherLogon();
    otherEndPoint.onSendTwoot(id, TWOOT);

    verify(twootRepository).add(id, TestData.OTHER_USER_ID, TWOOT);
    verify(receiverEndPoint).onTwoot(new Twoot(id, TestData.OTHER_USER_ID, TWOOT, new Position(0)));
  }

  @Test
  public void shouldNotReceiveTwootsAfterLogoff() {
    final String id = "1";

    userFollowsOtherUser();

    final SenderEndPoint otherEndPoint = otherLogon();
    otherEndPoint.onSendTwoot(id, TWOOT);

    verify(receiverEndPoint, never())
        .onTwoot(new Twoot(id, TestData.OTHER_USER_ID, TWOOT, POSITION_1));
  }

  @Test
  public void shouldReceiveReplayOfTwootsAfterLogoff() {
    final String id = "1";

    userFollowsOtherUser();

    final SenderEndPoint otherEndPoint = otherLogon();
    otherEndPoint.onSendTwoot(id, TWOOT);

    logon();

    verify(receiverEndPoint).onTwoot(twootAt(id, POSITION_1));
  }

  @Test
  public void shouldDeleteTwoots() {
    final String id = "1";

    userFollowsOtherUser();

    final SenderEndPoint otherEndPoint = otherLogon();
    otherEndPoint.onSendTwoot(id, TWOOT);
    final DeleteStatus status = otherEndPoint.onDeleteTwoot(id);

    logon();

    assertEquals(DeleteStatus.SUCCESS, status);
    verify(receiverEndPoint, never()).onTwoot(twootAt(id, POSITION_1));
  }

  @Test
  public void shouldNotDeleteFuturePositionTwoots() {
    logon();

    final DeleteStatus status = endPoint.onDeleteTwoot("DAS");

    assertEquals(DeleteStatus.UNKNOWN_TWOOT, status);
  }

  @Test
  public void shouldNotOtherUsersTwoots() {
    final String id = "1";

    logon();

    final SenderEndPoint otherEndPoint = otherLogon();
    otherEndPoint.onSendTwoot(id, TWOOT);

    final DeleteStatus status = endPoint.onDeleteTwoot(id);

    assertNotNull(twootRepository.get(id));
    assertEquals(DeleteStatus.NOT_YOUR_TWOOT, status);
  }

  private SenderEndPoint otherLogon() {
    Optional<SenderEndPoint> logon = logon(TestData.OTHER_USER_ID, mock(ReceiverEndPoint.class));
    return this.endPoint = logon.orElse(null);
  }

  private void userFollowsOtherUser() {
    logon();

    endPoint.onFollow(TestData.OTHER_USER_ID);

    endPoint.onLogoff();
  }

  private void logon() {
    this.endPoint = logon(TestData.USER_ID, receiverEndPoint).orElse(null);
  }

  private Optional<SenderEndPoint> logon(final String userId, final ReceiverEndPoint receiverEndPoint) {
    final Optional<SenderEndPoint> endPoint =
        twootr.onLogon(userId, TestData.PASSWORD, receiverEndPoint);
    assertTrue(endPoint.isPresent(), "Failed to logon");
    return Optional.of(endPoint.get());
  }
}
