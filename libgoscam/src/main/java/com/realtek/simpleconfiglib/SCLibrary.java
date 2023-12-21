package com.realtek.simpleconfiglib;

import android.content.Context;
import android.goscam.smartconn._8188ConnectParams;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ulife.goscam.com.loglib.dbg;

public class SCLibrary {

	private static final String TAG = "SCLibrary";
	public Handler TreadMsgHandler;
	public static int TotalConfigTimeMs;
	public static int OldModeConfigTimeMs;
	public static byte ProfileSendRounds;
	public static int ProfileSendTimeIntervalMs;
	public static int PacketSendTimeIntervalMs;
	public static byte EachPacketSendCounts;
	public static boolean PackType;
	private SCNetworkOps SCNetOps;
	private SCJNI ScJni = new SCJNI();
	private SCJNI.Args ScArgs;
	private Thread SendThread;
	private Thread RecvThread;
	private boolean ConfigSuccess;
	public boolean SendEnable;
	private boolean SendInProgress;
	private boolean RecvEnable;

	static {
		System.loadLibrary("simpleconfiglib");

		TotalConfigTimeMs = 120000;
		OldModeConfigTimeMs = 30000;
		ProfileSendRounds = 0;
		ProfileSendTimeIntervalMs = 0;
		PacketSendTimeIntervalMs = 0;
		EachPacketSendCounts = 1;
		PackType = true;
	}

	public SCLibrary() {
		this.ScArgs = new SCJNI.Args(this.ScJni);
		this.ConfigSuccess = false;
		this.SendEnable = false;
		this.SendInProgress = false;
		this.RecvEnable = false;
	}

	public void WifiInit(Context context) {
		this.SCNetOps.WifiInit(context);
	}

	public void WifiOpen() {
		this.SCNetOps.WifiOpen();
	}

	public boolean IsWifiApEnabled(Context context) {
		return this.SCNetOps.isApEnabled(context);
	}

	public void WifiStartScan() {
		this.SCNetOps.WifiStartScan();
	}

	public List<ScanResult> WifiGetScanResults() {
		return this.SCNetOps.WifiGetScanResults();
	}

	public int WifiStatus() {
		return this.SCNetOps.WifiStatus();
	}

	public String WifiAvailable() {
		return this.SCNetOps.WifiAvailable();
	}

	public boolean isWifiConnected(String ssid) {
		return this.SCNetOps.isWifiConnected(ssid);
	}

	public String getConnectedWifiSSID() {
		return this.SCNetOps.getConnectedWifiSSID();
	}

	public String getConnectedWifiBSSID() {
		return this.SCNetOps.getConnectedWifiBSSID();
	}

	public String WifiGetMacStr() {
		return this.SCNetOps.WifiGetMacStr();
	}

	public int WifiGetIpInt() {
		return this.SCNetOps.WifiGetIpInt();
	}

	public String WifiGetIpString(int ipInt) {
		return this.SCNetOps.WifiGetIpString(ipInt);
	}

	private void RtkSCNetInit() {
		this.SCNetOps = new SCNetworkOps();

		SCParam.UDPBcast.SrcPort = 18864;
		SCParam.UDPBcast.DestPort = 18864;
		this.SCNetOps.BroadcastSocketCreate();

		SCParam.UDPUcast.SrcPort = 8864;
		SCParam.UDPUcast.DestPort = 8864;
		this.SCNetOps.UnicastSocketCreate();
	}

	public void rtk_sc_reset() {
		this.ConfigSuccess = false;

		SCParam.RecvACK.MaxCfgNum = 0;
		Arrays.fill(SCParam.RecvACK.Status, (byte) 0);
		for (int i = 0; i < 32; ++i) {
			Arrays.fill(SCParam.RecvACK.Mac[i], (byte) 0);
			Arrays.fill(SCParam.RecvACK.Type[i], (byte) 0);
			Arrays.fill(SCParam.RecvACK.IPBuf[i], (byte) 0);
			Arrays.fill(SCParam.RecvACK.NameBuf[i], (byte) 0);
		}
		SCParam.RecvACK.IP = new String[32];
		SCParam.RecvACK.Name = new String[32];
	}

	public void rtk_sc_init() {
		RtkSCNetInit();
		rtk_sc_reset();
		RtkSCRecvThread();
	}

