package ch.maxant.checker

import android.util.Log
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import ch.maxant.checker.SiteCheckerWorker.TAG
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

object Model {
    private val queries = Collections.synchronizedList(ArrayList<Query>())

    private var certificateModel: CertificateModel = CertificateModel("not yet initialised")

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

    internal fun setCertificateModel(certificateModel: CertificateModel) {
        this.certificateModel = certificateModel
    }

    @JvmStatic
    fun getCertificateModel() = certificateModel

}

data class Certificate(val domain: String, val expiry: LocalDate, val validity: String, val warnings: String?)

data class CertificateModel(val lastUpdated: String?, val certificates: List<Certificate>?, val warnings: String?, val errors: String?) {
    constructor(errors: String) : this(null, null, null, errors)
    constructor(lastUpdated: String, certificates: List<Certificate>) : this(lastUpdated, certificates, null, null)
    constructor(lastUpdated: String, certificates: List<Certificate>, warnings: String) : this(lastUpdated, certificates, warnings, null)
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
    abstract fun onCertificatesChanged(certificateModel: CertificateModel)

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
        tryCallingListeners { it.onAddQuery(query) }
        return query
    }

    @JvmStatic
    fun updateQuerySuccess(query: Query, result: String) {
        query.setSuccess(result)
        tryCallingListeners { it.onSuccessfulQuery(query) }
    }

    @JvmStatic
    fun updateQueryFailed(query: Query, result: String) {
        query.setFailed(result)
        tryCallingListeners { it.onFailedQuery(query) }
    }

    @JvmStatic
    fun clearQueries() {
        Model.clearQueries()
        tryCallingListeners { it.onClearQueries() }
    }

    @JvmStatic
    fun getLatestWaitingToStartQueryAndMarkAsStarted(): Query {
        val lastUnstartedQuery = Model.queries().lastOrNull { it.start == null } ?: addQuery(LocalDateTime.now())
        lastUnstartedQuery.setActualStart()
        tryCallingListeners { it.onStartQuery(lastUnstartedQuery) }
        return lastUnstartedQuery
    }

    @JvmStatic
    fun certificatesChanged(lastUpdated: String, certificates: List<Certificate>) {
        Model.setCertificateModel(CertificateModel(lastUpdated, certificates))
        tryCallingListeners { it.onCertificatesChanged(Model.getCertificateModel()) }
    }

    @JvmStatic
    fun certificatesChanged(lastUpdated: String, certificates: List<Certificate>, warnings: String) {
        Model.setCertificateModel(CertificateModel(lastUpdated, certificates, warnings))
        tryCallingListeners { it.onCertificatesChanged(Model.getCertificateModel()) }
    }

    @JvmStatic
    fun certificatesChanged(certificatesError: String) {
        Model.setCertificateModel(CertificateModel(certificatesError))
        tryCallingListeners { it.onCertificatesChanged(Model.getCertificateModel()) }
    }

    private fun tryCallingListeners(f: (ModelListener) -> Unit) {
        try {
            modelListeners.forEach { f(it) }
        } catch (ex: Exception) {
            Log.e(TAG, "YYY error calling listener: " + ex.message, ex)
        }
    }

}
