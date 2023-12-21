package com.realtek.simpleconfiglib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

import ulife.goscam.com.loglib.dbg;

public class SCNetworkOps
{
  private static final String TAG = "SCNetworkOps";
  private WifiManager SCWifiMngr;
  private WifiInfo SCWifiInfo;
  private static String macAddr;
  private Context context;
  private final String METHOD_SET_WIFI_AP_ENABLED = "setWifiApEnabled";
  private final String METHOD_GET_WIFI_AP_STATE = "getWifiApState";
  private final String METHOD_GET_WIFI_AP_CONFIGURATION = "getWifiApConfiguration";
  private final String METHOD_SET_WIFI_AP_CONFIGURATION = "setWifiApConfiguration";
  private final String METHOD_SET_WIFI_AP_CONFIG = "setWifiApConfig";
  private static final int WIFI_AP_STATE_DISABLING = 10;
  private static final int WIFI_AP_STATE_DISABLED = 11;
  private static final int WIFI_AP_STATE_ENABLING = 12;
  private static final int WIFI_AP_STATE_ENABLED = 13;
  private static final int WIFI_AP_STATE_FAILED = 14;
  private boolean IsWifiApEnabled = false;
  private WifiConfiguration wifiApConf;

  public static byte[] int2byteLE(int in)
  {
    byte[] out = new byte[4];

    out[0] = (byte)(in & 0xFF);
    out[1] = (byte)(in >> 8 & 0xFF);
    out[2] = (byte)(in >> 16 & 0xFF);
    out[3] = (byte)(in >> 24 & 0xFF);

    return out;
  }

  public static byte[] int2byteBE(int in) {
    byte[] out = new byte[4];

    out[3] = (byte)(in & 0xFF);
    out[2] = (byte)(in >> 8 & 0xFF);
    out[1] = (byte)(in >> 16 & 0xFF);
    out[0] = (byte)(in >> 24 & 0xFF);

    return out;
  }

  public static long IPStr2IntegerBE(String ip) {
    String[] items = ip.split("\\.");

    return (Long.valueOf(items[0]).longValue() << 24 | 
      Long.valueOf(items[1]).longValue() << 16 | 
      Long.valueOf(items[2]).longValue() << 8 | 
      Long.valueOf(items[3]).longValue());
  }

  public static String IntegerLE2IPStr(int ipInt) {
    StringBuilder sb = new StringBuilder();

    sb.append(ipInt & 0xFF).append(".");
    sb.append(ipInt >> 8 & 0xFF).append(".");
    sb.append(ipInt >> 16 & 0xFF).append(".");
    sb.append(ipInt >> 24 & 0xFF);

    return sb.toString();
  }

  public static String IntegerBE2IPStr(int ipInt) {
    StringBuilder sb = new StringBuilder();

    sb.append(ipInt >> 24 & 0xFF).append(".");
    sb.append(ipInt >> 16 & 0xFF).append(".");
    sb.append(ipInt >> 8 & 0xFF).append(".");
    sb.append(ipInt & 0xFF);

    return sb.toString();
  }

  public void WifiInit(Context context) {
    this.context = context;
    this.SCWifiMngr = ((WifiManager)context.getSystemService("wifi"));
    this.SCWifiInfo = this.SCWifiMngr.getConnectionInfo();

    macAddr = (this.SCWifiInfo == null) ? null : this.SCWifiInfo.getMacAddress();
    dbg.e("SCNetworkOps", "mac(from java) is " + macAddr); }

  public void WifiOpen() {
    if (!(this.SCWifiMngr.isWifiEnabled()))
      this.SCWifiMngr.setWifiEnabled(true);
  }

  public boolean isApEnabled(Context context)
  {
    int state = getWifiApState(context);
    return ((12 == state) || (13 == state));
  }

  public int getWifiApState(Context context) {
    try {
      Method method = this.SCWifiMngr.getClass().getMethod("getWifiApState", new Class[0]);
      return ((Integer)method.invoke(this.SCWifiMngr, new Object[0])).intValue();
    } catch (Exception e) {
      e.printStackTrace();
      dbg.e("SCNetworkOps", "Cannot get WiFi AP state: " + e); }
    return 14;
  }

  public WifiConfiguration getWifiApConfiguration()
  {
    try {
      Method method = this.SCWifiMngr.getClass().getMethod("getWifiApConfiguration", new Class[0]);
      return ((WifiConfiguration)method.invoke(this.SCWifiMngr, new Object[0]));
    } catch (Exception e) {
      e.printStackTrace(); }
    return null;
  }

  public String getWifiApSSID()
  {
    WifiConfiguration wc = getWifiApConfiguration();
    String SSID = wc.SSID;
    dbg.e("SCNetworkOps", "softAP ssid =" + SSID);
    return ((wc == null) ? null : SSID);
  }

  public void WifiClose() {
    if (this.SCWifiMngr.isWifiEnabled())
      this.SCWifiMngr.setWifiEnabled(false);
  }

  public void WifiStartScan()
  {
    this.SCWifiMngr.startScan();
  }

  public List<ScanResult> WifiGetScanResults() {
    return this.SCWifiMngr.getScanResults();
  }

  public List<WifiConfiguration> WifiGetConfiguredNetworks() {
    return this.SCWifiMngr.getConfiguredNetworks();
  }

  public int WifiStatus() {
    return this.SCWifiMngr.getWifiState();
  }

  public String WifiAvailable() {
    return "android.net.wifi.SCAN_RESULTS";
  }

