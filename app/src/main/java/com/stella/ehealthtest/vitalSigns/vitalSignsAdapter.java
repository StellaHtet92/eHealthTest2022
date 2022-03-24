package com.stella.ehealthtest.vitalSigns;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stella.ehealthtest.R;
import com.stella.ehealthtest.ecg.ecg;

import java.util.List;

public class vitalSignsAdapter extends ArrayAdapter<vitalSigns> {
    private Context context;
    private List<vitalSigns> vitalSigns;

    public vitalSignsAdapter(@NonNull Context context, int resource, List<vitalSigns> vitalSigns) {
        super(context, resource,vitalSigns);
        this.context=context;
        this.vitalSigns=vitalSigns;
    }
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //getting the layoutinflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //getting listview items
        View listViewItem=inflater.inflate(R.layout.vitaldata,null,true);
        TextView textViewVitalID =(TextView) listViewItem.findViewById(R.id.textViewVitalID);
        TextView textViewCreatedAt =(TextView) listViewItem.findViewById(R.id.textViewVitalCreatedAt);
        TextView textViewVitalEWS =(TextView) listViewItem.findViewById(R.id.textViewEWS);
       // TextView textViewVitalTemp =(TextView) listViewItem.findViewById(R.id.textViewTemp);
       // TextView textViewVitalBP =(TextView) listViewItem.findViewById(R.id.textViewBP);
        TextView textViewVitalBloodSugar =(TextView) listViewItem.findViewById(R.id.textViewBloodSugar);
        ImageView imageViewVitalStatus=(ImageView) listViewItem.findViewById(R.id.imageViewVitalStatus);

        //getting the current ecg data
        vitalSigns vital=vitalSigns.get(position);
        //setting the name to textview
        textViewCreatedAt.setText(vital.getCreatedDateTime());
        textViewVitalID.setText(""+vital.getUser_id());
        textViewVitalEWS.setText(""+vital.getEWS());
       // textViewVitalTemp.setText(""+vital.getTemperature());
        //textViewVitalBP.setText(""+vital.getupperBP()+"/"+vital.getLowerBPBP());
        textViewVitalBloodSugar.setText(""+vital.getBlood_sugar_level());
        //if the synced status is 0 displaying
        //queued icon
        //else displaying synced icon
        if (vital.getStatus() == 0)
            imageViewVitalStatus.setBackgroundResource(R.drawable.stopwatch);
        else
            imageViewVitalStatus.setBackgroundResource(R.drawable.success);

        return listViewItem;
    }
}
