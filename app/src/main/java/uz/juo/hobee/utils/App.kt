package uz.juo.hobee.utils

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("126b6936-92d1-4ebe-88f6-4615dcf93ff6")
    }
}