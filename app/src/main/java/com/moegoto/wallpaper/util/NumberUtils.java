package com.moegoto.wallpaper.util;

/**
 * 数値用ユーティリティクラス
 * 
 * @author Satoshi Ida
 */
public class NumberUtils {

    /**
     * 文字列をint型の数値に変換します。<br/>
     * 変換できない文字列が指定された場合はdefaultValueが返されます
     * 
     * @param string 変換元文字列
     * @param defaultValue 変換できない文字列が指定された場合に返される数字
     * @return int型に変換された数字
     */
    public static int parseInt(String string, int defaultValue) {
        try {
            if (string == null)
                return defaultValue;
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

}
