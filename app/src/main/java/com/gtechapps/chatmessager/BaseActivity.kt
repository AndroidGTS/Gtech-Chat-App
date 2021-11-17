package com.gtechapps.chatmessager

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BaseActivity : AppCompatActivity() {
    val TAG = this.javaClass.simpleName
    var dialog: Dialog? = null
    private val progressDialog: ProgressDialog? = null

    //    LottieAnimationView lottieAnimationView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog = Dialog(this, R.style.DimDisabled)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(false)
    }

    fun showLoader(msg: String?) {
        try {
            dialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showLoader() {
        try {
            dialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideLoader() {
        if (dialog != null) {
            try {
                dialog!!.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun showToast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun showErrorState() {}
    fun showEmptyState() {}

    //    public void showSnackbar(String msg){
    //        Snackbar snackbar = Snackbar
    //                .make(getCurrentFocus(), "Try again!", Snackbar.LENGTH_LONG)
    //                .setAction("RETRY", new View.OnClickListener() {
    //                    @Override
    //                    public void onClick(View view) {
    //                    }
    //                });
    //        snackbar.setActionTextColor(getResources().getColor(R.color.outreach_background_color));
    //        View sbView = snackbar.getView();
    //        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
    //        textView.setTextColor(Color.YELLOW);
    //        snackbar.show();
    //    }

    fun showAlert(title: String?, msg: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setCancelable(false)
        builder.setPositiveButton("Ok") { dialogInterface, i ->
            dialogInterface.dismiss()
            finish()
        }
        val alertDialog = builder.create()
        alertDialog.setTitle(title)
        alertDialog.show()
    }
}