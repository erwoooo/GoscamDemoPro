package android.goscam.utils;

import android.graphics.BitmapFactory;
import android.text.TextUtils;

/**
 * Created by zzkong on 2017/7/6.
 */

public class PicUtil {

    public static String getPicType(String pathName) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        String type = options.outMimeType;
        if (!TextUtils.isEmpty(type)) {
            try {
                type = type.substring(6, type.length());
                if ("gif".equals(type)) {
                    return ".gif";
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        return ".jpg";
    }
}
