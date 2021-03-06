package uz.juo.hobee

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import androidx.navigation.NavController
import androidx.navigation.findNavController
import uz.juo.hobee.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.my_nav_host_fragment)
        setupSmoothBottomMenu()
        binding.bottomNavBar.setOnItemSelectedListener {
            bottomCLick(it)
        }
        window.navigationBarColor = Color.parseColor("#1961CD")
    }


    fun hideBottomBar() {
        try {
            binding.bottomNavBar.visibility = View.GONE
        } catch (e: Exception) {
//            binding.bottomNavBar.visibility = View.GONE
        }
    }

    fun showBottomBar() {
        try {
            binding.bottomNavBar.visibility = View.VISIBLE
        } catch (e: Exception) {
//            binding.bottomNavBar.visibility = View.VISIBLE
        }
    }

    private fun bottomCLick(it: Int) {
        when (it) {
            0 -> {
                binding.bottomNavBar.itemActiveIndex=0
                while (R.id.homeFragment12 != navController.currentDestination?.id && navController.currentDestination?.id != null) {
                    navController.popBackStack()
                }
            }
            1 -> {
                if (R.id.favoriteFragment1 != navController.currentDestination?.id) {
                    while (R.id.homeFragment12 != navController.currentDestination?.id && navController.currentDestination?.id != null) {
                        navController.popBackStack()
                    }
                    navController.navigate(R.id.favoriteFragment1)
                }
            }
            2 -> {
                if (R.id.branchsFragment != navController.currentDestination?.id) {
                    while (R.id.homeFragment12 != navController.currentDestination?.id && navController.currentDestination?.id != null) {
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
        try {
//            MapKitFactory.getInstance().onStop()
        } catch (e: Exception) {

        }

    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        super.onBackPressed()
        if (navController.currentDestination?.id == R.id.homeFragment12) {
            binding.bottomNavBar.itemActiveIndex = 0
        }

    }

}
