package com.multigp.racesync.domain.model.io

enum class EventSessionFilter(val title: String) {
    All("All"),
    MySchedule("My Schedule"),
    Spec("Spec"),
    OpenFly("Open Fly");

    companion object {
        fun fromTitle(title: String?): EventSessionFilter =
            entries.firstOrNull { it.title.equals(title, ignoreCase = true) } ?: All
    }
}
