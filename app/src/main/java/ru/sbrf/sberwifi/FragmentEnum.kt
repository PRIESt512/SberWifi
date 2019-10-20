package ru.sbrf.sberwifi

enum class FragmentEnum(val idFragment: Int) {
    WiFiFragment(R.id.navigation_wifi),
    ReportFragment(R.id.navigation_report),
    IperfFragment(R.id.navigation_iperf),
    DetailInfoFragment(R.layout.fragment_detail_info)
}