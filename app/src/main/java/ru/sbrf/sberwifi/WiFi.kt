package ru.sbrf.sberwifi

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [WiFi.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [WiFi.newInstance] factory method to
 * create an instance of this fragment.
 */
class WiFi : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var list: ParallaxScrollListView? = null
    private var viewModel: DetectorViewModel? = null

    private lateinit var viewFragment: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        viewFragment = inflater.inflate(R.layout.fragment_wifi, container, false)

        list = viewFragment.findViewById(R.id.layout_listwifi)
        list?.setZoomRatio(ParallaxScrollListView.ZOOM_X2)

        val header = LayoutInflater.from(this.context).inflate(R.layout.listview_header, null)
        val mImageView = header.findViewById(R.id.layout_header_image) as ImageView

        list?.setParallaxImageView(mImageView)
        list?.addHeaderView(header)

        viewModel = ViewModelProviders.of(this).get(DetectorViewModel::class.java)

        viewModel!!.getResultScanLiveData().observe(this, Observer {
            val listWiFi = ArrayList<ItemListWifi>()

            for (item in it) {
                listWiFi.add(ItemListWifi(item.scan.SSID, item.scan.level))
            }

            val adapter = ArrayAdapter(this.context!!,
                    android.R.layout.simple_expandable_list_item_1,
                    listWiFi)

            list?.adapter = adapter
        })

        return viewFragment
    }

    private class ItemListWifi(val SSID: String, val level: Int) {
        override fun toString(): String {
            return String.format("SSID: %s, Level: %sdBm", SSID, level)
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /* if (context is OnFragmentInteractionListener) {
             listener = context
         } else {
             throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
         }*/
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Wifi.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                WiFi().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
