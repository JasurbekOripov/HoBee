package uz.juo.hobee.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NetworkChangeListener : BroadcastReceiver() {
    private lateinit var internetConnectionHelper: NetworkHelper
    override fun onReceive(context: Context?, intent: Intent?) {
        internetConnectionHelper = NetworkHelper(context!!)
        if (!internetConnectionHelper.isNetworkConnected()) {
//            BranchsFragment.showLoading()
//            BranchsFragment.hideLoading()
//            val dialog = AlertDialog.Builder(context)
//            dialog.setTitle("Error")
//            dialog.setMessage("Please check internet connection or turn on wifi")
//            dialog.setPositiveButton("OK",
//                DialogInterface.OnClickListener { dialog, id ->
//                    onReceive(context, intent)
//                })
//            val alertDialog = dialog.create()
//            alertDialog.show()
        } else {
//            BranchsFragment().showLoading()
        }
    }

}