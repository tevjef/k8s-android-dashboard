package me.tevinjeffrey.kubernetes.api.typeadapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

class ZonedDateTimeConverter : JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {
  override fun serialize(src: ZonedDateTime,
                         typeOfSrc: Type,
                         context: JsonSerializationContext): JsonElement {
    return JsonPrimitive(FORMATTER.format(src))
  }

  @Throws(JsonParseException::class)
  override fun deserialize(json: JsonElement,
                           typeOfT: Type,
                           context: JsonDeserializationContext): ZonedDateTime {
    return FORMATTER.parse(json.asString, ZonedDateTime.FROM)
  }

  companion object {
    private val FORMATTER = DateTimeFormatter.ofPattern("[-]yyyy-MM-dd'T'HH:mm:ss[XXX][X]")
  }
}
