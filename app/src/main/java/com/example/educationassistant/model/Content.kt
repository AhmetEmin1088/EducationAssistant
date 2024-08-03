package com.example.educationassistant.model

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root

@Root(name = "content",strict = false)
data class Content constructor(

    @field: Attribute(name = "url")
    var url : String = ""

)