	public void rtk_sc_exit() {
		this.SCNetOps.BroadcastSocketDestroy();
		this.SCNetOps.UnicastSocketDestroy();

		this.SendEnable = false;
		this.ScJni.StopConfig();
		this.RecvEnable = false;
		this.ConfigSuccess = false;

		this.RecvThread.interrupt();
	}

	public void rtk_sc_set_ssid(String ssid) {
		if (ssid == null) {
			return;
		}
		SCParam.SC_SSID = ssid;
	}

	public void rtk_sc_set_password(String passwd) {
		if (passwd == null) {
			return;
		}
		SCParam.SC_PASSWD = passwd;
	}

	public void rtk_sc_set_ip(int ip) {
		SCParam.SC_IP = ip;
	}

	public void rtk_sc_set_bssid(String bssid) {
		if (bssid == null) {
			dbg.e("SCLibrary", "BSSID is null\n");
			return;
		}

		SCParam.SC_BSSID = bssid;
	}

	public void rtk_sc_set_default_pin(String pin) {
		if ((pin != null) && (pin.length() > 0)) {
			SCParam.Default_PIN = pin;
		} else {
			dbg.e("SCLibrary", "Invalid PIN");
		}
	}

	public String rtk_sc_get_default_pin() {
		return SCParam.Default_PIN;
	}

	public void rtk_sc_set_pin(String pin) {
		if ((pin != null) && (pin.length() > 0)) {
			SCParam.SC_PIN = pin;
			this.ScArgs.Mode = 3;
		} else {
			SCParam.SC_PIN = SCParam.Default_PIN;
			this.ScArgs.Mode = 2;
		}
	}

	public void rtk_sc_build_profile() {
	}

	public void rtk_sc_start(_8188ConnectParams params) {
		if (params == null) {
			throw new IllegalArgumentException("the params is null,connect error!");
		} else {
			rtk_sc_start(params.getSsid(), params.getPasswd(), params.getPin(), params.getBssid(), params.isPkt_type(), params.isIssoftap(),
					params.getTotal_time(), params.getOld_mode_time(), params.getProfile_rounds(), params.getProfile_interval(), params.getPacket_interval(),
					params.getPacket_counts(), params.getHostip(), params.getWifi_interface(), params.getPhoneMac());

		}
	}

