package android.goscam.utils;

/**
 * WiFi加密方式
 * <br>Capability
 * @author Steve
 * @date 2014年7月11日 下午1:53:06
 * @description
 */
public enum WifiCap {
	OPEN("NONE", 0), WPA("WPA-PSK", 1), WPA2("WPA2-PSK", 2);
	public String DESC;
	public int CODE;

	WifiCap(String desc, int type) {
		DESC = desc;
		CODE = type;
	}
}
