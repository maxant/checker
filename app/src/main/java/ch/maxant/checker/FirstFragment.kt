package ch.maxant.checker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ch.maxant.checker.databinding.FragmentFirstBinding
import java.util.stream.Collectors
import java.util.stream.Collectors.joining

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

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

        override fun onCertificatesChanged(certificateModel: CertificateModel) {
            // noop
        }

        override fun getId() = "FirstFragmentListener"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        Controller.addListener(modelListener)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        draw()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_LogFragment)
        }

        mTextView = view.findViewById(R.id.textview_logs)
        draw()
    }

    private fun draw() {
        mTextView?.post { // post, as that is similar to runOnUiThread - and we may be coming in from somewhere dodgy
            if(Model.queries().isEmpty()) {
                mTextView?.text = "no queries to show"
            } else {
                mTextView?.text = "Queried " + Model.queries().size + " times: \r\n" +
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