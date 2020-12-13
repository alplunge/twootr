package twoot.ap.examples;

import twoot.ap.Position;
import twoot.ap.Twoot;

import java.util.List;
import java.util.function.BinaryOperator;

import static java.util.Comparator.comparingInt;
import static java.util.function.BinaryOperator.maxBy;
import static twoot.ap.Position.INITIAL_POSITION;

public class Reduce {

  private final BinaryOperator<Position> maxPosition = maxBy(comparingInt(Position::getValue));

  Twoot combineTwootsBy(final List<Twoot> twoots, final String senderId, final String newId) {
    return twoots.stream()
        .reduce(
            new Twoot(newId, senderId, "", INITIAL_POSITION),
            (acc, twoot) ->
                new Twoot(
                    newId,
                    senderId,
                    twoot.getContent() + acc.getContent(),
                    maxPosition.apply(acc.getPosition(), twoot.getPosition())));
  }
}
