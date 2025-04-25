package es.uniovi.asturnatura.util

import com.squareup.moshi.*
import es.uniovi.asturnatura.model.SlideContent
import java.lang.reflect.Type

class SlideListJsonAdapter {

    @FromJson
    fun fromJson(reader: JsonReader, delegate: JsonAdapter<List<SlideContent>>): List<SlideContent>? {
        return if (reader.peek() == JsonReader.Token.BEGIN_ARRAY) {
            delegate.fromJson(reader)
        } else {
            val slide = Moshi.Builder().build().adapter(SlideContent::class.java).fromJson(reader)
            slide?.let { listOf(it) }
        }
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: List<SlideContent>?, delegate: JsonAdapter<List<SlideContent>>) {
        delegate.toJson(writer, value)
    }
}
