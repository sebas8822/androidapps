package com.finalproyect.niftydriverapp.ui.mytrips;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.db.Trip;

import java.util.List;

public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.MyViewHolder> {


    private Context context;
    private List<Trip> tripList;

    // determine the context

    public TripListAdapter(Context context){
        this.context = context;
    }


    // I am saying put the list into this object

    public void setTripList(List<Trip> tripList) {
        this.tripList = tripList;

    }

    @NonNull
    @Override
    public TripListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_maps_view,parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //Obtaind specific employee to also cast to other methods
        Trip tripfl = tripList.get(position);
        int pot = position ;
        // get the info in determine position in the specific item in the employee array

        holder.tv_dateTripRow.setText(this.tripList.get(position).getStartDate());
        holder.tv_startTripRow.setText(this.tripList.get(position).getStartTime());
        holder.tv_endTripRow.setText(this.tripList.get(position).getEndTime());
        holder.tv_startTripLocationRow.setText(getStartAddressLocation(this.tripList.get(position)));
        holder.tv_endTripLocationRow.setText(getEndAddressLocation(this.tripList.get(position)));//create the methods to convert in readable location
        holder.tv_scoreTripMyTrips.setText(String.valueOf((int)this.tripList.get(position).getScoreTrip()));
    }

    private String getEndAddressLocation(Trip trip) {
        trip.getStartLocation();
        return "24 Seaview avenue, Port macquarie 2444";
    }

    private String getStartAddressLocation(Trip trip) {
        trip.getStartLocation();
        return "2 Charles Sturt University, Port Macquarie 2";
    }

    @Override
    public int getItemCount() {
        return this.tripList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_dateTripRow,tv_startTripRow,tv_endTripRow,tv_startTripLocationRow,tv_endTripLocationRow,tv_scoreTripMyTrips;


        public MyViewHolder(View view) {
            super(view);
            tv_dateTripRow = view.findViewById(R.id.tv_dateTripRow);
            tv_startTripRow = view.findViewById(R.id.tv_startTripRow);
            tv_endTripRow = view.findViewById(R.id.tv_endTripRow);
            tv_startTripLocationRow = view.findViewById(R.id.tv_startTripLocationRow);
            tv_endTripLocationRow = view.findViewById(R.id.tv_endTripLocationRow);
            tv_scoreTripMyTrips = view.findViewById(R.id.tv_scoreTripMyTrips);



        }
    }



}
