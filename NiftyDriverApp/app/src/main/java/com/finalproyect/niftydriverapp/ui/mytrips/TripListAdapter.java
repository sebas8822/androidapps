package com.finalproyect.niftydriverapp.ui.mytrips;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.RecyclerViewInterface;
import com.finalproyect.niftydriverapp.db.Trip;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.MyViewHolder> {

    private  final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private List<Trip> tripList;
    LayoutInflater layoutInflater;

    // determine the context

    public TripListAdapter(RecyclerViewInterface recyclerViewInterface, Context context){
        this.recyclerViewInterface = recyclerViewInterface;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }



    // I am saying put the list into this object

    public void setTripList(List<Trip> tripList) {
        this.tripList = tripList;

    }

    @NonNull
    @Override
    public TripListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recycler_maps_view,parent, false);

        return new MyViewHolder(view, recyclerViewInterface);
    }

    // hold the data for a every single row
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //Obtaind specific employee to also cast to other methods
        Trip tripfl = tripList.get(position);
        int pot = position ;
        // get the info in determine position in the specific item in the employee array

        holder.tv_dateTripRow.setText(getDateFromMillis(this.tripList.get(position).getStartDate()));
        holder.tv_startTripRow.setText(getTimeFromMillis(this.tripList.get(position).getStartTime()));
        holder.tv_endTripRow.setText(getTimeFromMillis(this.tripList.get(position).getEndTime()));
        holder.tv_startTripLocationRow.setText(this.tripList.get(position).getStartLocationName());
        holder.tv_endTripLocationRow.setText(this.tripList.get(position).getEndLocationName());//create the methods to convert in readable location
        holder.tv_scoreTripMyTrips.setText(String.valueOf((int)this.tripList.get(position).getScoreTrip()));
    }
    /*****************************Test Data******************************/


    private String getDateFromMillis(long dateMillis) {
        Date startDate = new Date(dateMillis);
        DateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm");


        return df.format(startDate);
    }
    private String getTimeFromMillis(long timeMillis) {
        Date millis = new Date(timeMillis);

        DateFormat df = new SimpleDateFormat("HH:mm");

        return df.format(millis);
    }


    /*****************************Test Data******************************/
    @Override
    public int getItemCount() {
        return this.tripList.size();
    }// total number of views

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_dateTripRow,tv_startTripRow,tv_endTripRow,tv_startTripLocationRow,tv_endTripLocationRow,tv_scoreTripMyTrips;


        public MyViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            tv_dateTripRow = itemView.findViewById(R.id.tv_dateTripRow);
            tv_startTripRow = itemView.findViewById(R.id.tv_startTripRow);
            tv_endTripRow = itemView.findViewById(R.id.tv_endTripRow);
            tv_startTripLocationRow = itemView.findViewById(R.id.tv_startTripLocationRow);
            tv_endTripLocationRow = itemView.findViewById(R.id.tv_endTripLocationRow);
            tv_scoreTripMyTrips = itemView.findViewById(R.id.tv_scoreTripMyTrips);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface!=null){// make sure this is not null
                        int pos = getAdapterPosition();//if is not null then
                        if (pos!=RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);
                        }



                    }
                }
            });



        }
    }



}
