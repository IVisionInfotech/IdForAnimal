package com.idforanimal.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.idforanimal.R;
import com.idforanimal.activity.AnimalDetailsActivity;
import com.idforanimal.activity.BaseActivity;
import com.idforanimal.activity.MainNewActivity;
import com.idforanimal.databinding.ActivityBluetoothBinding;
import com.idforanimal.utils.Common;
import com.idforanimal.utils.Constant;
import com.idforanimal.utils.MyApplication;

import java.util.ArrayList;

public class BluetoothActivity extends BaseActivity {

    private static final String TAG = "BluetoothAct";
    private ArrayList<String> listDetectDevicesString = new ArrayList<>();
    private ArrayList<String> listPairedDevicesString = new ArrayList<>();
    private ArrayList<BluetoothDevice> listDetectBluetoothDevices = new ArrayList<>();
    private ArrayList<BluetoothDevice> listPairedBluetoothDevices = new ArrayList<>();
    private ArrayAdapter<String> adapterDetectBluetoothDevices, adapterPairedBluetoothDevices;
    private Bluetooth bluetooth;
    private Connection connection;
    private ActivityBluetoothBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBluetoothBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;
        initView();
        checkLocationPermissions(permission -> turnGPSOn(BluetoothActivity.this, permission1 -> checkLocationSettings()));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (connection.isConnected()) {
            connection.setOnReceiveListener(receiveListener);
        }
    }

    private final BluetoothListener.onReceiveListener receiveListener = receivedData -> {
        if (!receivedData.isEmpty()) {
            String readMessage = receivedData.replaceAll("[^0-9]", "").trim().replaceAll(" ", "");

            if (!readMessage.isEmpty() && readMessage.length() >= 15) {
                if (readMessage.length() != 15) {
                    readMessage = readMessage.substring(readMessage.length() - 15);
                }
                if (readMessage.length() == 15)
                    viewDetails(readMessage, Constant.typeRFIDTag);
            }
        }
    };

    private void viewDetails(String tag, String status) {
        if (MyApplication.isActivityVisible()) {
            Intent intent = new Intent("com.idforanimal.ACTION_VIEW_DETAILS");
            intent.putExtra("tag", tag);
            intent.putExtra("status", status);
            Common.sendBroadcast(context, intent);
        } else {
            startActivity(new Intent(context, AnimalDetailsActivity.class).putExtra("tag", tag).putExtra("status", status));
        }
    }

    private final BluetoothListener.onConnectionListener connectionListener = new BluetoothListener.onConnectionListener() {
        @Override
        public void onConnectionStateChanged(BluetoothSocket socket, int state) {
            switch (state) {
                case Connection.CONNECTING: {
                    break;
                }

                case Connection.CONNECTED: {
                    Common.hideProgressDialog();
                    onBackPressed();
                    break;
                }

                case Connection.DISCONNECTED: {
                    bindConnectedDevice("", "");
                    Common.hideProgressDialog();
                    disconnect();
                    break;
                }
            }
        }

        @Override
        public void onConnectionFailed(int errorCode) {
            Common.hideProgressDialog();
            bindConnectedDevice("", "");
            switch (errorCode) {
                case Connection.SOCKET_NOT_FOUND: {
                    Common.showToast("Socket not found");
                    break;
                }

                case Connection.CONNECT_FAILED: {
                    Common.showToast("Connect Failed");
                    break;
                }
            }
            disconnect();
        }
    };

    private void disconnect() {
        if (connection != null) {
            connection.disconnect();
        }
    }

    private void initView() {
        bluetooth = new Bluetooth(this);
        connection = new Connection(this);

        adapterDetectBluetoothDevices = new ArrayAdapter<>(context, R.layout.device_item, listDetectDevicesString);
        adapterPairedBluetoothDevices = new ArrayAdapter<>(context, R.layout.device_item, listPairedDevicesString);
        binding.listViewDetectDevice.setAdapter(adapterDetectBluetoothDevices);
        binding.listViewPairedDevice.setAdapter(adapterPairedBluetoothDevices);
        checkPermissions(getBluetoothPermission(), permission -> {
            if (bluetooth.isOn()) {
                getBluetoothDeviceList();
            } else {
                bluetooth.turnOnWithPermission(BluetoothActivity.this);
            }
        });

        binding.btnScan.setOnClickListener(v ->
                checkLocationPermissions(permission -> turnGPSOn(BluetoothActivity.this, permission1 -> {
                    if (!bluetooth.isOn()) {
                        bluetooth.turnOnWithPermission(BluetoothActivity.this);
                    }
                    clearDetectDeviceList();
                    getPairedDevices();
                    bluetooth.startDetectNearbyDevices();
                })));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void getBluetoothDeviceList() {
        getPairedDevices();      // Get Paired devices list


        bluetooth.setOnDiscoveryStateChangedListener(state -> {
            if (state == Bluetooth.DISCOVERY_STARTED) {
            }

            if (state == Bluetooth.DISCOVERY_FINISHED) {
            }
        });

        bluetooth.setOnDetectNearbyDeviceListener(device -> {
            if (!listDetectDevicesString.contains(device.getName())) {
                listDetectDevicesString.add(device.getName()); // add to list
                listDetectBluetoothDevices.add(device);
                adapterDetectBluetoothDevices.notifyDataSetChanged();
            }
        });

        bluetooth.setOnDevicePairListener(new BluetoothListener.onDevicePairListener() {
            @Override
            public void onDevicePaired(BluetoothDevice device) {

                Common.showToast("Paired successfull");          // remove device from detect device list
                listDetectDevicesString.remove(device.getName());
                listDetectBluetoothDevices.remove(device);
                adapterDetectBluetoothDevices.notifyDataSetChanged();

                listPairedDevicesString.add(device.getName());           // add device to paired device list
                listPairedBluetoothDevices.add(device);
                adapterPairedBluetoothDevices.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(BluetoothDevice device) {
                Common.showToast("Paired failed");
            }
        });

        binding.listViewDetectDevice.setOnItemClickListener((parent, view, position, id) -> {
            if (bluetooth.requestPairDevice(listDetectBluetoothDevices.get(position))) {
                Common.showToast("Pair request send successfully");
            }
        });


        binding.listViewPairedDevice.setOnItemClickListener((parent, view, position, id) -> {        // Unpair bluetooh device #START
            Common.showProgressDialog(this, "Connecting...");
            String deviceAddress = listPairedBluetoothDevices.get(position).getAddress();
            String deviceName = listPairedBluetoothDevices.get(position).getName();

            if (connection.connect(deviceAddress, true, connectionListener, receiveListener)) {
                bindConnectedDevice(deviceName, deviceAddress);
            } else {
                bindConnectedDevice("", "");
                Common.hideProgressDialog();
                connection.disconnect();
            }
        });
    }

    private void clearDetectDeviceList() {
        if (!listDetectDevicesString.isEmpty()) {
            listDetectDevicesString.clear();
        }

        if (!listDetectBluetoothDevices.isEmpty()) {
            listDetectBluetoothDevices.clear();
        }
        adapterDetectBluetoothDevices.notifyDataSetChanged();
    }

    private void getPairedDevices() {
        ArrayList<BluetoothDevice> devices = bluetooth.getPairedDevices();

        if (!devices.isEmpty()) {
            for (BluetoothDevice device : devices) {
                runOnUiThread(() -> {
                    String name = device.getName();
                    if (name != null) {
                        listPairedDevicesString.add(name);
                        listPairedBluetoothDevices.add(device);
                        adapterDetectBluetoothDevices.notifyDataSetChanged();
                        adapterPairedBluetoothDevices.notifyDataSetChanged();
                    }

                });

            }
        }
    }

    private void bindConnectedDevice(String deviceName, String deviceAddress) {
        session.setDeviceName(deviceName);
        session.setDeviceAddress(deviceAddress);
    }
}