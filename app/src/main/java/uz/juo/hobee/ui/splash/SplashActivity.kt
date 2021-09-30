package uz.juo.hobee.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import uz.juo.hobee.MainActivity
import uz.juo.hobee.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        var handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            var intent = Intent(this, MainActivity::class.java)
            this.finish()
            startActivity(intent)
        }, 2000)
    }
}