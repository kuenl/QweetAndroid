package hk.ust.cse.hunkim.questionroom;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * An adapter to due with the timestamp problem of gson.
 * Created by Leung Pui Kuen on 24/10/2015.
 */
public class ISO8601UTCDateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    private static ISO8601UTCDateTypeAdapter mInstance;
    private static final DateFormat dateFormat;

    static {
        mInstance = new ISO8601UTCDateTypeAdapter();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private ISO8601UTCDateTypeAdapter() {
    }


    public static synchronized ISO8601UTCDateTypeAdapter getInstance() {
        return mInstance;
    }

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return dateFormat.parse(json.getAsString());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(dateFormat.format(src));
    }
}
