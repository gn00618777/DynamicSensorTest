package com.example.user.berniedynamic;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import java.util.List;
import android.content.Context;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    int CONNECTION_TIMEOUT_SEC = 30;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

                TextView accSensor;
                TextView magSensor;
                TextView gyroSensor;
                TextView fusionSensor;

                TextView dynamicSensorListSize;
                Sensor aSensor;
                Sensor aSensor1;
                Sensor aSensor2;
                Sensor aSensor3;

                Callback mCallback;
                SensorManager sensorManager;
                SensorEventListener aListener;
                SensorEventListener aListener1;
                SensorEventListener aListener2;
                List<Sensor> dynamicSensorList = null;

                accSensor = (TextView)findViewById(R.id.accSensor);
                magSensor = (TextView)findViewById(R.id.magSensor);
                gyroSensor = (TextView)findViewById(R.id.gyroSensor);
                fusionSensor = (TextView)findViewById(R.id.fusionSensor);

                aListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        TextView magSensorData = (TextView)findViewById(R.id.magSensorData);
                        magSensorData.setText("mag x: " + Float.toString(event.values[0]) +
                                "\nmag y: " + Float.toString(event.values[1]) +
                                "\nmag z: " + Float.toString(event.values[2]));
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                };

                aListener1 = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        TextView gyroSensorData = (TextView)findViewById(R.id.gyroSensorData);
                        gyroSensorData.setText("gyro x: " + Float.toString(event.values[0]) +
                                "\ngyro y: " + Float.toString(event.values[1]) +
                                "\ngyro z: " + Float.toString(event.values[2]));
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                };
                aListener2 = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        TextView fusionSensorData = (TextView)findViewById(R.id.fusionSensorData);
                        fusionSensorData.setText("fusion x: " + Float.toString(event.values[0]) +
                                "\nfusion y: " + Float.toString(event.values[1]) +
                                "\nfusion z: " + Float.toString(event.values[2]));
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                };
                dynamicSensorListSize = (TextView)findViewById(R.id.dynamicSensorListSize);
                sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

                mCallback = new Callback();
                sensorManager.registerDynamicSensorCallback(mCallback);

                dynamicSensorList = sensorManager.getDynamicSensorList(Sensor.TYPE_ALL);
                dynamicSensorListSize.setText("dynamic list size is "+dynamicSensorList.size());
                aSensor = dynamicSensorList.get(0);
                accSensor.setText(aSensor.getName());
                sensorManager.registerListener(this,aSensor,SensorManager.SENSOR_DELAY_NORMAL);

                aSensor1 = dynamicSensorList.get(1);
                magSensor.setText(aSensor1.getName());
                sensorManager.registerListener(aListener,aSensor1,SensorManager.SENSOR_DELAY_NORMAL);

                aSensor2 = dynamicSensorList.get(2);
                gyroSensor.setText(aSensor2.getName());
                sensorManager.registerListener(aListener1,aSensor2,SensorManager.SENSOR_DELAY_NORMAL);

                aSensor3 = dynamicSensorList.get(3);
                fusionSensor.setText(aSensor3.getName());
                sensorManager.registerListener(aListener2,aSensor3,SensorManager.SENSOR_DELAY_NORMAL);

        } //end onCreate

        @Override
       public void onSensorChanged(SensorEvent event) {
            TextView accSensorData = (TextView) findViewById(R.id.accSensorData);
                accSensorData.setText("acc x: " + Float.toString(event.values[0]) +
                        "\nacc y: " + Float.toString(event.values[1]) +
                        "\nacc z: " + Float.toString(event.values[2]));
      }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class Callback extends SensorManager.DynamicSensorCallback{

        private Sensor mSensor = null;
        private CountDownLatch mConnectLatch;
        private CountDownLatch mDisconnectLatch;

        @Override
        public void onDynamicSensorConnected(Sensor sensor) {
            mSensor = sensor;
            TextView onSensorConnect = (TextView)findViewById(R.id.onSensorConnected);
            onSensorConnect.setText("Sensor Connected: "+mSensor);

            if (mConnectLatch != null) {
                mConnectLatch.countDown();
            }
        }
        @Override
        public void onDynamicSensorDisconnected(Sensor sensor) {
            if (mSensor == sensor) {
                mSensor = null;
                if (mDisconnectLatch != null) {
                    mDisconnectLatch.countDown();
                }
            }
        }

        public boolean waitForConnection(){
            boolean ret;
            mConnectLatch = new CountDownLatch(1);
            try {
                ret = mConnectLatch.await(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                ret = false;
                Thread.currentThread().interrupt();
            } finally {
                mConnectLatch = null;
            }
            return ret;
        }

    } //end class Callback


}

