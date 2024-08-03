package com.example.educationassistant.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "item", strict = false)
data class Item constructor(

    @JvmField
    @field: Element(required = false)
    var title: String = "",

    @JvmField
    @field: Element(required = false)
    var link: String = "",

    @JvmField
    @field: Element(required = false)
    var content: Content? = null

)