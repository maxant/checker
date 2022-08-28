package ch.maxant.checker

import java.time.LocalDateTime

object Model {
    val workExecutions = ArrayList<LocalDateTime>()

    @JvmStatic
    fun addWorkExecution() {
        workExecutions.add(LocalDateTime.now())
    }

}