package ch.maxant.checker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import ch.maxant.checker.databinding.FragmentLogBinding
import java.util.stream.Collectors.joining

class LogFragment : Fragment() {

    private var _binding: FragmentLogBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private var mTextView: TextView? = null

    private val modelListener = object : ModelListener() {
        override fun onAddQuery(query: Query) {
            draw()
        }

        override fun onStartQuery(query: Query) {
            draw()
        }

        override fun onSuccessfulQuery(query: Query) {
            draw()
        }

        override fun onFailedQuery(query: Query) {
            draw()
        }

        override fun onClearQueries() {
            draw()
        }

        override fun getId() = "LogFragmentListener"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogBinding.inflate(inflater, container, false)
        Controller.addListener(modelListener)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        draw()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonClear.setOnClickListener {
            Controller.clearQueries()
        }

        mTextView = view.findViewById(R.id.logview)
        draw()
    }

    private fun draw() {
        mTextView?.post { // post, as that is similar to runOnUiThread - and we may be coming in from somewhere dodgy
            if(Model.queries().isEmpty()) {
                mTextView?.text = "no queries to show"
            } else {
                mTextView?.text = "Queried ${Model.queries().size} times: \r\n" +
                        Model.queries().stream()
                            .sorted { a, b -> b.predictedStart.compareTo(a.predictedStart) } // reversed coz we start with b
                            .map { it.toString() }
                            .collect(joining("\r\n"))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Controller.removeListener(modelListener)
    }
}