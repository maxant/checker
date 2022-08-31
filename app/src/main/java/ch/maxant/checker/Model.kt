package ch.maxant.checker

import java.time.LocalDateTime

object Model {
    var createdAt = LocalDateTime.now();
    val workExecutions = ArrayList<LocalDateTime>()

    @JvmStatic
    fun addWorkExecution() {
        workExecutions.add(LocalDateTime.now())
    }

}