package twoot.ap.examples;

import twoot.ap.ReceiverEndPoint;
import twoot.ap.Twoot;

public class PrintingEndPoint implements ReceiverEndPoint {
  @Override
  public void onTwoot(final Twoot twoot) {
    System.out.println(twoot.getSenderId() + ": " + twoot.getContent());
  }
}
