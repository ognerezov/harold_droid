package net.okhotnikov.harald.view;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import net.okhotnikov.harald.MainActivity;
import net.okhotnikov.harald.model.api.PersonalValue;
import net.okhotnikov.harald.model.web.ApiEndpoint;
import net.okhotnikov.harald.model.web.HttpCall;
import net.okhotnikov.harald.model.web.HttpMethod;
import net.okhotnikov.harald.service.RestService;

public class ReportController implements View.OnClickListener {
    private final ImageButton iAmOk, iAmNotOk;
    private final LinearLayout progressBar;
    private final MainActivity activity;

    public ReportController(ImageButton iAmOk, ImageButton iAmNotOk, LinearLayout progressBar, MainActivity activity) {
        this.iAmOk = iAmOk;
        this.iAmNotOk = iAmNotOk;
        this.progressBar = progressBar;
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == iAmOk.getId()){
            onRequest();
            call(0);
        } else if(v.getId() == iAmNotOk.getId()){
            onRequest();
            call(1);
        }
    }

    private void call(int i) {
        RestService.instance.send(new HttpCall<>(
                ApiEndpoint.STRESS,
                HttpMethod.POST,
                new PersonalValue<Integer>(activity.getPerson(),i),
                aVoid ->{
                    Log.d("Http success ","send stress");
                    activity.getHandler().post(this::onResponse);
                }
                , error -> {
                    activity.getHandler().post(this::onResponse);
                    Log.e("Http error ",error.toString());
            }
        ));
    }

    private void onRequest() {
        iAmNotOk.setVisibility(View.GONE);
        iAmOk.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void onResponse() {
        iAmNotOk.setVisibility(View.VISIBLE);
        iAmOk.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}
