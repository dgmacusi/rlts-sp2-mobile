package com.app.rlts.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.app.rlts.R;
import com.app.rlts.entity.StateVO;

import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<StateVO>{

    private Context mContext;
    private ArrayList<StateVO> listState;
    private SpinnerAdapter myAdapter;
    private boolean isFromView = false;

    public SpinnerAdapter(Context context, int resource, List<StateVO> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.listState = (ArrayList<StateVO>) objects;
        this.myAdapter = this;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.spinner_item, null);

            holder = new ViewHolder();
            holder.mTextView = (TextView) convertView
                    .findViewById(R.id.spinner_text);
            holder.mCheckBox = (CheckBox) convertView
                    .findViewById(R.id.spinner_checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextView.setText(listState.get(position).getLocation());

        // To check whether checked event fire from getview() or user input
        isFromView = true;
        holder.mCheckBox.setChecked(listState.get(position).isSelected());
        isFromView = false;

        if ((position == 0)) {
            holder.mCheckBox.setVisibility(View.INVISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        }

        holder.mCheckBox.setTag(position);
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listState.get(0).setSelected(false);
                if(holder.mCheckBox.isChecked()){
                    listState.get(position).setSelected(true);
                }else{
                    listState.get(position).setSelected(false);
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }

    public ArrayList<String> getListState(){

        ArrayList<String> locationId = new ArrayList<>();

        for(int i = 0; i < this.listState.size(); i++){
            if(this.listState.get(i).isSelected()){
                locationId.add(String.valueOf(this.listState.get(i).getId()));
            }
        }

        return locationId;
    }
}