	public void rtk_sc_start(final String ssid, final String passwd, final String pin, final String bssid, final boolean pkt_type, final boolean issoftap,
			final int total_time, final int old_mode_time, final byte profile_rounds, final int profile_interval, final int packet_interval,
			final byte packet_counts, final String hostip, final String wifi_interface, final String phoneMac) {
		dbg.d( "ssid : " + ssid);
		dbg.d( "passwd : " + passwd);
		dbg.d( "pin : " + pin);
		dbg.d( "bssid : " + bssid);
		dbg.d( "pkt_type : " + pkt_type);
		dbg.d( "issoftap : " + issoftap);
		dbg.d( "total_time : " + total_time);
		dbg.d( "old_mode_time : " + old_mode_time);
		dbg.d( "profile_rounds : " + profile_rounds);
		dbg.d( "profile_interval : " + profile_interval);
		dbg.d( "packet_interval:" + packet_interval);
		dbg.d( "packet_counts:" + packet_counts);
		dbg.d( "hostip : " + hostip);
		dbg.d( "wifi_interface : " + wifi_interface);
		dbg.d( "phoneMac:" + phoneMac);

		// 07-26 15:06:40.562: D/york(949): ssid : HiWiFi-test
		// 07-26 15:06:40.562: D/york(949): passwd : goscam518
		// 07-26 15:06:40.563: D/york(949): pin : 57289961
		// 07-26 15:06:40.563: D/york(949): bssid : d4:ee:07:10:7f:08
		// 07-26 15:06:40.564: D/york(949): pkt_type : false
		// 07-26 15:06:40.565: D/york(949): issoftap : false
		// 07-26 15:06:40.565: D/york(949): total_time : 120000
		// 07-26 15:06:40.566: D/york(949): old_mode_time : 0
		// 07-26 15:06:40.566: D/york(949): profile_rounds : 1
		// 07-26 15:06:40.566: D/york(949): profile_interval : 1000
		// 07-26 15:06:40.567: D/york(949): packet_interval:0
		// 07-26 15:06:40.567: D/york(949): packet_counts:1
		// 07-26 15:06:40.568: D/york(949): hostip :
		// 07-26 15:06:40.570: D/york(949): wifi_interface :
		// 07-26 15:06:40.571: D/york(949): phoneMac:f8:a4:5f:74:8e:f0

		// 07-26 15:09:37.030: D/york(5420): ssid : HiWiFi-test
		// 07-26 15:09:37.030: D/york(5420): passwd : goscam518
		// 07-26 15:09:37.030: D/york(5420): pin : 57289921
		// 07-26 15:09:37.030: D/york(5420): bssid : d4:ee:07:10:7f:08
		// 07-26 15:09:37.031: D/york(5420): pkt_type : false
		// 07-26 15:09:37.031: D/york(5420): issoftap : false
		// 07-26 15:09:37.031: D/york(5420): total_time : 120000
		// 07-26 15:09:37.031: D/york(5420): old_mode_time : 0
		// 07-26 15:09:37.031: D/york(5420): profile_rounds : 1
		// 07-26 15:09:37.031: D/york(5420): profile_interval : 1000
		// 07-26 15:09:37.032: D/york(5420): packet_interval:0
		// 07-26 15:09:37.032: D/york(5420): packet_counts:1
		// 07-26 15:09:37.032: D/york(5420): hostip :
		// 07-26 15:09:37.032: D/york(5420): wifi_interface :
		// 07-26 15:09:37.032: D/york(5420): phoneMac:f8:a4:5f:74:8e:f0

		this.SendEnable = true;
		this.SendThread = new Thread(new Runnable() {
			@Override
			public void run() {
				SCLibrary.this.SendInProgress = true;
				Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);

				long startTime = System.currentTimeMillis();

				long endTime = System.currentTimeMillis();
				long timeElasped = endTime - startTime;

				SCLibrary.this.ScArgs.SSID = ssid.getBytes();
				SCLibrary.this.ScArgs.SSIDLen = (byte) ssid.getBytes().length;
				SCLibrary.this.ScArgs.Passwd = passwd.getBytes();
				SCLibrary.this.ScArgs.PasswdLen = (byte) passwd.getBytes().length;
				SCLibrary.this.ScArgs.PIN = pin.getBytes();
				SCLibrary.this.ScArgs.PINLen = (byte) pin.getBytes().length;
				SCLibrary.this.ScArgs.BSSID = bssid.getBytes();
				SCLibrary.this.ScArgs.BSSIDLen = (byte) bssid.getBytes().length;
				SCLibrary.this.ScArgs.ProfileRounds = profile_rounds;
				SCLibrary.this.ScArgs.ProfileInterval = profile_interval;
				SCLibrary.this.ScArgs.PacketInterval = packet_interval;
				SCLibrary.this.ScArgs.PacketCnts = packet_counts;
				SCLibrary.this.ScArgs.Pack_type = pkt_type;

				SCLibrary.this.ScArgs.isSoftApMode = issoftap;
				SCLibrary.this.ScArgs.hostIP = hostip.getBytes();
				SCLibrary.this.ScArgs.HostIPLEN = (byte) hostip.getBytes().length;
				SCLibrary.this.ScArgs.wifiInterface = wifi_interface.getBytes();
				SCLibrary.this.ScArgs.WifiInterfaceLEN = (byte) wifi_interface.getBytes().length;

				SCLibrary.this.ScArgs.PhoneMac = phoneMac.getBytes();
				SCLibrary.this.ScArgs.PhoneMacLen = (byte) phoneMac.getBytes().length;

				SCLibrary.this.ScArgs.Length = (SCLibrary.this.ScArgs.SSIDLen + SCLibrary.this.ScArgs.PasswdLen + 4 + 2);
				if (SCLibrary.this.ScArgs.SSIDLen > 0) {
					SCLibrary.this.ScArgs.Length += 1;
				}
				dbg.i("SCLibrary", "ssid len " + SCLibrary.this.ScArgs.SSIDLen);
				dbg.i("SCLibrary", "passwd len " + SCLibrary.this.ScArgs.PasswdLen);
				dbg.i("SCLibrary", "profile length is " + SCLibrary.this.ScArgs.Length);
				do {
					dbg.i("SCLibrary", "Start old mode config...");
					SCLibrary.this.ScArgs.ConfigTime = old_mode_time;
					SCLibrary.this.ScArgs.SyncRounds = 1;
					SCLibrary.this.ScJni.StartConfig(SCLibrary.this.ScArgs);
					endTime = System.currentTimeMillis();
					timeElasped = endTime - startTime;

					if ((!(SCLibrary.this.SendEnable)) || (SCLibrary.this.ConfigSuccess)) {
						break;
					}
				} while (timeElasped < old_mode_time);

				while ((SCLibrary.this.SendEnable) && (!(SCLibrary.this.ConfigSuccess)) && (timeElasped < total_time)) {
					dbg.i("SCLibrary", "Start new mode config...");
					SCLibrary.this.ScArgs.ConfigTime = (total_time - old_mode_time);
					SCLibrary.this.ScArgs.SyncRounds = 16;
					SCLibrary.this.ScArgs.Mode = 4;
					SCLibrary.this.ScJni.StartConfig(SCLibrary.this.ScArgs);
					endTime = System.currentTimeMillis();
					timeElasped = endTime - startTime;
				}

				endTime = System.currentTimeMillis();
				timeElasped = endTime - startTime;
				dbg.i("SCLibrary", "Total Config Time Elapsed: " + timeElasped + "ms");
				if ((!(SCLibrary.this.ConfigSuccess)) && (timeElasped > total_time)) {
					Message msg = Message.obtain();
					msg.obj = null;
					msg.what = -1;
					if (SCLibrary.this.TreadMsgHandler != null) {
						SCLibrary.this.TreadMsgHandler.sendMessage(msg);
					}
				}
				try {
					Thread.sleep(500L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Message msg = Message.obtain();
				msg.obj = Long.toString(timeElasped);
				msg.what = 5;
				if (SCLibrary.this.TreadMsgHandler != null) {
					SCLibrary.this.TreadMsgHandler.sendMessage(msg);
				}
				SCLibrary.this.SendInProgress = false;
			}
		});
		if (!(this.SendInProgress)) {
			this.SendThread.start();
		} else {
			dbg.w("SCLibrary", "Config already in progress!");
		}
	}

	public void rtk_sc_start(String in_wifiInterface, String in_hostip) {
		SCParam.SC_WIFI_Interface = in_wifiInterface;
		SCParam.SC_HOSTIP = in_hostip;
		SCParam.SC_PHONE_MAC_ADDR = this.SCNetOps.WifiGetMacStr();
		SCParam.SC_SOFTAP_MODE = false;
		rtk_sc_start(SCParam.SC_SSID, SCParam.SC_PASSWD, SCParam.SC_PIN, SCParam.SC_BSSID, SCParam.SC_PKT_TYPE, SCParam.SC_SOFTAP_MODE, TotalConfigTimeMs,
				OldModeConfigTimeMs, ProfileSendRounds, ProfileSendTimeIntervalMs, PacketSendTimeIntervalMs, EachPacketSendCounts, SCParam.SC_HOSTIP,
				SCParam.SC_WIFI_Interface, SCParam.SC_PHONE_MAC_ADDR);
	}

	public void rtk_sc_stop() {
		this.SendEnable = false;
		this.ScJni.StopConfig();
		this.ConfigSuccess = false;
	}

	public int HandleCfgACK(byte[] recv_buf) {
		int cfgIndex = 0;
		int MacEqualCnt = 0;

		int length = recv_buf[1] << 8 & 0xFF00 | recv_buf[2] & 0xFF;

		if (length < 13) {
			dbg.e("SCLibrary", "Received format error\n");
			return -1;
		}

		if (SCParam.RecvACK.MaxCfgNum > 32) {
			dbg.e("SCLibrary", "Receive buf is full\n");
			return -1;
		}

		if (SCParam.RecvACK.MaxCfgNum > 0) {
			for (cfgIndex = 0; cfgIndex < SCParam.RecvACK.MaxCfgNum; ++cfgIndex) {
				for (int i = 0; i < 6; ++i) {
					if (recv_buf[(3 + i)] == SCParam.RecvACK.Mac[cfgIndex][i]) {
						++MacEqualCnt;
					}
				}
				if (MacEqualCnt == 6) {
					break;
				}
				MacEqualCnt = 0;
			}

		}

		byte[] ip_buf = new byte[4];
		System.arraycopy(recv_buf, 12, ip_buf, 0, 4);
		String IPStr = String.format(
				"%d.%d.%d.%d",
				new Object[] { Integer.valueOf(ip_buf[0] & 0xFF), Integer.valueOf(ip_buf[1] & 0xFF), Integer.valueOf(ip_buf[2] & 0xFF),
						Integer.valueOf(ip_buf[3] & 0xFF) });

		if ((SCParam.RecvACK.IP[cfgIndex] != null) && (SCParam.RecvACK.IP[cfgIndex].length() > 0) && (!(SCParam.RecvACK.IP[cfgIndex].equals(IPStr)))) {
			System.arraycopy(recv_buf, 3, SCParam.RecvACK.Mac[cfgIndex], 0, 6);
			String MacStr = new String();
			for (int i = 0; i < 6; ++i) {
				MacStr = MacStr + String.format("%02x", new Object[] { Byte.valueOf(SCParam.RecvACK.Mac[cfgIndex][i]) });
				if (i < 5) {
					MacStr = MacStr + ":";
				}
			}
			System.arraycopy(recv_buf, 12, SCParam.RecvACK.IPBuf[cfgIndex], 0, 4);
			SCParam.RecvACK.IP[cfgIndex] = String.format("%d.%d.%d.%d",
					new Object[] { Integer.valueOf(SCParam.RecvACK.IPBuf[cfgIndex][0] & 0xFF), Integer.valueOf(SCParam.RecvACK.IPBuf[cfgIndex][1] & 0xFF),
							Integer.valueOf(SCParam.RecvACK.IPBuf[cfgIndex][2] & 0xFF), Integer.valueOf(SCParam.RecvACK.IPBuf[cfgIndex][3] & 0xFF) });
			dbg.i("SCLibrary", "Refresh IP: " + SCParam.RecvACK.IP[cfgIndex] + " of MAC: " + MacStr);
			rtk_sc_send_cfg_ack_packet();

			Message msg = Message.obtain();
			msg.obj = null;
			msg.what = 0;
			this.TreadMsgHandler.sendMessage(msg);
		}

		if (SCParam.RecvACK.MaxCfgNum > 0) {
			return 0;
		}

		if (MacEqualCnt == 6) {
			return 0;
		}

		System.arraycopy(recv_buf, 3, SCParam.RecvACK.Mac[SCParam.RecvACK.MaxCfgNum], 0, 6);
		String MacStr = new String();
		for (int i = 0; i < 6; ++i) {
			MacStr = MacStr + String.format("%02x", new Object[] { Byte.valueOf(SCParam.RecvACK.Mac[SCParam.RecvACK.MaxCfgNum][i]) });
			if (i < 5) {
				MacStr = MacStr + ":";
			}
		}
		dbg.i("SCLibrary", "Added MAC: " + MacStr);

		if (length >= 7) {
			SCParam.RecvACK.Status[SCParam.RecvACK.MaxCfgNum] = recv_buf[9];
		}

		if (length >= 9) {
			System.arraycopy(recv_buf, 10, SCParam.RecvACK.Type[SCParam.RecvACK.MaxCfgNum], 0, 2);
		}

		if (length >= 13) {
			System.arraycopy(recv_buf, 12, SCParam.RecvACK.IPBuf[SCParam.RecvACK.MaxCfgNum], 0, 4);
			SCParam.RecvACK.IP[SCParam.RecvACK.MaxCfgNum] = String.format(
					"%d.%d.%d.%d",
					new Object[] { Integer.valueOf(SCParam.RecvACK.IPBuf[SCParam.RecvACK.MaxCfgNum][0] & 0xFF),
							Integer.valueOf(SCParam.RecvACK.IPBuf[SCParam.RecvACK.MaxCfgNum][1] & 0xFF),
							Integer.valueOf(SCParam.RecvACK.IPBuf[SCParam.RecvACK.MaxCfgNum][2] & 0xFF),
							Integer.valueOf(SCParam.RecvACK.IPBuf[SCParam.RecvACK.MaxCfgNum][3] & 0xFF) });
			dbg.i("SCLibrary", "IP: " + SCParam.RecvACK.IP[SCParam.RecvACK.MaxCfgNum]);
			rtk_sc_send_cfg_ack_packet();
		}

		if (length >= 77) {
			System.arraycopy(recv_buf, 16, SCParam.RecvACK.NameBuf[SCParam.RecvACK.MaxCfgNum], 0, 64);
			String name = null;
			try {
				name = new String(SCParam.RecvACK.NameBuf[SCParam.RecvACK.MaxCfgNum], "UTF-8").trim();
			} catch (UnsupportedEncodingException e) {
				dbg.e("SCLibrary", "Get device's name error");
				e.printStackTrace();
			}
			if (name.length() > 0) {
				SCParam.RecvACK.Name[SCParam.RecvACK.MaxCfgNum] = name;
			} else {
				SCParam.RecvACK.Name[SCParam.RecvACK.MaxCfgNum] = null;
			}
			dbg.i("SCLibrary", "Name: " + SCParam.RecvACK.Name[SCParam.RecvACK.MaxCfgNum]);
		}

		if (length >= 78) {
			SCParam.RecvACK.UsePin[SCParam.RecvACK.MaxCfgNum] = Boolean.valueOf((recv_buf[80] > 0) ? true : false);
		}

		SCParam.RecvACK.MaxCfgNum += 1;

		Message msg = Message.obtain();
		msg.obj = null;
		msg.what = 0;
		this.TreadMsgHandler.sendMessage(msg);

		return 0;
	}

	public void rtk_sc_send_cfg_ack_packet() {
		byte[] CmdBuf = new byte[92];

		Arrays.fill(CmdBuf, (byte) 0);
		int tmp12_11 = 0;
		byte[] tmp12_10 = CmdBuf;
		tmp12_10[tmp12_11] = (byte) (tmp12_10[tmp12_11] + 0);
		int tmp20_19 = 0;
		byte[] tmp20_18 = CmdBuf;
		tmp20_18[tmp20_19] = (byte) (tmp20_18[tmp20_19] + 0);
		int tmp28_27 = 0;
		byte[] tmp28_26 = CmdBuf;
		tmp28_26[tmp28_27] = (byte) (tmp28_26[tmp28_27] + 4);

		CmdBuf[1] = 0;
		CmdBuf[2] = 90;

		CmdBuf[3] = 0;

		if ((SCParam.RecvACK.IP[0] != null) && (SCParam.RecvACK.IP[0].length() > 0) && (!(SCParam.RecvACK.IP[0].equals("0.0.0.0")))) {
			SCParam.UDPUcast.IPAddr = SCParam.RecvACK.IP[0];
			SCParam.UDPUcast.SendLen = CmdBuf.length;
			SCParam.UDPUcast.SendMsg = CmdBuf;

			for (int i = 0; i < 8; ++i) {
				this.SCNetOps.UDPUnicastSend();
			}

		}

		SCParam.UDPBcast.IPAddr = "255.255.255.255";
		SCParam.UDPBcast.SendLen = CmdBuf.length;
		SCParam.UDPBcast.SendMsg = CmdBuf;

		for (int i = 0; i < 8; ++i) {
			this.SCNetOps.UDPBroadcastSend();
		}
	}

	private int RtkSCParseResult() {
		int recv_len = SCParam.UDPUcast.RecvLen;
		byte[] recv_buf = new byte[recv_len];
		System.arraycopy(SCParam.UDPUcast.RecvBuf, 0, recv_buf, 0, recv_len);

		if (recv_len < 9) {
			dbg.e("SCLibrary", "ACK too short\n");
			return -1;
		}

		byte flag = recv_buf[0];

		if ((flag & (SCParam.BIT(7) | SCParam.BIT(6))) != 0) {
			dbg.e("SCLibrary", "ACK version not match\n");
			return -1;
		}

		if ((flag & SCParam.BIT(5)) != 32) {
			return -1;
		}

		Message msg = Message.obtain();
		switch (flag & 0x1F) {
		case 0:
			this.SendEnable = false;
			this.ConfigSuccess = true;
			this.ScJni.StopConfig();
			int ack_ret = HandleCfgACK(recv_buf);

			return ack_ret;
		case 1:
			msg.obj = recv_buf;
			msg.what = 1;
			this.TreadMsgHandler.sendMessage(msg);
			break;
		case 2:
			msg.obj = recv_buf;
			msg.what = 2;
			this.TreadMsgHandler.sendMessage(msg);
			break;
		case 3:
			msg.obj = recv_buf;
			msg.what = 3;
			this.TreadMsgHandler.sendMessage(msg);
			break;
		case 4:
			msg.obj = recv_buf;
			msg.what = 4;
			this.TreadMsgHandler.sendMessage(msg);
			break;
		default:
			dbg.e("SCLibrary", "Unknow response");
		}

		return 0;
	}

	public void RtkSCRecvThread() {
		this.RecvEnable = true;
		this.RecvThread = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean ret = false;
				while (SCLibrary.this.RecvEnable) {
					ret = false;
					try {
						ret = SCLibrary.this.SCNetOps.UDPUnicastRecv();
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (!(ret)) {
						continue;
					}
					try {
						SCLibrary.this.RtkSCParseResult();
					} catch (Exception e) {
						e.printStackTrace();
						dbg.e("SCLibrary", "Parse Result Error");
						return;
					}
				}
			}
		});
		this.RecvThread.start();
	}

	public int rtk_sc_get_connected_sta_num() {
		return SCParam.RecvACK.MaxCfgNum;
	}

	public int rtk_sc_get_connected_sta_info(List<HashMap<String, Object>> DevInfo) {
		for (int index = 0; index < SCParam.RecvACK.MaxCfgNum; ++index) {
			String tmpStr = new String();
			HashMap hmap = new HashMap();
			for (int i = 0; i < 6; ++i) {
				tmpStr = tmpStr + String.format("%02x", new Object[] { Byte.valueOf(SCParam.RecvACK.Mac[index][i]) });
				if (i < 5) {
					tmpStr = tmpStr + ":";
				}
			}
			hmap.put("MAC", tmpStr);

			tmpStr = new String();
			switch (SCParam.RecvACK.Status[index]) {
			case 1:
				tmpStr = "Connected";
				break;
			case 2:
				tmpStr = "Profile saved";
				break;
			default:
				tmpStr = "Unkown status";
			}

			hmap.put("Status", tmpStr);

			tmpStr = new String();
			short type = (short) ((SCParam.RecvACK.Type[index][0] & 0xFF00) + (SCParam.RecvACK.Type[index][1] & 0xFF));
			switch (type) {
			case 0:
				tmpStr = "Any type";
				break;
			case 1:
				tmpStr = "TV";
				break;
			case 2:
				tmpStr = "Air conditioner";
				break;
			default:
				tmpStr = "Unkown type";
			}

			hmap.put("Type", tmpStr);

			hmap.put("IP", SCParam.RecvACK.IP[index]);

			hmap.put("Name", SCParam.RecvACK.Name[index]);

			hmap.put("PIN", SCParam.RecvACK.UsePin[index]);

			DevInfo.add(hmap);
		}

		return 0;
	}

	public int rtk_sc_send_discover_packet(byte[] cmdbuf, String send_ip) {
		try {
			SCParam.UDPBcast.IPAddr = send_ip;
			SCParam.UDPBcast.SendLen = cmdbuf.length;
			SCParam.UDPBcast.SendMsg = cmdbuf;

			this.SCNetOps.UDPBroadcastSend();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public int rtk_sc_send_control_packet(byte[] cmdbuf, String send_ip) {
		try {
			SCParam.UDPUcast.IPAddr = send_ip;
			SCParam.UDPUcast.SendLen = cmdbuf.length;
			SCParam.UDPUcast.SendMsg = cmdbuf;
			this.SCNetOps.UDPUnicastSend();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public void rtk_sc_set_packet_type(boolean pkt_type) {
		SCParam.SC_PKT_TYPE = pkt_type;
	}

	public void rtk_sc_set_softap(boolean issoftap) {
		SCParam.SC_SOFTAP_MODE = issoftap;
	}

	public String rtk_sc_get_softap_ssid() {
		return this.SCNetOps.getWifiApSSID();
	}
}
