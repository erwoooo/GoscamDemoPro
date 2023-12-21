package android.goscam.smartconn;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

import ulife.goscam.com.loglib.dbg;

public class FileOps {
	private String SDPATH = Environment.getExternalStorageDirectory()+"/";
	private String CFGFOLDER = "rtk_sc/";
	public static String OldSsidPasswdFile = "1.txt";
	public static String NewSsidPasswdFile = "1-1.txt";
	public static String CfgPinFile = "2.txt";
	public static String CtlPinFile = "3.txt";
	public static String NounceFile = "4.txt";
	public static String MAC;
	public static String PASSWD;

	private boolean checkFileExists(String path) {
	    File file = new File(path);
	    return file.exists();
	}

	private boolean createDir(String dir) {
  		File dfile = new File(dir);
  		if(dfile.exists())
  			return true;
//		dbg.d("createDir: " + dfile);
  		return dfile.mkdir();
    }

	private boolean createFile(String file) throws Exception
	{
        File ffile = new File(file);
//		dbg.d("createFile: " + ffile);
    	return ffile.createNewFile();
	}

	private boolean deleteFile(String file) throws Exception
	{
        File ffile = new File(file);
//		dbg.d("deleteFile: " + ffile);
    	return ffile.delete();
	}

	public void SetKey(String key)
	{
		MAC = key;
		CreateNounceFile();
		PASSWD = ParseNounceFile() + MAC;
//		dbg.d("PASSWD: " + PASSWD);
	}


	protected RandomAccessFile openFile(String filename) throws Exception
    {
        RandomAccessFile rf = null;
		String dir = SDPATH + CFGFOLDER;
		String file = dir + filename;
		dbg.d("openFile: " + file);

		if(!checkFileExists(file)) {
			if(!createDir(dir)) {
				dbg.e("Create Dir Error");
				return null;
			}
			if(!createFile(file)) {
				dbg.e("Create File Error");
				return null;
			}
		}

        rf = new RandomAccessFile(file, "rw");

        return rf;
    }

    protected void writeFile(RandomAccessFile rf, String str, boolean encrypt) throws Exception
    {
    	if(rf==null) {
    		return;
    	}

    	if(encrypt) {
        	String estr = AESCrypt.encrypt(PASSWD, str);
//    		dbg.d("estr: " + estr);
            rf.writeBytes(estr);
    	} else {
            rf.writeBytes(str);
    	}
    }

    protected String readFile(RandomAccessFile rf, boolean encrypt) throws Exception
    {
        String str = null;
        byte[] strbuf = null;
        int len = 0 ;

    	if(rf==null) {
    		return null;
    	}

        len = (int)rf.length();
		if(len==0) {
			return null;
		}

        strbuf = new byte[len];
        rf.read(strbuf, 0, len);
//        for(int i=0; i<strbuf.length; i++)
//    		dbg.d("" + strbuf[i]);
        str = new String(strbuf);
//		dbg.d("read str: " + str);

        if(encrypt) {
        	String dstr = AESCrypt.decrypt(PASSWD, str);
//    		dbg.d("decrypt str: " + dstr);
            return dstr;
        } else {
        	return str;
        }
    }

    protected void closeFile(RandomAccessFile rf) throws Exception
    {
    	if(rf==null)
    		return;

        rf.close();
    }


	public void ParseSsidPasswdFile(String ssid)
	{
    	RandomAccessFile rf = null;
    	String str = null;

		try {
			rf = openFile(FileOps.NewSsidPasswdFile);
		} catch (Exception e1) {
			e1.printStackTrace();
			dbg.e("Open File Error");
			return;
		}

		try {
			str = readFile(rf, true);
		} catch (Exception e) {
			e.printStackTrace();
			dbg.e("Read File Error");
			try {
				rf.setLength(0); //clear file
			} catch (IOException e1) {
				e1.printStackTrace();
				dbg.e("Set Length Error");
				return;
			}
			return;
		}
		dbg.d("Read: " + str);

		SCParams.StoredPasswd = new String();
		if(str==null) {
			dbg.e("Null File");
		} else {
		    String[] items = str.split("\\|");
		    for(int i=0; i<items.length; i++) {
//				System.out.printf("items[%d]: %s\n", i, items[i]);
			    String[] subitems = items[i].split("\\:");
//			    for(int j=0; j<subitems.length; j++) {
//					dbg.d(String.format("subitems[%d]: %s\n", j, subitems[j]));
//			    }
//				dbg.d(String.format("Selected SSID: %s\n", ssid));
			    if(subitems.length>1) { //make sure password area exists
				    if(ssid.equals(subitems[0]) && !subitems[1].equals("null")) {
				    	SCParams.StoredPasswd += subitems[1];
//						dbg.d("Find already existed SSID");
				    	break;
				    }
			    }
		    }
		}

    	try {
    		closeFile(rf);
		} catch (Exception e) {
			e.printStackTrace();
			dbg.e("Close File Error");
		}
	}

