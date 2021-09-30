package uz.juo.hobee.utils

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import uz.juo.hobee.models.ItemMedIdPrice
import uz.juo.hobee.room.AppDataBase
import uz.juo.hobee.room.entity.FavoritesEntity
import java.util.*
import kotlin.collections.ArrayList
import androidx.core.content.ContextCompat.startActivity


class Functions {

    fun checkPermission(context: Context): Boolean {
        var a = false
        Dexter.withContext(context)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            a = true
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    a = false
                    token?.continuePermissionRequest()
                }
            })
            .withErrorListener {
                a = false
                return@withErrorListener
            }
            .check()
        return a
    }

    fun getLocationName(context: Context, latitude: Double, longitude: Double): String {
        var name = "Incorrect location"
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val listAddresses: List<Address>? =
                geocoder.getFromLocation(latitude, longitude, 1)
            if (null != listAddresses && listAddresses.isNotEmpty()) {
                name = listAddresses[0].getAddressLine(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return name
        }
        return name
    }

    fun kmConvertor(m: Double?): String {
        if (m == null) {
            return "Нет"
        }
        try {
            var distance = m / 1000
            var km =
                "${distance.toString().subSequence(0, distance.toString().indexOf(".") + 2)} km"
            if (km=="0.0"){
                return "Нет"
            }
            return km
        } catch (e: Exception) {
            return "Error"
        }

    }

    fun getWorkingTime(statTime: String, endTIme: String): String {
        return if (statTime == "00:00") {
            "Круглосуточно"
        } else {
            "${statTime}-${endTIme}"
        }
    }

    fun getFavorite(context: Context): ArrayList<FavoritesEntity> {
        var db = AppDataBase.getInstance(context)
        return db.dao().getAll() as ArrayList<FavoritesEntity>
    }

}