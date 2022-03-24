package com.stella.ehealthtest.ecg;
import com.stella.ehealthtest.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class ecgAdapter extends ArrayAdapter<ecg> {
    private Context context;
    private List<ecg> ecgData;

    public ecgAdapter(@NonNull Context context, int resource, List<ecg> ecgData) {
        super(context, resource,ecgData);
        this.context=context;
        this.ecgData=ecgData;
    }
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //getting the layoutinflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //getting listview items
        View listViewItem=inflater.inflate(R.layout.ecgdata,null,true);
        TextView textViewECGData =(TextView) listViewItem.findViewById(R.id.textViewECG);
        ImageView imageViewStatus=(ImageView) listViewItem.findViewById(R.id.imageViewStatus);

        //getting the current ecg data
        ecg ecg=ecgData.get(position);
        //setting the name to textview
        textViewECGData.setText(ecg.getCreatedDateTime());

        //if the synced status is 0 displaying
        //queued icon
        //else displaying synced icon
        if (ecg.getStatus() == 0)
            imageViewStatus.setBackgroundResource(R.drawable.stopwatch);
        else
            imageViewStatus.setBackgroundResource(R.drawable.success);

        return listViewItem;
    }
}