	public void UpdateSsidPasswdFile()
	{
//		dbg.d("isOpenNetwork: " + isOpenNetwork);
		if(!SCParams.IsOpenNetwork && (SCParams.ConnectedPasswd==null|| SCParams.ConnectedPasswd.length()==0)) {
//			dbg.d("SCCtlOps.ConnectedPasswd: " + SCCtlOps.ConnectedPasswd);
			return;
		}

    	RandomAccessFile rf;
		try {
			rf = openFile(FileOps.NewSsidPasswdFile);
		} catch (Exception e2) {
			e2.printStackTrace();
			dbg.e("Open File Error");
    		return;
		}

    	long len=0;
    	boolean isOld = false;
		String getstr = new String();
		String setstr = new String();
    	try {
			len = rf.length();
		} catch (IOException e1) {
			e1.printStackTrace();
			dbg.e("Get Length Error");
    		return;
		}
		if(len>0) {
			try {
				getstr = readFile(rf, true);
			} catch (Exception e) {
				e.printStackTrace();
				dbg.e("Read File Error");
	    		return;
			}
//			dbg.d("getstr: " + getstr);

		    String[] items = getstr.split("\\|");
		    for(int i=0; i<items.length; i++) {
//				System.out.printf("items[%d]: %s\n", i, items[i]);
			    String[] subitems = items[i].split("\\:");
//			    for(int j=0; j<subitems.length; j++) {
//					System.out.printf("subitems[%d]: %s\n", j, subitems[j]);
//			    }
			    if(SCParams.ConnectedSSID.equals(subitems[0])) {
			    	isOld = true;
//					dbg.d("Refresh old");
			    	if(SCParams.IsOpenNetwork)
			    		setstr += SCParams.ConnectedSSID + ":null|";
			    	else
			    		setstr += SCParams.ConnectedSSID + ":" + SCParams.ConnectedPasswd + "|";
			    } else {
//					dbg.d("Re-Add existed");
					setstr += subitems[0] + ":" + subitems[1] + "|";
			    }
		    }
		}
		if(!isOld) {
//			dbg.d("Add new");
	    	if(SCParams.IsOpenNetwork)
	    		setstr += SCParams.ConnectedSSID + ":null|";
	    	else
	    		setstr += SCParams.ConnectedSSID + ":" + SCParams.ConnectedPasswd + "|";
		}

//		dbg.d("setstr: " + setstr);
		try {
			rf.setLength(0); //clear file
			rf.seek(0); //re-write all info
		} catch (IOException e) {
			e.printStackTrace();
			dbg.e("Re-Seek Error");
    		return;
		}
    	try {
    		writeFile(rf, setstr, true);
		} catch (Exception e) {
			e.printStackTrace();
			dbg.e("Write File Error");
    		return;
		}

    	try {
    		closeFile(rf);
		} catch (Exception e) {
			e.printStackTrace();
			dbg.e("Close File Error");
    		return;
		}
	}

