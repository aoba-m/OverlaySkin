package com.moegoto.wallpaper.util;

/**
 * 文字列用ユーティリティクラス
 * 
 * @author Satoshi Ida
 */
public class StringUtils {

    /**
     * 比較対象の２つの文字列が等しいかどうか比較します。<br/>
     * 比較する文字列にNULLを指定しても例外は発生しません。<br/>
     * 比較する文字列の両方にNULLを指定した場合は、等しいと判断されます。 <br/>
     * 大文字と小文字は区別して比較されます。
     * 
     * @param a
     * @param b
     * @return 等しい場合はtrue、等しくない場合はfalse
     */
    public static boolean equals(String a, String b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }

    /**
     * 文字列がnullまたは空文字でない場合trueを返します。
     * 
     * @param a 検証対象の文字列
     * @return nullまたは空文字でない場合true、nullまたは空文字の場合false
     */
    public static boolean isNotEmpty(String a) {
        if (a == null) {
            return false;
        }
        return a.length() > 0;
    }

    /**
     * 文字列がnullまたは空文字の場合trueを返します。
     * 
     * @param a 検証対象の文字列
     * @return nullまたは空文字の場合true、nullまたは空文字でない場合false
     */
    public static boolean isEmpty(String a) {
        if (a == null) {
            return true;
        }
        return a.length() == 0;
    }

    /**
     * 第1引数に指定された文字列の配列を、第2引数をデリミタとして結合した文字列を返します。<br/>
     * 第1引数に指定された配列のうち、nullまたは空文字は無視します。
     * 
     * @param values
     * @param delimiter
     * @return
     */
    public static String explode(String[] values, String delimiter) {
        StringBuffer sb = new StringBuffer();
        for (String value : values) {
            if (StringUtils.isNotEmpty(value)) {
                if (0 < sb.length()) {
                    sb.append(delimiter);
                }
                sb.append(value);
            }
        }
        return sb.toString();
    }

    public static String defaultString(String trackTitle) {
        if (trackTitle == null)
            return "";
        return trackTitle;
    }
}
