package com.example.educationassistant.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "rss", strict = false)
data class RSS constructor(
    @field: Element
    var channel: Channel? = null
)