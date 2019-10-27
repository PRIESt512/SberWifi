package ru.sbrf.sberwifi.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.agik.AGIKSwipeButton.Controller.OnSwipeCompleteListener
import com.agik.AGIKSwipeButton.View.Swipe_Button_View
import com.agrawalsuneet.squareloaderspack.loaders.WaveLoader
import com.github.hamzaahmedkhan.spinnerdialog.OnSpinnerOKPressedListener
import com.github.hamzaahmedkhan.spinnerdialog.SpinnerDialogFragment
import com.github.hamzaahmedkhan.spinnerdialog.SpinnerModel
import com.jayway.jsonpath.JsonPath
import com.savvyapps.togglebuttonlayout.ToggleButtonLayout
import kotlinx.coroutines.*
import org.apache.commons.lang3.StringUtils
import ru.sbrf.sberwifi.MainContext
import ru.sbrf.sberwifi.R
import ru.sbrf.sberwifi.http.report.iperf.PostOfIperfUseCase
import ru.sbrf.sberwifi.wifi.iperf.Info
import ru.sbrf.sberwifi.wifi.iperf.IperfReport
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class IperfFragment : Fragment(), CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    var countDownLatch = CountDownLatch(2)

    var uuid: UUID = UUID.randomUUID()

    lateinit var animationTest: WaveLoader

    lateinit var resultTest: TextView

    var duration: Int = 5

    var streams: Int = 1

    var inputPort: String? = null

    var inputHost: String? = null

    var startStopButton: Swipe_Button_View? = null

    lateinit var currentView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        initTempPath()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        currentView = inflater.inflate(R.layout.fragment_iperf, container, false)

        animationTest = currentView.findViewById(R.id.animationTest)
        animationTest.visibility = View.GONE
        val inputPortText = currentView.findViewById<EditText>(R.id.inputPort)
        val inputHostText = currentView.findViewById<EditText>(R.id.inputHost)
        resultTest = currentView.findViewById(R.id.resultTest)
        resultTest.visibility = View.GONE

        val switch = view?.findViewById<Switch>(R.id.switchReverse)

        /*switch?.setOnClickListener { _, isChecked ->
            if (isChecked) {

            } else {

            }
        }
*/
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

        val durationsIperfTest: ArrayList<SpinnerModel> = ArrayList()

        for (i in 5..60 step 5) {
            durationsIperfTest.add(SpinnerModel("$i секунд"))
        }

        val spinnerStreams =
                SpinnerDialogFragment.newInstance(
                        "Spinner Dialog", streamsIperfTest,
                        object : OnSpinnerOKPressedListener {
                            override fun onItemSelect(data: SpinnerModel, selectedPosition: Int) {
                                Toast.makeText(currentView.context, data.text, Toast.LENGTH_LONG).show()
                                streams = selectedPosition + 1
                            }

                        }, 0
                )

        val spinnerDurations = SpinnerDialogFragment.newInstance(
                "Spinner Dialog", durationsIperfTest,
                object : OnSpinnerOKPressedListener {
                    override fun onItemSelect(data: SpinnerModel, selectedPosition: Int) {
                        duration = selectedPosition * 5 + 5
                        Toast.makeText(currentView.context, data.text, Toast.LENGTH_LONG).show()
                    }
                }, 0
        )

        val toggle = currentView.findViewById<ToggleButtonLayout>(R.id.toggleButtonLayoutTextIperf)
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

        startStopButton = currentView.findViewById<Swipe_Button_View>(R.id.start_stop)
        startStopButton?.setOnSwipeCompleteListener_forward_reverse(object : OnSwipeCompleteListener {
            override fun onSwipe_Forward(swipe_button_view: Swipe_Button_View?) {
                if (inputHost == null || inputPort == null) {
                    Toast.makeText(currentView.context, "Введите host и port удаленной машины", Toast.LENGTH_SHORT).show()
                    startStopButton?.setreverse_0()
                    return
                }
                launch {
                    try {
                        Toast.makeText(currentView.context, "Запущен тест: длительность - $duration сек, потоков - $streams", Toast.LENGTH_SHORT).show()
                        iperfStartView(currentView)
                        startAsync(inputHost!!, inputPort!!.toInt(), duration, streams, false)
                        kotlinx.coroutines.delay(1500)
                        startAsync(inputHost!!, inputPort!!.toInt(), duration, streams, true)
                    } catch (ex: java.lang.Exception) {
                        iperfStopView(currentView, View.GONE)
                        startStopButton?.setreverse_0()
                        startStopButton?.swipe_reverse = false
                        Toast.makeText(currentView.context, "Ошибка iperf $ex", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onSwipe_Reverse(swipe_button_view: Swipe_Button_View?) {
                iperfStopView(currentView, View.GONE)
            }
        })
        return currentView
    }

    private fun iperfStartView(view: View) {
        resultTest.visibility = View.GONE
        animationTest.visibility = View.VISIBLE
        startStopButton?.setText("Остановить тест iperf")
        startStopButton?.setThumbBackgroundColor(ContextCompat.getColor(view.context, R.color.material_red400))
        startStopButton?.setSwipeBackgroundColor(ContextCompat.getColor(view.context, R.color.material_red500))
    }

    private fun iperfStopView(view: View, resultTestVisibility: Int) {
        resultTest.visibility = resultTestVisibility
        animationTest.visibility = View.GONE
        startStopButton?.setText("Запустить тест iperf")
        startStopButton?.setThumbBackgroundColor(ContextCompat.getColor(view.context, R.color.material_green400))
        startStopButton?.setSwipeBackgroundColor(ContextCompat.getColor(view.context, R.color.material_green500))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    suspend fun startAsync(host: String, port: Int, duration: Int, streams: Int, reverse: Boolean) = withContext(Dispatchers.IO) {
        start(host, port, duration, streams, reverse)
    }

    suspend fun startServerAsync(host: String, port: Int, duration: Int, streams: Int, reverse: Boolean) = withContext(Dispatchers.IO) {
        startServer(host, port, duration, streams, reverse)
    }

    external fun start(host: String, port: Int, duration: Int, streams: Int, reverse: Boolean)

    external fun startServer(host: String, port: Int, duration: Int, streams: Int, reverse: Boolean)

    /**
     * Функция для вызова с нативной стороны JNI для корректной установки
     * путе для кешированных файлов
     */
    external fun initTempPath()

    public fun getTempPath(): String? {
        return this.context?.cacheDir?.absolutePath
    }

    var count: Int = 0

    var reports: MutableList<String> = ArrayList(2)

    /**
     * Функция для вызова с нативной стороны и передачи результатов тестирования в код Java
     */
    public fun reportCallback(report: String?) {
        try {
            var secondsTest: Double = 0.0
            var bytesTest: Int = 0
            var speedTest: Double = 0.0
            var cpuUtilTest: Double = 0.0
            var result: String = StringUtils.EMPTY
            if (count == 0) {
                secondsTest = JsonPath.read<Double>(report, "$.end.sum_sent.seconds")
                bytesTest = JsonPath.read<Int>(report, "$.end.sum_sent.bytes")
                speedTest = JsonPath.read<Double>(report, "$.end.sum_sent.bits_per_second")
                cpuUtilTest = JsonPath.read<Double>(report, "$.end.cpu_utilization_percent.host_total")
                result = "Время тестирования $secondsTest сек, \n" +
                        "Отправлено МБ: ${bytesTest / 1000000} \n" +
                        //"Скорость передачи данных МБ/с ${speedTest / 800000000} \n" +
                        "Нагрузка на процессор: $cpuUtilTest% \n"
                reports.add(result)

                count++
            } else {
                //secondsTest = JsonPath.read<Double>(report, "$.end.sum_sent.seconds")
                bytesTest = JsonPath.read<Int>(report, "$.end.sum_received.bytes")
                speedTest = JsonPath.read<Double>(report, "$.end.sum_received.bits_per_second")
                cpuUtilTest = JsonPath.read<Double>(report, "$.end.cpu_utilization_percent.host_total")
                result =
                        "Принято МБ: ${bytesTest / 1000000} \n" +
                                //"Скорость передачи данных МБ/с ${speedTest / 800000000} \n" +
                                "Нагрузка на процессор: $cpuUtilTest% \n"
                reports.add(result)

                count++
            }

            //countDownLatch.countDown()
            //countDownLatch.await()

            launch {
                if (report != null) {
                    val result = sendReport(report)
                    if (result.status == 200) {
                        Toast.makeText(this@IperfFragment.context, "Отчет отправлен успешно", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@IperfFragment.context, "Отчет не отправлен; Статус ${result.status}", Toast.LENGTH_SHORT).show()
                    }
                }
                if (count == 2) {
                    iperfStopView(currentView, View.VISIBLE)
                    val rep = reports[0] + reports[1]
                    resultTest.text = rep
                    startStopButton?.setreverse_0()

                    uuid = UUID.randomUUID()
                    count = 0
                }
            }
        } catch (ex: Exception) {
            Log.e("Iperf", ex.toString())
        } finally {
            countDownLatch = CountDownLatch(2)
        }
    }

    private suspend fun sendReport(report: String): PostOfIperfUseCase.Result = withContext(Dispatchers.IO) {
        val iperfCase = PostOfIperfUseCase()
        val ssid = MainContext.INSTANCE.wiFiData.wiFiConnection.ssid
        val bssid = MainContext.INSTANCE.wiFiData.wiFiConnection.bssid

        val result = iperfCase.doWork(IperfReport(Info(uuid.toString(), bssid, ssid), report).toString())
        if (result.status == 200)
            return@withContext result
        else {
            Log.d("Report", "request status ${result.status}")
            return@withContext result
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                IperfFragment().apply {
                    arguments = Bundle().apply {
                    }
                }

        init {//без этой либы iperf не заведется
            System.loadLibrary("iperf")
        }
    }
}
