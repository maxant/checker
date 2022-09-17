package ch.maxant.checker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ch.maxant.checker.databinding.FragmentCerts2Binding
import java.time.LocalDate


class CertsFragment2 : Fragment() {

    private var _binding: FragmentCerts2Binding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private var mWebView: WebView? = null

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
        _binding = FragmentCerts2Binding.inflate(inflater, container, false)
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
            findNavController().navigate(R.id.action_CertsFragment2_to_CertsFragment)
        }
        binding.buttonGotoAbstratium.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://abstratium.dev")))
        }

        mWebView = view.findViewById(R.id.certs2view)
        draw(Model.getCertificateModel())
    }

    // https://developer.android.com/develop/ui/views/layout/webapps/webview
    private fun draw(certificateModel: CertificateModel) {
        var text = StringBuilder()
        if(certificateModel.errors != null) {
            text.append("Errors fetching certificates: " + Model.getCertificateModel().errors)
        } else {
            if(Model.getCertificateModel().certificates != null) {
                Model.getCertificateModel().certificates?.forEach { certificate ->
                    var colour = "#90EE90";
                    var expiresSoon = "";
                    if(certificate.expiry.isBefore(LocalDate.now().plusDays(20))) {
                        expiresSoon = " EXPIRES SOON!!"
                        colour = "#FFA500";
                    }
                    text.append("<div style='margin: 3px; border: 1px solid black; background-color: " + colour + ";'>")
                    text.append(certificate.domain + " valid " + certificate.validity + expiresSoon)
                    if(certificate.warnings != null) {
                        text.append("<div style='margin: 3px; background-color: yellow;'>Certificate warnings: " + certificate.warnings + "</div>")
                    }
                    text.append("</div>")
                }
            }
            if(Model.getCertificateModel().warnings != null) {
                text = StringBuilder("<div style='margin: 3px; border: 1px solid black; background-color: red;'>General warnings: "
                        + Model.getCertificateModel().warnings + "</div>").append(text)
            }
        }

        mWebView?.post { // post, as that is similar to runOnUiThread - and we may be coming in from somewhere dodgy
            // Create an unencoded HTML string
            // then convert the unencoded HTML string into bytes, encode
            // it with Base64, and load the data.
            val unencodedHtml =
                "<html><body>" + text + "</body></html>";
            val encodedHtml = Base64.encodeToString(unencodedHtml.toByteArray(), Base64.NO_PADDING)
            mWebView?.loadData(encodedHtml, "text/html", "base64")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Controller.removeListener(modelListener)
    }
}