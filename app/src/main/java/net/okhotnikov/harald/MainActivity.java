package net.okhotnikov.harald;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import net.okhotnikov.harald.model.BluetoothState;
import net.okhotnikov.harald.protocols.BluetoothStateListener;
import net.okhotnikov.harald.service.AsyncService;
import net.okhotnikov.harald.service.bluetooth.BluetoothService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BluetoothStateListener {

    private static final int REQUEST_ENABLE_BT = 1;
    EditText nameEdit;
    ImageButton bluetoothButton, iAmOkButton, iAmInStressButton;
    Button bluetoothTextButton;
    TextView bpmText, stressIndexText;
    BluetoothService bluetoothService;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
            view links
         */
        nameEdit = findViewById(R.id.name_edit);
        bluetoothButton = findViewById(R.id.bluetooth_image);
        bluetoothTextButton = findViewById(R.id.bluetooth_button);
        iAmOkButton = findViewById(R.id.im_ok);
        iAmInStressButton = findViewById(R.id.im_in_stress);
        bpmText = findViewById(R.id.bpm_text);
        stressIndexText = findViewById(R.id.si_text);

        bluetoothButton.setOnClickListener(this);
        bluetoothTextButton.setOnClickListener(this);
        iAmOkButton.setOnClickListener(this);
        iAmInStressButton.setOnClickListener(this);

        nameEdit.setText("Anonymous");

        bluetoothService = new BluetoothService(this);

        AsyncService.instance.setHandler(handler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothService.stop();
    }

    @Override
    public void onClick(View v) {
        System.out.println(v.getId());
    }

    @Override
    public void onStateChanged(BluetoothState state) {
        bluetoothButton.setBackground(
                ContextCompat.getDrawable(this, state.getImage()));
        bluetoothTextButton.setText(getResources().getText(state.getString()));
        bluetoothTextButton.setBackgroundColor(ContextCompat.getColor(this,state.getColor()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ENABLE_BT){
            System.out.println(resultCode);
            bluetoothService.resetAdapter();
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void enableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    @Override
    public void onBpm(int bpm) {
        bpmText.setText(String.valueOf(bpm));
    }
}