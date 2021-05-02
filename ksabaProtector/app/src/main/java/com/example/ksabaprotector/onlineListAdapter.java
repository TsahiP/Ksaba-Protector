package com.example.ksabaprotector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class onlineListAdapter extends ArrayAdapter<workers> {
//    private  static  final String TAG = "PersonListAdapter";
    private Context mContext;
    int mResource;
    ArrayList<workers> cArray;

    public onlineListAdapter(Context context, int resource, ArrayList<workers> cArray){
        super(context,resource,cArray);
        mContext = context;
        mResource = resource;
        this.cArray=cArray;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View row = inflater.inflate(R.layout.adapter_lv,parent, false);
        final workers c = cArray.get(position);
        if(c != null) {
            String name = getItem(position).getName();
            String car = getItem(position).getCar();
            String email = getItem(position).getEmail();
            String inwork = getItem(position).getInwork();
//        create object with this info
            workers mWorker = new workers(name, car, email,inwork);



            TextView tv_name = (TextView) row.findViewById((R.id.name_tv_xml));
            TextView tv_inwork = (TextView) row.findViewById((R.id.inWork_tv_xml));
            TextView tv_car = (TextView) row.findViewById((R.id.car_tv_xml));

            tv_name.setText(c.getEmail());
            tv_inwork.setText("במשמרת");
            tv_car.setText(c.getCar());
        }
        return  row;

    }

}