	public void UpgradeSsidPasswdFile()
	{
		String password = PASSWD; //backup it first

		String old_file = SDPATH + CFGFOLDER + FileOps.OldSsidPasswdFile;
		dbg.d("old_file: " + old_file);
		if(!checkFileExists(old_file)) {
			dbg.d("Old File does not exist.");
			return;
		}

    	RandomAccessFile rf_old;
		try {
			rf_old = openFile(FileOps.OldSsidPasswdFile);
		} catch (Exception e2) {
			e2.printStackTrace();
			dbg.e("Open Old File Error");
    		return;
		}

    	long len=0;
		String getstr = new String();
    	try {
			len = rf_old.length();
		} catch (IOException e1) {
			e1.printStackTrace();
			dbg.e("Get Length Error");
    		return;
		}
		if(len>0) {
			try {
				PASSWD = MAC; //for old encryption
				dbg.d("Old key: " + PASSWD);
				getstr = readFile(rf_old, true);
			} catch (Exception e) {
				e.printStackTrace();
				dbg.e("Read File Error");
	    		return;
			}
			dbg.d("getstr: " + getstr);
		}

    	try {
    		closeFile(rf_old);
		} catch (Exception e) {
			e.printStackTrace();
			dbg.e("Close File Error");
    		return;
		}

    	PASSWD = password; //for new encryption
//		dbg.d("New key: " + PASSWD);
    	RandomAccessFile rf_new;
		try {
			rf_new = openFile(FileOps.NewSsidPasswdFile);
		} catch (Exception e2) {
			e2.printStackTrace();
			dbg.e("Open File Error");
    		return;
		}

		try {
			rf_new.setLength(0); //clear file
			rf_new.seek(0); //re-write all info
		} catch (IOException e) {
			e.printStackTrace();
			dbg.e("Re-Seek Error");
    		return;
		}
    	try {
    		writeFile(rf_new, getstr, true);
		} catch (Exception e) {
			e.printStackTrace();
			dbg.e("Write File Error");
    		return;
		}
		dbg.i("Upgrade File Success.");

    	try {
    		closeFile(rf_new);
		} catch (Exception e) {
			e.printStackTrace();
			dbg.e("Close File Error");
    		return;
		}

		try {
			deleteFile(old_file);
		} catch (Exception e1) {
			dbg.e("Delete File Error");
			e1.printStackTrace();
		}
	}

	public String ParseCfgPinFile()
	{
    	RandomAccessFile rf = null;
    	String getstr = null;

		try {
			rf = openFile(FileOps.CfgPinFile);
		} catch (Exception e1) {
			e1.printStackTrace();
			dbg.e("Open File Error");
			return null;
		}

		try {
			getstr = readFile(rf, true);
		} catch (Exception e) {
			e.printStackTrace();
			dbg.e("Read File Error");
			try {
				rf.setLength(0); //clear file
			} catch (IOException e1) {
				e1.printStackTrace();
				dbg.e("Set Length Error");
				return null;
			}
			return null;
		}
//		dbg.d("Read: " + getstr);

    	try {
    		closeFile(rf);
		} catch (Exception e) {
			e.printStackTrace();
			dbg.e("Close File Error");
		}

		return getstr;
	}

	public void UpdateCfgPinFile(String pin)
	{
    	RandomAccessFile rf;
		try {
			rf = openFile(FileOps.CfgPinFile);
		} catch (Exception e2) {
			e2.printStackTrace();
			dbg.e("Open File Error");
    		return;
		}

		try {
			rf.setLength(0); //clear file
			rf.seek(0); //re-write all info
		} catch (IOException e) {
			e.printStackTrace();
			dbg.e("Re-Seek Error");
    		return;
		}

    	try {
    		writeFile(rf, pin, true);
		} catch (Exception e) {
			e.printStackTrace();
			dbg.e("Write File Error");
    		return;
		}

    	try {
    		closeFile(rf);
		} catch (Exception e) {
			e.printStackTrace();
			dbg.e("Close File Error");
    		return;
		}
	}

	public String ParseCtlPinFile(String mac)
	{
    	RandomAccessFile rf = null;
    	String getstr = null;
    	String pin = null;

		try {
			rf = openFile(FileOps.CtlPinFile);
		} catch (Exception e1) {
			e1.printStackTrace();
			dbg.e("Open File Error");
			return null;
		}

		try {
			getstr = readFile(rf, true);
		} catch (Exception e) {
			e.printStackTrace();
			dbg.e("Read File Error");
			try {
				rf.setLength(0); //clear file
			} catch (IOException e1) {
				e1.printStackTrace();
				dbg.e("Set Length Error");
				return null;
			}
			return null;
		}
//		dbg.d("Read: " + getstr);

		if(getstr==null) {
			dbg.e("Null File");
		} else {
		    String[] items = getstr.split("\\;");
		    for(int i=0; i<items.length; i++) {
//				System.out.printf("items[%d]: %s\n", i, items[i]);
			    String[] subitems = items[i].split("\\|");
//			    for(int j=0; j<subitems.length; j++) {
//					dbg.d(String.format("subitems[%d]: %s\n", j, subitems[j]));
//			    }
//				dbg.d(String.format("Selected MAC: %s\n", mac));
			    if(subitems.length>1) { //make sure pin area exists
				    if(mac.equals(subitems[0]) && !subitems[1].equals("null")) {
				    	pin = subitems[1];
//						dbg.d("Find PIN");
				    	break;
				    }
			    }
		    }
		}

    	try {
    		closeFile(rf);
		} catch (Exception e) {
			e.printStackTrace();
			dbg.e("Close File Error");
		}

		return pin;
	}

