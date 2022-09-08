package com.dede.android_eggs

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import androidx.annotation.RequiresPermission
import androidx.core.content.getSystemService
import java.net.Inet4Address

/**
 * Ipv4
 *
 * @author shhu
 * @since 2022/9/8
 */
class Ipv4AddressRequest() {

    interface Callback {
        fun onResult(ipv4Address: String?)
    }

    private fun formatAddress(ipAddress: Int): String {
        return String.format("%d.%d.%d.%d",
            ipAddress and 0xff, ipAddress shr 8 and 0xff, ipAddress shr 16 and 0xff,
            ipAddress shr 24 and 0xff)
    }

    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    private fun getAddressDeprecated(context: Context): String? {
        val wifiManager = context.getSystemService<WifiManager>() ?: return null
        return formatAddress(wifiManager.connectionInfo.ipAddress)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.CHANGE_NETWORK_STATE])
    private fun getAddress(context: Context, callback: Callback) {
        val connectivityManager = context.getSystemService<ConnectivityManager>()
        if (connectivityManager == null) {
            callback.onResult(null)
            return
        }
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        connectivityManager.requestNetwork(request, object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                val linkProperties = connectivityManager.getLinkProperties(network)
                if (linkProperties != null) {
                    val inet4Address =
                        linkProperties.linkAddresses.find { it.address is Inet4Address }
                    if (inet4Address != null) {
                        val address = inet4Address.address.hostAddress
                        callback.onResult(address)
                        return
                    }
                }

                val wifiInfo = networkCapabilities.transportInfo as WifiInfo
                callback.onResult(formatAddress(wifiInfo.ipAddress))
            }
        })
    }

    fun request(context: Context, callback: Callback) {
        getAddress(context, callback)
    }
}