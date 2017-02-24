package com.liuruichao.app.utils;

import java.io.UnsupportedEncodingException;

/**
 * DataUtils
 *
 * @author liuruichao
 * @date 15/11/3 上午10:19
 */
public final class DataUtils {
    private static final String charsetName = "utf-8";

    /**
     * 包装数据，添加数据长度
     * @param msg
     * @return
     * @throws UnsupportedEncodingException
     */
    public static byte[] addDataSize(String msg) throws UnsupportedEncodingException {
        byte[] buffer = msg.getBytes(charsetName);
        byte[] result = new byte[buffer.length + 4];
        byte[] sizeBytes = TypeConvertUtils.int2byte(buffer.length);
        result[0] = sizeBytes[0];
        result[1] = sizeBytes[1];
        result[2] = sizeBytes[2];
        result[3] = sizeBytes[3];
        for (int i = 4; i < result.length; i++) {
            result[i] = buffer[i - 4];
        }
        return result;
    }
}
