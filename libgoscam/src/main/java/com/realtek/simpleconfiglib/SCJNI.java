package com.realtek.simpleconfiglib;

public class SCJNI {

	  public native void StartConfig(Args paramArgs);

	  public native void StopConfig();

	  public static class Args
	  {
	    public Args(SCJNI jni) {
			// TODO Auto-generated constructor stub
			// jni.StartConfig(this);
		}
		public byte[] SSID;
	    public byte SSIDLen;
	    public byte[] Passwd;
	    public byte PasswdLen;
	    public byte[] PIN;
	    public byte PINLen;
	    public byte[] BSSID;
	    public byte BSSIDLen;
	    public int ConfigTime;
	    public byte ProfileRounds;
	    public int ProfileInterval;
	    public int PacketInterval;
	    public byte PacketCnts;
	    public byte SyncRounds;
	    public byte Mode;
	    public boolean Pack_type;
	    public boolean isSoftApMode;
	    public int Length;
	    public byte[] hostIP;
	    public byte HostIPLEN;
	    public byte[] wifiInterface;
	    public byte WifiInterfaceLEN;
	    public byte[] PhoneMac;
	    public byte PhoneMacLen;
	  }
}
