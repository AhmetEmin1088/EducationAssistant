package com.example.educationassistant.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "channel",strict = false)
data class Channel constructor(
    @field: ElementList(entry = "item", inline = true)
    var items : List<Item>? = null
)