package com.example.storyapp

import com.example.storyapp.model.story.ListStoryItem

object DummyData {

    fun generateStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = i.toString(),
                photoUrl = "Photo $i",
                name = "Name $i",
                description = "DESC $i",
                createdAt = "Created at $i",
                lon =( 2.1 + i),
                lat = (2.2 + i),
            )
            items.add(story)
        }
        return items
    }
}