package com.example.educationassistant

data class LessonOfTimetable(
    var lessonDocumentId : String,
    var lessonName: String,
    var lessonStartTime: String,
    var lessonEndTime : String,
    var dayOfLesson: String
) {
}