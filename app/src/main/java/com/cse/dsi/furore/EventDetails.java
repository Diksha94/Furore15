package com.cse.dsi.furore;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventDetails extends Fragment {

    View layout;
    DBEventDetails get;

    public EventDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_event_details, container, false);
        get = new DBEventDetails(getActivity());
        return layout;
    }

    public void setData(String id) {
        get.open();
        String[] data = get.getSingleEvent(id);
        get.close();
//        ((TextView) layout.findViewById(R.id.event_name)).setText(data[0]);
        ((TextView) layout.findViewById(R.id.event_coordinator)).setText(data[1]);
        ((TextView) layout.findViewById(R.id.event_rules)).setText(data[2]+" ");
        ((TextView) layout.findViewById(R.id.fee)).setText("₹"+data[4]);
        String[] cash = data[5].split("-");
        ((TextView) layout.findViewById(R.id.cash1)).setText("₹"+cash[0]);
        if(cash[1].equals("0")){
            (layout.findViewById(R.id.cash2)).setVisibility(View.INVISIBLE);
            (layout.findViewById(R.id.hidethis)).setVisibility(View.INVISIBLE);
        }
        else {
            ((TextView) layout.findViewById(R.id.cash2)).setText("₹"+cash[1]);
            (layout.findViewById(R.id.cash2)).setVisibility(View.VISIBLE);
            (layout.findViewById(R.id.hidethis)).setVisibility(View.VISIBLE);
        }
    }

}
