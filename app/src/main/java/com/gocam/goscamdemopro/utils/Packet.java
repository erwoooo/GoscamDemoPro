/******************************************************************************
 *                                                                            *
 * Copyright (c) 2011 by TUTK Co.LTD. All Rights Reserved.                    *
 *                                                                            *
 *                                                                            *
 * Class: Packet.java                                                         *
 *                                                                            *
 * Author: joshua ju                                                          *
 *                                                                            *
 * Date: 2011-05-14                                                           *
 *                                                                            *
 ******************************************************************************/

package com.gocam.goscamdemopro.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Packet {

	public static String bytesToHex(byte[] data, int offset, int count) {
		if (offset < 0 || count < 0 || data == null || data.length < offset
				|| data.length < offset + count)
			return null;
		StringBuilder sb = new StringBuilder();
		int size = offset + count;
		for (int i = offset; i < offset + count; i++) {
			sb.append(String.format("%02x", data[i]));
			if (i != size - 1) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	public static final short byteArrayToShort_Little(byte byt[], int nBeginPos) {
		return (short) ((0xff & byt[nBeginPos]) | ((0xff & byt[nBeginPos + 1]) << 8));
	}

	public static final int byteArrayToInt_Little(byte byt[], int nBeginPos) {
		return (0xff & byt[nBeginPos]) | (0xff & byt[nBeginPos + 1]) << 8
				| (0xff & byt[nBeginPos + 2]) << 16
				| (0xff & byt[nBeginPos + 3]) << 24;
	}

	public static final int byteArrayToInt_Big(byte byt[], int nBeginPos) {
		return (0xff & byt[nBeginPos] << 24)
				| (0xff & byt[nBeginPos + 1]) << 16
				| (0xff & byt[nBeginPos + 2]) << 8
				| (0xff & byt[nBeginPos + 3]);
	}

	public static final int byteArrayToInt_Little(byte byt[]) {
		if (byt.length == 1)
			return 0xff & byt[0];
		else if (byt.length == 2)
			return (0xff & byt[0]) | ((0xff & byt[1]) << 8);
		else if (byt.length >= 4)
			return (0xff & byt[0]) | (0xff & byt[1]) << 8
					| (0xff & byt[2]) << 16 | (0xff & byt[3]) << 24;
		else
			return 0;
	}

	public static final long byteArrayToLong_Little(byte byt[], int nBeginPos) {
		return (0xff & byt[nBeginPos]) | (0xff & byt[nBeginPos + 1]) << 8
				| (0xff & byt[nBeginPos + 2]) << 16
				| (0xff & byt[nBeginPos + 3]) << 24
				| (0xff & byt[nBeginPos + 4]) << 32
				| (0xff & byt[nBeginPos + 5]) << 40
				| (0xff & byt[nBeginPos + 6]) << 48
				| (0xff & byt[nBeginPos + 7]) << 56;
	}

	public static final int byteArrayToInt_Big(byte byt[]) {
		if (byt.length == 1)
			return 0xff & byt[0];
		else if (byt.length == 2)
			return (0xff & byt[0]) << 8 | 0xff & byt[1];
		else if (byt.length >= 4)
			return (0xff & byt[0]) << 24 | (0xff & byt[1]) << 16
					| (0xff & byt[2]) << 8 | 0xff & byt[3];
		else
			return 0;
	}

	public static final byte[] longToByteArray_Little(long value) {
		return new byte[] { (byte) value, (byte) (value >>> 8),
				(byte) (value >>> 16), (byte) (value >>> 24),
				(byte) (value >>> 32), (byte) (value >>> 40),
				(byte) (value >>> 48), (byte) (value >>> 56) };
	}

	public static final byte[] intToByteArray_Little(int value) {
		return new byte[] { (byte) value, (byte) (value >>> 8),
				(byte) (value >>> 16), (byte) (value >>> 24) };
	}

	public static final byte[] intToByteArray_Little(int value, byte[] outBuff) {
		outBuff[0] = (byte) (value & 0xff);
		outBuff[1] = (byte) (value >>> 8);
		outBuff[2] = (byte) (value >>> 16);
		outBuff[3] = (byte) (value >>> 24);
		return outBuff;
		// return new byte[] { (byte) value, (byte) (value >>> 8), (byte) (value
		// >>> 16), (byte) (value >>> 24) };
	}

	public static final byte[] intToByteArray_Big(int value) {
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
				(byte) (value >>> 8), (byte) value };
	}

	public static final byte[] shortToByteArray_Little(short value) {
		return new byte[] { (byte) value, (byte) (value >>> 8) };
	}

	public static final byte[] shortToByteArray_Big(short value) {
		return new byte[] { (byte) (value >>> 8), (byte) value };
	}

	public static final byte[] doubleToByte_Little(double value) {
		// return new byte[] { (byte) (value >>> 8), (byte) value };
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.asDoubleBuffer().put(value);
		return buffer.array();
	}
	public static final byte[] doubleToByte_Big(double value){
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.asDoubleBuffer().put(value);
		return buffer.array();
	}
	
	public static final byte[] floatToByte_Little(float value){
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.asFloatBuffer().put(value);
		return buffer.array();
	}
	public static final byte[] floatToByte_Big(float value){
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.asFloatBuffer().put(value);
		return buffer.array();
	}

	public static final byte[] charToByteArray(char value) {
		byte[] b = new byte[2];
		b[0] = (byte) ((value & 0xFF00) >> 8);
		b[1] = (byte) (value & 0xFF);
		return b;
	}
	
	public static double bytesToDouble_Little(byte[] b,int nBeginPos) {
		long l;
		l = b[0];
		l &= 0xff;
		l |= ((long) b[1] << 8);
		l &= 0xffff;
		l |= ((long) b[2] << 16);
		l &= 0xffffff;
		l |= ((long) b[3] << 24);
		l &= 0xffffffffl;
		l |= ((long) b[4] << 32);
		l &= 0xffffffffffl;
		l |= ((long) b[5] << 40);
		l &= 0xffffffffffffl;
		l |= ((long) b[6] << 48);
		l &= 0xffffffffffffffl;
		l |= ((long) b[7] << 56);
		return Double.longBitsToDouble(l);
//        return Double.longBitsToDouble(byteArrayToLong_Little(b, nBeginPos));  
    }  
	
	public static float bytesToFloat_Little(byte[] b,int nBeginPos){
		return Float.intBitsToFloat(byteArrayToInt_Little(b,nBeginPos));  
	}
	public static float bytesToFloat_Big(byte[] b,int nBeginPos){
		return Float.intBitsToFloat(byteArrayToInt_Big(b,nBeginPos));  
	}
}
