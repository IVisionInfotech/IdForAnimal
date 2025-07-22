package com.idforanimal.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public interface BluetoothListener {

    interface onConnectionListener {
        void onConnectionStateChanged(BluetoothSocket socket, int state);

        void onConnectionFailed(int errorCode);
    }

    interface onReceiveListener {
        void onReceived(String receivedData);
    }

    interface onDetectNearbyDeviceListener {
        void onDeviceDetected(BluetoothDevice device);
    }

    interface onDevicePairListener {
        void onDevicePaired(BluetoothDevice device);

        void onCancelled(BluetoothDevice device);
    }

    interface onDiscoveryStateChangedListener {
        void onDiscoveryStateChanged(int state);
    }
}