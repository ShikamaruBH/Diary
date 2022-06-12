package com.shikamarubh.diary.model

data class Post(
    var id: String? = null,
    var title: String? = null,
    var content: String? = null,
    var color: Int? = null,
    var created_at: String? = null,
)
