package cs.wnmc.salutapp;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.OutputStream;


@JsonObject
public class Message {

    @JsonField
    public String name;

    @JsonField
    public String type;

    @JsonField
    public String encodedMessage;

    @JsonField
    public OutputStream os;

    /*
     * Note that since this field isn't annotated as a
     * @JsonField, LoganSquare will ignore it when parsing
     * and serializing this class.
     */
    public int nonJsonField;

}
