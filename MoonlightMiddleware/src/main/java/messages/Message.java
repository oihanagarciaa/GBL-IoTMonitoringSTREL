package messages;

import eu.quanticol.moonlight.io.MoonLightRecord;

import java.util.List;
import java.util.Map;

public interface Message<M, U> {

    U getConvertedMessage(M message);
}
