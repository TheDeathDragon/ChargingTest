package com.sunritel.chargingtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView text_view;
    private TextView text_view2;

    private Button button;
    private StringBuilder sb2;

    private int chargingCurrent;
    private BatteryManager mBatteryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text_view = findViewById(R.id.text_view);
        text_view2 = findViewById(R.id.text_view2);
        button = findViewById(R.id.button);
        button.setOnClickListener(this);
        PowerConnectionReceiver powerConnectionReceiver = new PowerConnectionReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(powerConnectionReceiver, intentFilter);
        mBatteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            sb2 = new StringBuilder();
            chargingCurrent = (int) (mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)/1000);
            text_view2.setText("Charging Current: " + chargingCurrent + " mA");
            Log.d("Rin", "chargingCurrent: " + chargingCurrent);
        }
    }

    public class PowerConnectionReceiver extends BroadcastReceiver {

        private int level;
        private int status;
        private boolean isCharging;
        private int chargePlug;
        private boolean usbCharge;
        private boolean acCharge;





        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            StringBuilder sb = new StringBuilder();
            sb2 = new StringBuilder();

            Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            VibrationEffect vibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE);

            if (Intent.ACTION_POWER_CONNECTED.equals(action) || Intent.ACTION_POWER_DISCONNECTED.equals(action) || Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
                chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
                acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
                chargingCurrent = (int) (mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW));

                level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

                sb.append("Charging: ").append(isCharging).append("\n");
                sb.append("USB: ").append(usbCharge).append("\n");
                sb.append("AC: ").append(acCharge).append("\n");
                sb.append("Battery: ").append(level).append("%\n");
                sb.append("Charging Current: ").append(chargingCurrent).append("mA\n");
                Log.d("Rin", "onReceive: "+sb);
            }

            switch (action) {
                case Intent.ACTION_POWER_CONNECTED:
                    sb.append("Status: Power connected");
                    text_view.setText(sb);
                    vb.vibrate(vibrationEffect);
                    sb2.append(text_view2.getText());
                    sb2.append("Power connected").append(" level:").append(level).append(" chargingCurrent:").append(chargingCurrent).append("\n");
                    text_view2.setText(sb2);
                    Toast.makeText(context, "Power connected", Toast.LENGTH_SHORT).show();
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    sb.append("Status: Power disconnected");
                    text_view.setText(sb);
                    vb.vibrate(vibrationEffect);
                    sb2.append(text_view2.getText());
                    sb2.append("Power disconnected").append(" level:").append(level).append(" chargingCurrent:").append(chargingCurrent).append("\n");
                    text_view2.setText(sb2);
                    break;
                case Intent.ACTION_BATTERY_CHANGED:
                    sb.append("Status: Battery changed");
                    text_view.setText(sb);
                    vb.vibrate(vibrationEffect);
                    sb2.append(text_view2.getText());
                    sb2.append("Battery changed").append(" level:").append(level).append(" chargingCurrent:").append(chargingCurrent).append("\n");
                    text_view2.setText(sb2);
                    break;
                default:
                    break;
            }
        }
    }

}