package com.gtechapps.chatmessager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    public final String TAG = this.getClass().getSimpleName();
    public Dialog dialog;
    private ProgressDialog progressDialog;
//    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new Dialog(this, R.style.DimDisabled);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);


    }

    public void showLoader(String msg) {
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLoader() {
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideLoader() {
        if (dialog != null) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    public void showErrorState() {

    }

    public void showEmptyState() {

    }


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

    public void showAlert(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle(title);
        alertDialog.show();
    }


}