	public void UpdateCtlPinFile(String mac, String pin)
	{
    	RandomAccessFile rf;
		try {
			rf = openFile(FileOps.CtlPinFile);
		} catch (Exception e2) {
			e2.printStackTrace();
			dbg.e("Open File Error");
    		return;
		}

    	long len=0;
    	boolean isOld = false;
		String getstr = new String();
		String setstr = new String();
    	try {
			len = rf.length();
		} catch (IOException e1) {
			e1.printStackTrace();
			dbg.e("Get Length Error");
    		return;
		}
		if(len>0) {
			try {
				getstr = readFile(rf, true);
			} catch (Exception e) {
				e.printStackTrace();
				dbg.e("Read File Error");
	    		return;
			}
//			dbg.d("getstr: " + getstr);

		    String[] items = getstr.split("\\;");
		    for(int i=0; i<items.length; i++) {
//				System.out.printf("items[%d]: %s\n", i, items[i]);
			    String[] subitems = items[i].split("\\|");
//			    for(int j=0; j<subitems.length; j++) {
//					System.out.printf("subitems[%d]: %s\n", j, subitems[j]);
//			    }
			    if(mac.equals(subitems[0])) {
			    	isOld = true;
//					dbg.d("Refresh old");
				    setstr += subitems[0] + "|" + pin + ";";
			    } else {
//					dbg.d("Re-Add existed");
				    setstr += subitems[0] + "|" + subitems[1] + ";";
			    }
		    }
		}
		if(!isOld) {
//			dbg.d("Add new");
	    	setstr += mac + "|" + pin + ";";
		}

//		dbg.d("setstr: " + setstr);
		try {
			rf.setLength(0); //clear file
			rf.seek(0); //re-write all info
		} catch (IOException e) {
			e.printStackTrace();
			dbg.e("Re-Seek Error");
    		return;
		}
    	try {
    		writeFile(rf, setstr, true);
		} catch (Exception e) {
			e.printStackTrace();
			dbg.e("Write File Error");
    		return;
		}

    	try {
    		closeFile(rf);
		} catch (Exception e) {
			e.printStackTrace();
			dbg.e("Close File Error");
    		return;
		}
	}

	private void CreateNounceFile()
	{
    	RandomAccessFile rf;
		try {
			rf = openFile(FileOps.NounceFile);
		} catch (Exception e2) {
			e2.printStackTrace();
			dbg.e("Open File Error");
    		return;
		}

		try {
			if(rf.length()==64) {
//				dbg.d("Nonce file existed.");
		    	try {
		    		closeFile(rf);
				} catch (Exception e) {
					e.printStackTrace();
					dbg.e("Close File Error");
		    		return;
				}
				return;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}

		/** Nonce */
		byte[] Nonce = new byte[64];
		String NonceStr = new String();
		for(int i=0; i<64; i++) {
    		Random r = new Random();
    		Nonce[i] = (byte) (r.nextInt(94)+32);
//    		System.out.printf("%02x ", Nonce[i]);
    		NonceStr += String.format("%c", Nonce[i]);
		}
//		System.out.printf("\n");
//		dbg.d("NonceStr: " + NonceStr);

    	try {
			rf.setLength(0); //clear file
			rf.seek(0); //re-write all info
    		writeFile(rf, NonceStr, false);
		} catch (Exception e) {
			e.printStackTrace();
			dbg.e("Write File Error");
    		return;
		}

    	try {
    		closeFile(rf);
		} catch (Exception e) {
			e.printStackTrace();
			dbg.e("Close File Error");
    		return;
		}
	}

	private String ParseNounceFile()
	{
    	RandomAccessFile rf = null;
    	String getstr = null;

		try {
			rf = openFile(FileOps.NounceFile);
		} catch (Exception e1) {
			e1.printStackTrace();
			dbg.e("Open File Error");
			return null;
		}

		try {
			getstr = readFile(rf, false);
		} catch (Exception e) {
			e.printStackTrace();
			dbg.e("Read File Error");
			try {
				rf.setLength(0); //clear file
			} catch (IOException e1) {
				e1.printStackTrace();
				dbg.e("Set Length Error");
				return null;
			}
			return null;
		}
//		dbg.d("Read: " + getstr);

    	try {
    		closeFile(rf);
		} catch (Exception e) {
			e.printStackTrace();
			dbg.e("Close File Error");
		}

		return getstr;
	}
}
