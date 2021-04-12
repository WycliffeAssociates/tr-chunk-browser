package org.bibletranslationtools.trchunkbrowser.common.model.audio

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class BttrMetadataMapper {
    private val mapper = ObjectMapper().registerKotlinModule()
    fun toJSON(metadata: BttrMetadata): String = mapper.writeValueAsString(metadata)
    fun fromJSON(json: String): BttrMetadata = mapper.readValue(json)
}
