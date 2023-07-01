package com.moegoto.wallpaper.util;

import java.util.Locale;

/**
 * 多言語用のユーティリティクラスです。
 * 
 * @author Satoshi Ida
 */
public class LocaleUtil {

    /**
     * 引数指定された日本語用、英語用文字列より、現在のLocaleに一致する文字列を返します。<br/>
     * Localeが日本、英語以外の場合は英語を返します。
     * 
     * @param ja 日本語用文字列
     * @param en 英語用文字列
     * @return Localeに一致した文字列
     */
    public static String locale(String ja, String en) {

        // 本来はResouceBundleを使用すべきであるが、めんどくさいで…
        if (Locale.JAPAN.equals(Locale.getDefault())) {
            return ja;
        }
        return en;
    }
}
