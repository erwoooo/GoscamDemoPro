package android.goscam.net;


import java.io.IOException;
import java.util.LinkedList;

import ulife.goscam.com.loglib.dbg;

public class LanScan implements Runnable {
	private static LanScan mInstance;

	public static LanScan getInstance() {
		synchronized (LanScan.class) {
			if (mInstance == null) {
				mInstance = new LanScan();
			}
			return mInstance;
		}
	}

	private static LinkedList<LanScanCallback> mCallbacks = new LinkedList<>();

	public void addCallback(LanScanCallback cb) {
		synchronized (mCallbacks) {
			if(cb == null) {
				return;
			}
			if(!mCallbacks.contains(cb)){
				mCallbacks.add(cb);
			}
		}
	}

	public void removeCallback(LanScanCallback cb) {
		synchronized (mCallbacks) {
			if(cb == null) {
				return;
			}
			if(mCallbacks.contains(cb)){
				mCallbacks.remove(cb);
			}
		}
	}

	public static native void checkValidApp(android.content.Context ctx);

	public static native boolean isRunning();

	private static native boolean startImpl() throws IOException;

	private static native boolean scanImpl() throws IOException;

	private static native void stopImpl();

	private static native void releaseImpl() throws IOException;

	public static void onJniReceiverReady() {
		getInstance().startToScan();
	}

	public static void onJniLanScanCallback(String devUid, String wifiIP, String rcvIP, long cost, int tag) {
		dbg.D("ADD","JNI_SCAN_CALL;id="+ devUid+";ip="+wifiIP+";rip="+rcvIP+";cost="+cost+";tag="+tag);
		synchronized (mCallbacks) {
			for(LanScanCallback cb : mCallbacks) {
				if(cb != null){
					cb.onLanScanCallback(devUid, rcvIP, cost, tag);
				}
			}
		}
	}

	private Thread mRcvThread;
	private Thread mSndThread;

	private LanScan() {
	}

	private final Runnable SCANNER = new Runnable() {
		@Override
		public void run() {
			while(mRcvThread != null && isRunning()) {
				try {
					scanImpl();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	public boolean start() {
		dbg.D("ADD","lan start 1111");
		if(mRcvThread != null) {
			return false;
		}
		mRcvThread = new Thread(this);
		mRcvThread.start();
		dbg.D("ADD","lan start 2222");
		return true;
	}

	private boolean startToScan() {
		if(mSndThread != null) {
			return false;
		}
		mSndThread = new Thread(SCANNER);
		mSndThread.start();
		return true;
	}

	@Override
	public void run() {
		try{
			dbg.D("ADD","LAN startImpl 111");
			boolean retVal = startImpl();
			dbg.D("ADD","LAN startImpl 222::" + retVal);
		}catch (IOException e) {
			e.printStackTrace();
		}
		mRcvThread = null;
	}

	public void stop() {
		dbg.D("ADD","lan stop 1111");
		stopImpl();
		if(mSndThread != null && mSndThread.isAlive()) {
			mSndThread.interrupt();
		}
		mSndThread = null;
		if(mRcvThread != null && mRcvThread.isAlive()) {
			mRcvThread.interrupt();
		}
		mRcvThread = null;
	}

	public void release() {
		dbg.D("ADD","lan release 1111");
		stop();
		try {
			releaseImpl();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
