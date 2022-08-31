package ch.maxant.checker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ch.maxant.checker.databinding.FragmentFirstBinding
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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

        mTextView = view.findViewById(R.id.textview_first_start_value)
        draw()
    }

    private fun draw() {
        if(Model.workExecutions.isEmpty()) {
            mTextView?.text = "Created at " + Model.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) +
                                "\r\nno executions yet"
        } else {
            mTextView?.text = "Created at " + Model.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) +
                                "\r\nExecuted " + Model.workExecutions.size + " times: \r\n" +
                    Model.workExecutions.stream()
                        .sorted(Comparator.reverseOrder())
                        .map { it.format(DateTimeFormatter.ISO_DATE_TIME) + "\r\n" }
                        .collect(Collectors.joining(","))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}