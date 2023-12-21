package android.goscam.smartconn;

public class _8188ConnectParams {
	private String ssid;
	private String passwd;
	private String pin = "57289961";
	private String bssid;
	private boolean pkt_type = false;
	private boolean issoftap = false;
	private int total_time = 120000;
	private int old_mode_time = 0;
	private byte profile_rounds = 1;
	private int profile_interval = 1000;
	private int packet_interval = 0;
	private byte packet_counts = 1;
	private String wifi_interface = "";
	private String hostip = "";
	private String phoneMac;

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public boolean isPkt_type() {
		return pkt_type;
	}

	public void setPkt_type(boolean pkt_type) {
		this.pkt_type = pkt_type;
	}

	public boolean isIssoftap() {
		return issoftap;
	}

	public void setIssoftap(boolean issoftap) {
		this.issoftap = issoftap;
	}

	public int getTotal_time() {
		return total_time;
	}

	public void setTotal_time(int total_time) {
		this.total_time = total_time;
	}

	public int getOld_mode_time() {
		return old_mode_time;
	}

	public void setOld_mode_time(int old_mode_time) {
		this.old_mode_time = old_mode_time;
	}

	public byte getProfile_rounds() {
		return profile_rounds;
	}

	public void setProfile_rounds(byte profile_rounds) {
		this.profile_rounds = profile_rounds;
	}

	public int getProfile_interval() {
		return profile_interval;
	}

	public void setProfile_interval(int profile_interval) {
		this.profile_interval = profile_interval;
	}

	public byte getPacket_counts() {
		return packet_counts;
	}

	public void setPacket_counts(byte packet_counts) {
		this.packet_counts = packet_counts;
	}

	public String getWifi_interface() {
		return wifi_interface;
	}

	public void setWifi_interface(String wifi_interface) {
		this.wifi_interface = wifi_interface;
	}

	public String getPhoneMac() {
		return phoneMac;
	}

	public void setPhoneMac(String phoneMac) {
		this.phoneMac = phoneMac;
	}

	public int getPacket_interval() {
		return packet_interval;
	}

	public void setPacket_interval(int packet_interval) {
		this.packet_interval = packet_interval;
	}

	public String getHostip() {
		return hostip;
	}

	public void setHostip(String hostip) {
		this.hostip = hostip;
	}

}
