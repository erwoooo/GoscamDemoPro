package com.gocam.goscamdemopro.utils;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

/**
 * 二维码图生成器<br>
 * 根据给定的文本, 生成指定大小的二维码图
 */
public class QRBitmapCreater implements Runnable {

	/**
	 * 二维码图生成器回调接口
	 */
	public interface QRBitmapCallback {
		/**
		 * 回调接口
		 * 
		 * @param bmp
		 *            若为null, 表示创建失败. 可以优先判断created..
		 * @param created
		 *            是否成功创建二维码图片
		 */
		public void onQRBitmapCreated(Bitmap bmp, boolean created);
	}

	private final QRBitmapCallback cb;
	private final Handler uiHandler;
	private final String info;
	private final int width, height;

	/**
	 * 二维码图生成器
	 * 
	 *
	 *            UI线程的Handler.
	 * @param info
	 *            二维码的文本内容
	 * @param w
	 *            需要生成的二维码的宽度
	 * @param h
	 *            需要生成的二维码的高度
	 * @param cb
	 *            结果回调接口
	 */
	public QRBitmapCreater(String info, int w, int h, QRBitmapCallback cb) {
		this.uiHandler = new Handler(Looper.getMainLooper());
		this.info = info;
		width = w;
		height = h;
		this.cb = cb;
	}

	public void execute() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		Bitmap bmp = null;
		boolean created = false;
		try {
			// 需要引入core包
			QRCodeWriter writer = new QRCodeWriter();
			dbg.d("生成的文本：" + info);
			// 把输入的文本转为二维码
			BitMatrix martix = writer.encode(info, BarcodeFormat.QR_CODE, width, height);
			dbg.d("w:" + martix.getWidth() + "h:" + martix.getHeight());
			/* UTF-8编码文本内容 */
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			/* 生成点阵图 */
			BitMatrix bitMatrix = new QRCodeWriter().encode(info, BarcodeFormat.QR_CODE, width, height, hints);

			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					} else {
						pixels[y * width + x] = 0xffffffff;
					}
				}
			}
			/* 创建一张图片 */
			bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			/* 像素填充 */
			bmp.setPixels(pixels, 0, width, 0, 0, width, height);
			created = true;
		} catch (WriterException e) {
			dbg.e("er: ", e);
			e.printStackTrace();
		} catch (Exception e) {
			dbg.e("er: ", e);
			e.printStackTrace();
		} finally {
			/* 调用回调接口函数 */
			uiHandler.post(new QRBitmapHandler(bmp, created) {
				@Override
				public void handleQRBitmap(Bitmap bmp, boolean created) {
					cb.onQRBitmapCreated(bmp, created);
				}
			});
		}
	}

	/**
	 * 二维码图片结果处理虚类
	 */
	private abstract class QRBitmapHandler implements Runnable {
		private final Bitmap bmp;
		private final boolean created;

		public QRBitmapHandler(Bitmap bmp, boolean created) {
			this.bmp = bmp;
			this.created = created;
		}

		/**
		 * 实际实现的处理函数
		 * 
		 * @param bmp
		 * @param created
		 */
		public abstract void handleQRBitmap(Bitmap bmp, boolean created);

		@Override
		public void run() {
			handleQRBitmap(bmp, created);
		}
	}
}