  public boolean isWifiConnected(String ssid) {
    WifiInfo wifiInfo = this.SCWifiMngr.getConnectionInfo();
    String ssidGet = wifiInfo.getSSID();

    if (ssidGet == null) {
      dbg.e("SCNetworkOps", "Get SSID Error");
      return false;
    }

    if ((ssidGet.equals(new String("\"" + ssid + "\""))) || (ssidGet.equals(new String(ssid))))
    {
      ConnectivityManager connManager = 
        (ConnectivityManager)this.context.getSystemService("connectivity");
      NetworkInfo mWifi = connManager.getNetworkInfo(1);
      if (mWifi.isConnected()) {
        return true;
      }
    }
    return false;
  }

  public String getConnectedWifiSSID() {
    WifiInfo wifiInfo = this.SCWifiMngr.getConnectionInfo();
    return wifiInfo.getSSID();
  }

  public String getConnectedWifiBSSID() {
    WifiInfo wifiInfo = this.SCWifiMngr.getConnectionInfo();
    return wifiInfo.getBSSID();
  }

  public String WifiGetMacStr() {
    return macAddr;
  }

  public int WifiGetIpInt()
  {
    WifiInfo wifiInfo = this.SCWifiMngr.getConnectionInfo();
    return ((wifiInfo == null) ? 0 : wifiInfo.getIpAddress());
  }

  public String WifiGetIpString(int ipInt) {
    StringBuilder sb = new StringBuilder();
    sb.append(ipInt & 0xFF).append(".");
    sb.append(ipInt >> 8 & 0xFF).append(".");
    sb.append(ipInt >> 16 & 0xFF).append(".");
    sb.append(ipInt >> 24 & 0xFF);
    return sb.toString();
  }

	public int SoftApGetInt() throws SocketException {
		Enumeration e = NetworkInterface.getNetworkInterfaces();
		if (e.hasMoreElements()) {
			NetworkInterface n = (NetworkInterface) e.nextElement();
			Enumeration ee = n.getInetAddresses();
			while (true) {
				InetAddress i = (InetAddress) ee.nextElement();
				if ((i.getHostAddress().length() > 9)
						&& (i.getHostAddress().length() <= 15)
						&& (!(i.getHostAddress().equals("127.0.0.1")))) {
					String ipStr = i.getHostAddress().toString();
					int ret = Integer.parseInt(ipStr);
					System.out.println("SoftAp ipStr:" + ipStr);
					System.out.println("SoftAp int format:" + ret);
					return ret;
				}
				if (!(ee.hasMoreElements())) {
					return 0;
				}
			}
		}
		return 0;
	}

  public void BroadcastSocketCreate()
  {
    try {
      SCParam.UDPBcast.BcastSock = new DatagramSocket(SCParam.UDPBcast.SrcPort);
    } catch (IOException e2) {
      e2.printStackTrace();
      System.out.printf("Broadcast Socket Create Error", new Object[0]);
      return;
    }
  }

  public void BroadcastSocketDestroy() {
    if (SCParam.UDPBcast.BcastSock != null)
      SCParam.UDPBcast.BcastSock.close();
  }

  public void UnicastSocketCreate()
  {
    try
    {
      SCParam.UDPUcast.UcastSock = new DatagramSocket(SCParam.UDPUcast.SrcPort);
    } catch (SocketException e) {
      e.printStackTrace();
      dbg.e("SCNetworkOps", "Unicast Socket Create Error");
      return;
    }
  }

  public void UnicastSocketDestroy() {
    if (SCParam.UDPUcast.UcastSock != null)
      SCParam.UDPUcast.UcastSock.close();
  }

  public void UDPBroadcastSend()
  {
    InetAddress uAddress = null;
    try
    {
      uAddress = InetAddress.getByName(SCParam.UDPBcast.IPAddr);
    } catch (UnknownHostException e2) {
      e2.printStackTrace();
    }
    if (uAddress == null) {
      System.out.printf("Get InetAddress error!", new Object[0]);
      return;
    }

    DatagramPacket dgPkt = new DatagramPacket(
      SCParam.UDPBcast.SendMsg, 
      SCParam.UDPBcast.SendLen, 
      uAddress, 
      SCParam.UDPBcast.DestPort);
    try {
      SCParam.UDPBcast.BcastSock.send(dgPkt);
    }
    catch (IOException e) {
      dbg.e("SCNetworkOps", "UDP Broadcast Send Error");
      return;
    }
  }

  public void UDPUnicastSend()
  {
    InetAddress uAddress = null;
    try
    {
      uAddress = InetAddress.getByName(SCParam.UDPUcast.IPAddr);
    } catch (UnknownHostException e2) {
      e2.printStackTrace();
    }
    if (uAddress == null) {
      System.out.printf("Get InetAddress error!", new Object[0]);
      return;
    }

    DatagramPacket dgPkt = new DatagramPacket(
      SCParam.UDPUcast.SendMsg, 
      SCParam.UDPUcast.SendLen, 
      uAddress, 
      SCParam.UDPUcast.DestPort);
    try {
      SCParam.UDPUcast.UcastSock.send(dgPkt);
    }
    catch (IOException e) {
      dbg.e("SCNetworkOps", "UDP Send Error");
      return;
    }
  }

  public boolean UDPUnicastRecv()
  {
    DatagramPacket dgPacket = null;
    try
    {
      dgPacket = new DatagramPacket(SCParam.UDPUcast.RecvBuf, SCParam.UDPUcast.RecvBuf.length);
      if (dgPacket != null && SCParam.UDPUcast.UcastSock != null)
        SCParam.UDPUcast.UcastSock.receive(dgPacket);
    }
    catch (IOException e)
    {
      dbg.e("SCNetworkOps", "UDP Receive Error");
      return false;
    }

    SCParam.UDPUcast.RecvLen = dgPacket.getLength();

    return true;
  }
}