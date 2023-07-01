package com.moegoto.wallpaper.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ListAdapter extends BaseAdapter implements OnItemClickListener, OnItemLongClickListener {

    private List<ListAdapterItem> adapterItems = new ArrayList<ListAdapterItem>();
    private final Context context;

    public ListAdapter(Context context) {
        this.context = context;
    }

    public void add(ListAdapterItem item) {
        adapterItems.add(item);
    }

    @Override
    public int getCount() {
        return adapterItems.size();
    }

    @Override
    public Object getItem(int position) {
        return adapterItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return adapterItems.get(position).getView(context, convertView, parent);
    }

    public void remove(ListAdapterItem item) {
        item.detach();
        adapterItems.remove(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        adapterItems.get(position).onItemClick(parent, view);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return adapterItems.get(position).onItemLongClick(parent, view);
    }

    public void clear() {
        for (ListAdapterItem item : adapterItems) {
            item.detach();
        }
        adapterItems.clear();
    }

}
