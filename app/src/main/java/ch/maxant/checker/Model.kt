package ch.maxant.checker

import android.util.Log
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import ch.maxant.checker.SiteCheckerWorker.TAG
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

object Model {
    private val queries = Collections.synchronizedList(ArrayList<Query>())

    internal fun addQuery(predictedStart: LocalDateTime): Query {
        val q = Query(predictedStart)
        queries.add(q)
        if(queries.size > 100) {
            queries.removeAt(0) // remove oldest
        }
        return q
    }

    @JvmStatic
    fun queries(): List<Query> { return queries }

    @JvmStatic
    fun clearQueries() {
        queries.clear()
    }
}

data class Query(val predictedStart: LocalDateTime, var start: LocalDateTime? = null, var end: LocalDateTime? = null, var result: String? = null, var state: Boolean? = null) {
    internal fun setActualStart() {
        this.start = LocalDateTime.now()
    }

    internal fun setSuccess(result: String) {
        this.result = result
        this.state = true
        this.end = LocalDateTime.now()
    }
    internal fun setFailed(result: String) {
        this.result = result
        this.state = false
        this.end = LocalDateTime.now()
    }

    override fun toString(): String {
        return if(start == null) {
            predictedStart.format(DateTimeFormatter.ISO_DATE_TIME).substring(0, 19) + ": <- will start then..."
        } else {
            start?.format(DateTimeFormatter.ISO_DATE_TIME)?.substring(0, 19) + ": " +
                    if(null == state) "Pending..." else
                        (   if(true == state) "Success in " + Duration.between(start, end).toMillis() + "ms"
                        else "*** FAILED *** $result")
        }
    }
}

abstract class ModelListener {
    abstract fun onAddQuery(query: Query)
    abstract fun onStartQuery(query: Query)
    abstract fun onSuccessfulQuery(query: Query)
    abstract fun onFailedQuery(query: Query)
    abstract fun onClearQueries()

    abstract fun getId(): String

    override fun equals(other: Any?)
            = (other is ModelListener)
            && getId() == other.getId()

    override fun hashCode(): Int {
        return Objects.hashCode(getId())
    }

}

object Controller {
    private val modelListeners = HashSet<ModelListener>()

    @JvmStatic
    fun addListener(listener: ModelListener) {
        modelListeners.add(listener)
    }

    @JvmStatic
    fun removeListener(listener: ModelListener) {
        modelListeners.remove(listener)
    }

    /** adds a new query to the model and informs all listeners */
    @JvmStatic
    fun addQuery(predictedStart: LocalDateTime): Query {
        val query = Model.addQuery(predictedStart)
        tryCallingListeners { it.onAddQuery(query)}
        return query
    }

    @JvmStatic
    fun updateQuerySuccess(query: Query, result: String) {
        query.setSuccess(result)
        tryCallingListeners { it.onSuccessfulQuery(query)}
    }

    @JvmStatic
    fun updateQueryFailed(query: Query, result: String) {
        query.setFailed(result)
        tryCallingListeners { it.onFailedQuery(query)}
    }

    @JvmStatic
    fun clearQueries() {
        Model.clearQueries()
        tryCallingListeners { it.onClearQueries()}
    }

    @JvmStatic
    fun getLatestWaitingToStartQueryAndMarkAsStarted(): Query {
        val lastUnstartedQuery = Model.queries().lastOrNull { it.start == null } ?: addQuery(LocalDateTime.now())
        lastUnstartedQuery.setActualStart()
        tryCallingListeners { it.onStartQuery(lastUnstartedQuery)}
        return lastUnstartedQuery
    }

    private fun tryCallingListeners(f: (ModelListener) -> Unit) {
        try {
            modelListeners.forEach { f(it) }
        } catch (ex: Exception) {
            Log.e(TAG, "YYY error calling listener: " + ex.message, ex)
        }
    }

}
