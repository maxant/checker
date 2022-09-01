package ch.maxant.checker

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Model {
    val queries = ArrayList<Query>()

    @JvmStatic
    fun addQuery(): Query {
        val q = Query(LocalDateTime.now())
        queries.add(q)
        return q
    }

}

data class Query(val start: LocalDateTime, var end: LocalDateTime? = null, var result: String? = null, var state: Boolean? = null) {
    fun setSuccess(result: String) {
        this.result = result
        this.state = true
        this.end = LocalDateTime.now()
    }
    fun setFailed(result: String) {
        this.result = result
        this.state = false
        this.end = LocalDateTime.now()
    }

    override fun toString(): String {
        return start.format(DateTimeFormatter.ISO_DATE_TIME).substring(0, 19) + ": " +
                if(null == state) "Pending..." else
                        (   if(true == state) "Success in " + Duration.between(start, end).toMillis() + "ms"
                            else "*** FAILED *** $result")
    }
}