package com.example.bledemo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.bledemo.Characteristics;
import com.example.bledemo.R;
import com.example.bledemo.ble.CharModel;

import java.util.List;

public class CharListAdapter extends ArrayAdapter<CharModel> {

    private final Context context;
    private Characteristics charActivity;
    private List<CharModel> charModelList;

    public CharListAdapter(@NonNull Context context, List<CharModel> charModelList, Characteristics charActivity){
        super(context, R.layout.characteristic_list_item, charModelList);
        this.context = context;
        this.charModelList = charModelList;
        this.charActivity = charActivity;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = charActivity.getLayoutInflater();

        View rowView = inflater.inflate(R.layout.characteristic_list_item, null, true);
        final ListView listView = (ListView) charActivity.findViewById(R.id.characteristic_list);

        TextView UUID_View = (TextView) charActivity.findViewById(R.id.UUID);
        TextView Properties_View = (TextView) charActivity.findViewById(R.id.Properties);

        String UUID = charModelList.get(position).getUUID();
        boolean[] Properties = charModelList.get(position).getProperties();

        UUID_View.setText(UUID);
        String prop_Msg = "";
        if (Properties[0]){
            prop_Msg += "R";
        }
        if (Properties[1]){
            prop_Msg += "W";
        }
        if (Properties[2]){
            prop_Msg += "N";
        }
        Properties_View.setText(prop_Msg);

        return rowView;
    }
}
