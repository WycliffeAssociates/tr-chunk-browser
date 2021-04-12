package org.bibletranslationtools.trchunkbrowser.common.model.audio

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import org.wycliffeassociates.otter.common.audio.wav.WavCue
import java.lang.IllegalArgumentException
import java.text.ParseException

class MarkerListDeserializer : JsonDeserializer<List<WavCue>>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): List<WavCue> {
        val node = p?.codec?.readTree<JsonNode>(p) ?: throw ParseException("Unable to parse json", 0)
        val cuePoints = mutableListOf<WavCue>()
        for (key in node.fieldNames()) {
            cuePoints.add(WavCue(node.get(key).asInt(), key))
        }
        return cuePoints
    }
}

class MarkerListSerializer : JsonSerializer<List<WavCue>>() {
    override fun serialize(value: List<WavCue>?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        if (value == null) throw IllegalArgumentException("Input cannot be null")
        gen?.writeStartObject()
        for (cue in value) {
            gen?.writeNumberField(cue.label, cue.location)
        }
        gen?.writeEndObject()
    }
}
