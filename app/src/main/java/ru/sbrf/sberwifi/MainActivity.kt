package ru.sbrf.sberwifi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            val selectedFragment: Fragment? = when (it.itemId) {
                R.id.navigation_menu -> WiFi.newInstance("","")
                R.id.navigation_report -> Report.newInstance("","")
                //R.id.navigation_settings -> selectedFragment = SettingsFragment.newInstance()
                else -> throw UnsupportedOperationException("Невозможно выбрать фрагмент для отображения")
            }

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, selectedFragment!!)
            transaction.commit()
            true
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, WiFi.newInstance("",""))
        transaction.commit()
    }
}