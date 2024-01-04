package ch.maxant.checker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ch.maxant.checker.databinding.FragmentCertsBinding
import java.time.LocalDate

class CertsFragment : Fragment() {

    private var _binding: FragmentCertsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private var mTextView: TextView? = null

    private val modelListener = object : ModelListener() {
        override fun onAddQuery(query: Query) {
            // noop
        }

        override fun onStartQuery(query: Query) {
            // noop
        }

        override fun onSuccessfulQuery(query: Query) {
            // noop
        }

        override fun onFailedQuery(query: Query) {
            // noop
        }

        override fun onClearQueries() {
            // noop
        }

        override fun onCertificatesChanged(certificateModel: CertificateModel) {
            draw(certificateModel)
        }

        override fun getId() = "CertsFragmentListener"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCertsBinding.inflate(inflater, container, false)
        Controller.addListener(modelListener)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        draw(Model.getCertificateModel())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_CertsFragment_to_LogFragment)
        }
        binding.buttonForward.setOnClickListener {
            findNavController().navigate(R.id.action_CertsFragment_to_Certs2Fragment)
        }

        mTextView = view.findViewById(R.id.certsview)
        draw(Model.getCertificateModel())
    }

    private fun draw(certificateModel: CertificateModel) {
        var text = "getting ready..."
        if(certificateModel.errors != null) {
            text = "Errors fetching certificates: " + Model.getCertificateModel().errors
        } else {
            if(Model.getCertificateModel().certificates != null) {
                text = ""
                Model.getCertificateModel().certificates?.forEach { certificate ->
                    text += "\r\n\r\n" + certificate.domain + " Valid " + certificate.validity
                    if(certificate.expiry.isBefore(LocalDate.now().plusDays(20))) {
                        text += " EXPIRES SOON!!"
                    }

                    if(certificate.warnings != null) {
                        text = " Certificate warnings (" + certificate.domain + "): " + certificate.warnings + "!!\r\n\r\n" + text
                    }
                }
            }

            if(Model.getCertificateModel().warnings != null) {
                text = "General warnings: " + Model.getCertificateModel().warnings + "\r\n\r\n" + text
            }
        }

        mTextView?.post { // post, as that is similar to runOnUiThread - and we may be coming in from somewhere dodgy
            mTextView?.text = text
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Controller.removeListener(modelListener)
    }
}