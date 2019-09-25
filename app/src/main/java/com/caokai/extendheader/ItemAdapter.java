package com.caokai.extendheader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author caokai
 * @time 2019/9/25
 */
public class ItemAdapter extends BaseAdapter {

    private Context mContext;
    public ItemAdapter(Context mContext){
        this.mContext=mContext;
    }

    @Override
    public int getCount() {
        return 20;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.item_listview,parent,false);
            holder=new ViewHolder();
            holder.textView=convertView.findViewById(R.id.textview);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        holder.textView.setText("第"+position+"行");
        return convertView;
    }

    class ViewHolder{
        TextView textView;
    }
}
