package uz.juo.hobee

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.yandex.mapkit.MapKitFactory
import uz.juo.hobee.databinding.ActivityMainBinding
import uz.juo.hobee.utils.SharedPreference

class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.my_nav_host_fragment)
        setupSmoothBottomMenu()
        navController.navigate(R.id.homeFragment)
        binding.bottomNavBar.setOnItemSelectedListener {
            bottomCLick(it)
        }
        window.navigationBarColor = Color.parseColor("#1961CD")
    }

    fun hideBottomBar() {
        binding.bottomNavBar.visibility = View.GONE
    }

    fun showBottomBar() {
        binding.bottomNavBar.visibility = View.VISIBLE
    }

    private fun bottomCLick(it: Int) {
        when (it) {
            0 -> {
                while (R.id.homeFragment != navController.currentDestination?.id && navController.currentDestination?.id != null) {
                    navController.popBackStack()
                }
            }
            1 -> {
                if (R.id.favoriteFragment1 != navController.currentDestination?.id) {
                    while (R.id.homeFragment != navController.currentDestination?.id && navController.currentDestination?.id != null) {
                        navController.popBackStack()
                    }
                    navController.navigate(R.id.favoriteFragment1)
                }
            }
            2 -> {
                if (R.id.branchsFragment != navController.currentDestination?.id) {
                    while (R.id.homeFragment != navController.currentDestination?.id && navController.currentDestination?.id != null) {
                        navController.popBackStack()
                    }
                    navController.navigate(R.id.branchFragment)
                }
            }
        }
    }

    private fun setupSmoothBottomMenu() {
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bottom_nav)
        val menu = popupMenu.menu
        binding.bottomNavBar.setupWithNavController(menu, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_nav, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        MapKitFactory.getInstance().onStop()
    }
    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        super.onBackPressed()
//        if (navController.previousBackStackEntry?.destination?.id == R.id.homeFragment) {
//            finish()
//        } else
        if (navController.currentDestination?.id == R.id.homeFragment) {
            binding.bottomNavBar.itemActiveIndex = 0
        }

    }

}
