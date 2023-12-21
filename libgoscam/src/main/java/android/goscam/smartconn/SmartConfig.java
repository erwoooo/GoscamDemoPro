package android.goscam.smartconn;

import android.goscam.smartconn.SmartConnection.SmartConnectionCallback;
import android.goscam.utils.WifiCap;

import java.nio.ByteBuffer;

import ulife.goscam.com.loglib.dbg;

/**
 * 二维码信息编译器<br>
 * 格式: SSID-PWD-SECURITY-USRACCOUNT-CHECKSUM<br>
 * 如: 将要添加的摄像头，连接到WiFi(lb-test, pwd123, WPA), 并绑定到用户账号user002下<br>
 * lb-test为ssid, pwd123为密码, WPA是加密方式.<br>
 * 加密方式: 未加密用0表示, WPA用1表示, WPA2用2表示<br>
 * 需要生成二维码如下:<br>
 * 07lb-test06pwd12301107user0020855714565<br>
 * 07: lb-test长度;<br>
 * lb-test: WiFi名称<br>
 * 06: pwd123长度<br>
 * pwd123: WIFI密码<br>
 * 01: 后面1的长度<br>
 * 1: WiFi加密类型(0:NONE; 1:WPA; 2:WPA2)<br>
 * 07: user002长度<br>
 * user002: 将要绑定的用户账号<br>
 * 08: 校验码长度<br>
 * 55714565: 八位校随即验码<br>
 * 
 * @author Steve
 * @date 2014年7月11日 下午1:49:07
 * @description
 */
public class SmartConfig {
	// private final static String FMT_QRCODE = "G:T:%s;P:%s;S:%s;N:%s;F:%s";
	public enum OPT {
		CFG_WIFI("0"), CFG_WIFI_AND_BIND("1"), BIND("2");
		OPT(String code) {
			CODE = code;
		}

		public final String CODE;
	}

	/**
	 * 0: CFG_WIFI<br>
	 * 1: CFG_WIFI && BIND <br>
	 * 2: BIND<br>
	 */
	public OPT opt = OPT.CFG_WIFI_AND_BIND;
	public String ssid;
	public String pwd;
	public WifiCap type;
	public String usr;
	public String flag;
	private ByteBuffer byteBuffer;
	public String bssid;// for 8188 (mac address)
	public String phoneMac;
	public int localIp;//for ap6212a(本机ip)
	private boolean isBuilt = false;

	public String matchUid;
	public SmartConnectionCallback callback;
	public int timeoutSec;
	public byte autoMode;

	public SmartConfig() {
		// StringBuilder sb = new StringBuilder();
		// for (int i = 0; i < 8; i++) {
		// sb.append(String.valueOf((int) (Math.random() * 9)));
		// }
		// flag = sb.toString();
		flag = new StringBuffer().append(System.currentTimeMillis()).append((int) (Math.random() * 9)).append((int) (Math.random() * 9))
				.append((int) (Math.random() * 9)).append((int) (Math.random() * 9)).append((int) (Math.random() * 9)).append((int) (Math.random() * 9))
				.append((int) (Math.random() * 9)).append((int) (Math.random() * 9)).toString();
		byteBuffer = ByteBuffer.allocate(512);
	}

	private boolean addParam(String param) {
		int length = param.length() + 8;
		if (length > byteBuffer.limit()) // 空间不够，重新申请空间
		{
			byte newBuf[] = new byte[byteBuffer.capacity() + length + 512];
			int oldLen = byteBuffer.position();
			byteBuffer.flip();
			byteBuffer.get(newBuf, 0, oldLen);
			byteBuffer = ByteBuffer.wrap(newBuf);
			byteBuffer.position(oldLen);
		}
		String paramLen = String.format("%02X", param.length());
		byteBuffer.put(paramLen.getBytes());
		byteBuffer.put(param.getBytes());
		return true;
	}

	/**
	 * 当任意一个数据段发生改变时调用
	 */
	public void build() {
		byteBuffer.clear();
		if (opt == null) {
			opt = OPT.CFG_WIFI_AND_BIND;
		}
		if (ssid == null) {
			ssid = "";
		}
		if (pwd == null) {
			pwd = "";
		}
		if (type == null) {
			type = WifiCap.OPEN;
		}

		//TODO TEST 7601 调试
		usr = null;
		if (usr == null) {
			usr = "";
		}

		addParam(opt.CODE);
		addParam(ssid);
		addParam(pwd);
		addParam(String.valueOf(type.CODE));
		// addParam("123\ndfdf");//test
		addParam(usr);
		addParam(flag);
		addParam(String.valueOf(localIp));
		String info = getInfo();

		dbg.d("info======"+info);
	}

	public String getInfo() {
		return new String(byteBuffer.array(), 0, byteBuffer.position());
	}


	@Override
	public String toString() {
		return "SmartConfig{" +
				"pwd='" + pwd + '\'' +
				", ssid='" + ssid + '\'' +
				", usr='" + usr + '\'' +
				", matchUid='" + matchUid + '\'' +
				", bssid='" + bssid + '\'' +
				", phoneMac='" + phoneMac + '\'' +
				", localIp=" + localIp +
				'}';
	}
}