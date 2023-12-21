package android.goscam.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;

import java.util.Iterator;
import java.util.List;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class WifiUtils {
	/**
	 * Use Permission
	 * 
	 * @author Stephen, 253407292@qq.com
	 * @date 2015年5月14日 下午5:42:37
	 * @version V1.0.001
	 * @param c
	 * @return
	 */
	public static String getSsidConnected(Context c) {
		WifiManager wifi = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
		if (wifi.isWifiEnabled()) {
			WifiInfo info = wifi.getConnectionInfo();
			String ssid = info.getSSID();
			int iLen = ssid.length();
			if (iLen == 0) {
				return null;
			}
			if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
				return ssid.substring(1, iLen - 1);
			}
		}
		return null;
	}

	/**
	 * 
	 * @author Stephen, 253407292@qq.com
	 * @date 2015年5月14日 下午5:45:08
	 * @version V1.0.001
	 * @param c
	 * @param ssid
	 *            Which SSID to be matched.
	 * @return
	 */
	public static byte getAuthMode(Context c, String ssid) {
		WifiManager wifi = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
		return getAuthMode(wifi.getScanResults(), ssid);
	}

	/**
	 * 
	 * @author Stephen, 253407292@qq.com
	 * @date 2015年5月14日 下午5:43:11
	 * @version V1.0.001
	 * @param results
	 * @param ssid
	 *            Which SSID to be matched.
	 * @return
	 */
	public static byte getAuthMode(List<ScanResult> results, String ssid) {
		if (results == null || ssid == null || ssid.isEmpty()) {
			return -1;
		}
		for (int i = 0, len = results.size(); i < len; i++) {
			ScanResult sr = results.get(i);
			if (sr.SSID.equals(ssid)) {
				return getAuthMode(sr);
			}
		}
		return -1;
	}

	/**
	 * 
	 * @author Stephen, 253407292@qq.com
	 * @date 2015年5月14日 下午5:43:16
	 * @version V1.0.001
	 * @param sr
	 * @return
	 */
	public static byte getAuthMode(ScanResult sr) {
		byte AuthModeOpen = 0x00;
		byte AuthModeWPA = 0x03;
		byte AuthModeWPAPSK = 0x04;
		byte AuthModeWPA2 = 0x06;
		byte AuthModeWPA2PSK = 0x07;
		byte AuthModeWPA1WPA2 = 0x08;
		byte AuthModeWPA1PSKWPA2PSK = 0x09;
		boolean WpaPsk = sr.capabilities.contains("WPA-PSK");
		boolean Wpa2Psk = sr.capabilities.contains("WPA2-PSK");
		boolean Wpa = sr.capabilities.contains("WPA-EAP");
		boolean Wpa2 = sr.capabilities.contains("WPA2-EAP");

		if (sr.capabilities.contains("WEP")) {
			return AuthModeOpen;
		}
		if (WpaPsk && Wpa2Psk) {
			return AuthModeWPA1PSKWPA2PSK;
		} else if (Wpa2Psk) {
			return AuthModeWPA2PSK;
		} else if (WpaPsk) {
			return AuthModeWPAPSK;
		}
		if (Wpa && Wpa2) {
			return AuthModeWPA1WPA2;
		} else if (Wpa2) {
			return AuthModeWPA2;
		} else if (Wpa) {
			return AuthModeWPA;
		}
		return AuthModeOpen;
	}

	public static final int INVALID_NETWORK_ID = -1;

	/**
	 * These values are matched in string arrays -- changes must be kept in sync
	 */
	public static final int SECURITY_NONE = 0;
	public static final int SECURITY_WEP = 1;
	public static final int SECURITY_PSK = 2;
	public static final int SECURITY_EAP = 3;

	public enum Security {
		NONE, WEP, PSK, EAP
	}

	public enum PskType {
		UNKNOWN, WPA, WPA2, WPA_WPA2
	}

	public static PskType getPskType(ScanResult result) {
		boolean wpa = result.capabilities.contains("WPA-PSK");
		boolean wpa2 = result.capabilities.contains("WPA2-PSK");
		if (wpa2 && wpa) {
			return PskType.WPA_WPA2;
		} else if (wpa2) {
			return PskType.WPA2;
		} else if (wpa) {
			return PskType.WPA;
		} else {
			return PskType.UNKNOWN;
		}
	}

	public static boolean removeWifiConfig(WifiManager wm, String ssid) {
		if (TextUtils.isEmpty(ssid) || wm == null) {
			return false;
		}
		List<WifiConfiguration> cfgs = wm.getConfiguredNetworks();
		if (cfgs == null) {
			return true;
		}
		Iterator<WifiConfiguration> it = cfgs.iterator();
		while (it.hasNext()) {
			WifiConfiguration cfg = it.next();
			if (ssid.equals(removeDoubleQuotes(cfg.SSID))) {
				boolean ret = wm.removeNetwork(cfg.networkId);
				return ret;
			}
		}
		return false;
	}

	public static int getSecurity(WifiConfiguration config) {
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
			return SECURITY_PSK;
		}
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
			return SECURITY_EAP;
		}
		return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
	}

	public static int getSecurity(ScanResult result) {
		if (result.capabilities.contains("WEP")) {
			//
			return SECURITY_WEP;
		} else if (result.capabilities.contains("PSK")) {
			return SECURITY_PSK;
		} else if (result.capabilities.contains("EAP")) {
			return SECURITY_EAP;
		}
		return SECURITY_NONE;
	}

	public static String getSecurityString(int type) {
		String secure;
		switch (type) {
		case SECURITY_EAP:
			secure = "EAP";
			break;
		case SECURITY_PSK:
			secure = "PSK";
			break;
		case SECURITY_WEP:
			secure = "WEP";
			break;
		case SECURITY_NONE:
		default:
			// secure = "NONE";
			secure = "OPEN";
			break;
		}
		return secure;
	}

	public String getPrintableSsid(String SSID) {
		if (SSID == null) {
			return "";
		}
		final int length = SSID.length();
		if (length > 2 && (SSID.charAt(0) == '"') && SSID.charAt(length - 1) == '"') {
			return SSID.substring(1, length - 1);
		}

		// /** The ascii-encoded string format is P"<ascii-encoded-string>"
		// * The decoding is implemented in the supplicant for a newly
		// configured
		// * network.
		// */
		// if (length > 3 && (SSID.charAt(0) == 'P') && (SSID.charAt(1) == '"')
		// &&
		// (SSID.charAt(length-1) == '"')) {
		// WifiSsid wifiSsid = WifiSsid.createFromAsciiEncoded(
		// SSID.substring(2, length - 1));
		// return wifiSsid.toString();
		// }
		return SSID;
	}

	public static String removeDoubleQuotes(String string) {
		if (string == null) {
			return null;
		}
		int length = string.length();
		if ((length > 1) && (string.charAt(0) == '"') && (string.charAt(length - 1) == '"')) {
			return string.substring(1, length - 1);
		}
		return string;
	}

	public static String convertToQuotedString(String string) {
		return "\"" + string + "\"";
	}

	public enum ProxySettings {
		/*
		 * No proxy is to be used. Any existing proxy settings should be
		 * cleared.
		 */
		NONE,
		/*
		 * Use statically configured proxy. Configuration can be accessed with
		 * linkProperties
		 */
		STATIC,
		/*
		 * no proxy details are assigned, this is used to indicate that any
		 * existing proxy settings should be retained
		 */
		UNASSIGNED
	}

	public static WifiConfiguration getConfig(String ssid, String pwd, int security) {
		if (pwd == null) {
			pwd = "";
		}
		WifiConfiguration config = new WifiConfiguration();
		config.SSID = convertToQuotedString(ssid);
		config.networkId = INVALID_NETWORK_ID;
		switch (security) {
		case SECURITY_NONE:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			break;

		case SECURITY_WEP:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			if (pwd.length() != 0) {
				int length = pwd.length();
				String password = pwd;
				// WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
				if ((length == 10 || length == 26 || length == 58) && password.matches("[0-9A-Fa-f]*")) {
					config.wepKeys[0] = password;
				} else {
					config.wepKeys[0] = '"' + password + '"';
				}
			}
			break;
		case SECURITY_PSK:
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			if (pwd.length() != 0) {
				String password = pwd.toString();
				if (password.matches("[0-9A-Fa-f]{64}")) {
					config.preSharedKey = password;
				} else {
					config.preSharedKey = '"' + password + '"';
				}
			}
			break;
		case SECURITY_EAP:
			// config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
			// config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
			// config.enterpriseConfig = new WifiEnterpriseConfig();
			// int eapMethod = mEapMethodSpinner.getSelectedItemPosition();
			// int phase2Method = mPhase2Spinner.getSelectedItemPosition();
			// config.enterpriseConfig.setEapMethod(eapMethod);
			// switch (eapMethod) {
			// case Eap.PEAP:
			// // PEAP supports limited phase2 values
			// // Map the index from the PHASE2_PEAP_ADAPTER to the one used
			// // by the API which has the full list of PEAP methods.
			// switch(phase2Method) {
			// case WIFI_PEAP_PHASE2_NONE:
			// config.enterpriseConfig.setPhase2Method(Phase2.NONE);
			// break;
			// case WIFI_PEAP_PHASE2_MSCHAPV2:
			// config.enterpriseConfig.setPhase2Method(Phase2.MSCHAPV2);
			// break;
			// case WIFI_PEAP_PHASE2_GTC:
			// config.enterpriseConfig.setPhase2Method(Phase2.GTC);
			// break;
			// default:
			// Log.e(TAG, "Unknown phase2 method" + phase2Method);
			// break;
			// }
			// break;
			// default:
			// // The default index from PHASE2_FULL_ADAPTER maps to the API
			// config.enterpriseConfig.setPhase2Method(phase2Method);
			// break;
			// }
			// String caCert = (String) mEapCaCertSpinner.getSelectedItem();
			// if (caCert.equals(unspecifiedCert)) caCert = "";
			// config.enterpriseConfig.setCaCertificateAlias(caCert);
			// String clientCert = (String)
			// mEapUserCertSpinner.getSelectedItem();
			// if (clientCert.equals(unspecifiedCert)) clientCert = "";
			// config.enterpriseConfig.setClientCertificateAlias(clientCert);
			// config.enterpriseConfig.setIdentity(mEapIdentityView.getText().toString());
			// config.enterpriseConfig.setAnonymousIdentity(
			// mEapAnonymousView.getText().toString());
			//
			// if (mPasswordView.isShown()) {
			// // For security reasons, a previous password is not displayed to
			// user.
			// // Update only if it has been changed.
			// if (mPasswordView.length() > 0) {
			// config.enterpriseConfig.setPassword(mPasswordView.getText().toString());
			// }
			// } else {
			// // clear password
			// config.enterpriseConfig.setPassword(mPasswordView.getText().toString());
			// }
			// break;
		default:
			return null;
		}
		// try {
		// // config.proxySettings = mProxySettings;
		// // config.ipAssignment = mIpAssignment;
		// // config.linkProperties = new LinkProperties(mLinkProperties);
		// Class ProxySettings = null;
		// try {
		// ProxySettings = Class
		// .forName("android.net.wifi.WifiConfiguration.ProxySettings");
		// } catch (Exception e) {
		// // TODO: handle exception
		// dbg.e(TAG, "" + e);
		// }
		// Field proxySettings = config.getClass().getDeclaredField(
		// "proxySettings");
		// /* or"NONE" */
		// proxySettings
		// .set(config, Enum.valueOf(ProxySettings, "UNASSIGNED"));
		// Class IpAssignment = Class
		// .forName("android.net.wifi.WifiConfiguration.IpAssignment");
		// Field ipAssignment = config.getClass().getDeclaredField(
		// "ipAssignment");
		// /* or"DHCP" */
		// ipAssignment.set(config, Enum.valueOf(ProxySettings, "UNASSIGNED"));
		// Class LinkProperties = Class.forName("android.net.LinkProperties");
		// Field linkProperties = config.getClass().getDeclaredField(
		// "linkProperties");
		// linkProperties.set(config, LinkProperties.newInstance());
		// } catch (NoSuchFieldException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IllegalArgumentException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IllegalAccessException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (ClassNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (InstantiationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return config;
	}
}
