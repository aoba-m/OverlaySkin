package com.moegoto.wallpaper.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

/**
 * ListAdapterで使用される、リスト項目の基底クラスです。<br/>
 * このクラスを継承してください。
 * 
 * @author Satoshi Ida
 */
public abstract class ListAdapterItem {

    /**
     * Viewを生成して返します。
     * 
     * @param context
     * @param convertView
     * @param parent
     * @return
     */
    public abstract View getView(Context context, View convertView, ViewGroup parent);

    /**
     * Viewがクリックされた際に呼び出されます。
     * 
     * @param parent
     * @param view
     */
    public void onItemClick(AdapterView<?> parent, View view) {

    }

    /**
     * Viewがクリックされた際に呼び出されます。
     * 
     * @param parent
     * @param view
     */
    public boolean onItemLongClick(AdapterView<?> parent, View view) {
        return false;
    }

    /**
     * 項目がクリアされる場合に呼び出されます。 <br/>
     * 項目のメモリ解放や、後処理を行います。<br/>
     */
    public void detach() {

    }

}
