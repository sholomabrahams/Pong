package spring2020.mcon364.pong;

import java.awt.Point;
import java.io.Serializable;

public class Payload implements Serializable {
    final Codes CODE;
    final Point DATA;

    public Payload(Codes code) { //For Codes.GAME_OVER
        CODE = code;
        DATA = new Point();
    }

    public Payload(Codes code, Point data) { //For other Codes
        CODE = code;
        DATA = data;
    }
}
