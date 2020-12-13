package twoot.ap.examples;

import twoot.ap.ReceiverEndPoint;
import twoot.ap.Twoot;

public class PrintingEndPointSnippets {
  public static void main(String[] args) {
    final ReceiverEndPoint anonymousClass =
        new ReceiverEndPoint() {
          @Override
          public void onTwoot(final Twoot twoot) {
            System.out.println(twoot.getSenderId() + ": " + twoot.getContent());
          }
        };

    final ReceiverEndPoint lambda =
        twoot -> System.out.println(twoot.getSenderId() + ": " + twoot.getContent());
  }
}
