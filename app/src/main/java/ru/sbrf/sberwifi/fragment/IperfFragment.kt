package ru.sbrf.sberwifi.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.agik.AGIKSwipeButton.Controller.OnSwipeCompleteListener
import com.agik.AGIKSwipeButton.View.Swipe_Button_View
import com.github.hamzaahmedkhan.spinnerdialog.OnSpinnerOKPressedListener
import com.github.hamzaahmedkhan.spinnerdialog.SpinnerDialogFragment
import com.github.hamzaahmedkhan.spinnerdialog.SpinnerModel
import com.savvyapps.togglebuttonlayout.ToggleButtonLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.sbrf.sberwifi.R

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [IperfFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [IperfFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IperfFragment : Fragment() {

    var duration: Int? = null

    var streams: Int? = null

    var inputPort: String? = null

    var inputHost: String? = null

    var startStopButton: Swipe_Button_View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_iperf, container, false)

        val inputPortText = view.findViewById<EditText>(R.id.inputPort)
        val inputHostText = view.findViewById<EditText>(R.id.inputHost)

        inputPortText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.isNotEmpty()!!) {
                    inputPort = s.toString()
                }
            }
        })

        inputHostText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.isNotEmpty()!!) {
                    inputHost = s.toString()
                }
            }
        })

        val streamsIperfTest: ArrayList<SpinnerModel> = ArrayList()

        for (i in 1..8) {
            streamsIperfTest.add(SpinnerModel("$i поток(-ов)"))
        }

        val durationsIpefTest: ArrayList<SpinnerModel> = ArrayList()

        for (i in 5..60 step 5) {
            durationsIpefTest.add(SpinnerModel("$i секунд"))
        }

        val spinnerStreams =
                SpinnerDialogFragment.newInstance(
                        "Spinner Dialog", streamsIperfTest,
                        object : OnSpinnerOKPressedListener {
                            override fun onItemSelect(data: SpinnerModel, selectedPosition: Int) {
                                Toast.makeText(view.context, data.text, Toast.LENGTH_LONG).show()
                                streams = selectedPosition + 1
                            }

                        }, 0
                )

        val spinnerDurations = SpinnerDialogFragment.newInstance(
                "Spinner Dialog", durationsIpefTest,
                object : OnSpinnerOKPressedListener {
                    override fun onItemSelect(data: SpinnerModel, selectedPosition: Int) {
                        duration = selectedPosition * 5 + 5
                        Toast.makeText(view.context, data.text, Toast.LENGTH_LONG).show()
                    }

                }, 0
        )

        val toggle = view.findViewById<ToggleButtonLayout>(R.id.toggleButtonLayoutTextIperf)
        toggle?.onToggledListener = { _, toggle, selected ->
            when (toggle.id) {
                R.id.toggle_left -> {
                    activity?.supportFragmentManager?.let { spinnerDurations.show(it, "Длительность") }
                }
                R.id.toggle_right -> {
                    activity?.supportFragmentManager?.let { spinnerStreams.show(it, "Число потоков") }
                }
            }
        }

        startStopButton = view.findViewById<Swipe_Button_View>(R.id.start_stop)
        startStopButton?.setOnSwipeCompleteListener_forward_reverse(object : OnSwipeCompleteListener {
            override fun onSwipe_Forward(swipe_button_view: Swipe_Button_View?) {
                /* GlobalScope.launch {
                     initTempPath()
                     startAsync(inputHost!!, inputPort!!.toInt(), 5, 1)
                 }*/
                Thread {
                    initTempPath()
                    start(inputHost!!, inputPort!!.toInt(), 5, 1)
                }.start()

                startStopButton?.setText("Остановить тест iperf")
                startStopButton?.setThumbBackgroundColor(ContextCompat.getColor(view.context, R.color.material_red400))
                startStopButton?.setSwipeBackgroundColor(ContextCompat.getColor(view.context, R.color.material_red500))
            }

            override fun onSwipe_Reverse(swipe_button_view: Swipe_Button_View?) {
                startStopButton?.setText("Запустить тест iperf")
                startStopButton?.setThumbBackgroundColor(ContextCompat.getColor(view.context, R.color.material_green400))
                startStopButton?.setSwipeBackgroundColor(ContextCompat.getColor(view.context, R.color.material_green500))
            }
        })
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    suspend fun startAsync(host: String, port: Int, duration: Int, streams: Int) = withContext(Dispatchers.IO) {
        start(host, port, duration, streams)
    }

    external fun start(host: String, port: Int, duration: Int, streams: Int)

    external fun initTempPath()

    public fun getTempPath(): String? {
        return this.context?.cacheDir?.absolutePath
    }

    public fun reportCallback(report: String?) {
        val str = report
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment IperfFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                IperfFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
