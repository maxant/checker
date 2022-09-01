package ch.maxant.checker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ch.maxant.checker.databinding.FragmentFirstBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors
import kotlin.reflect.KProperty1

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private var mTextView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        draw();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        mTextView = view.findViewById(R.id.textview_logs)
        draw()
    }

    private fun draw() {
        if(Model.queries.isEmpty()) {
            mTextView?.text = "no queries to show"
        } else {
            mTextView?.text = "Queried " + Model.queries.size + " times: \r\n" +
                    Model.queries.stream()
                        .sorted { a, b -> b.start.compareTo(a.start) } // reversed coz we start with b
                        .map { it.toString() + "\r\n" }
                        .collect(Collectors.joining(","))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}