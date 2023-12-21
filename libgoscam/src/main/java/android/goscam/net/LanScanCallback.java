package android.goscam.net;

public interface LanScanCallback {
	void onLanScanCallback(String uid, String ip, long costMs, int tag);
}
