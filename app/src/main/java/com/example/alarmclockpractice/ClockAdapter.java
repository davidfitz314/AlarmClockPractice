package com.example.alarmclockpractice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.util.LinkedList;

public class ClockAdapter extends RecyclerView.Adapter<ClockAdapter.ClockHolder> {
    private LinkedList<ClockTimes> mClocks;
    private LayoutInflater mInflater;
    private Context context;
    private static ClickListener clickListener;

    public ClockAdapter(Context context_in, LinkedList<ClockTimes> clocks_in) {
        mClocks = clocks_in;
        context = context_in;
        mInflater = LayoutInflater.from(context);
    }

    public ClockTimes getClockAtPosition(int position){ return mClocks.get(position); }

    @NonNull
    @Override
    public ClockAdapter.ClockHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mItemView = mInflater.inflate(R.layout.recyclerview_single_item, viewGroup, false);
        return new ClockHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ClockAdapter.ClockHolder clockHolder, int i) {
        ClockTimes mCurrent = mClocks.get(i);
        //plug in data for new view here
        clockHolder.hourView.setText(mCurrent.getmHour());
        clockHolder.minuteView.setText(mCurrent.getmMinute());
        if (mCurrent.getmAm()) {
            clockHolder.amPmView.setText(R.string.am_input);
        } else {
            clockHolder.amPmView.setText(R.string.pm_input);
        }

        if (mCurrent.getmOn()){
            clockHolder.onOffToggle.setChecked(true);
        } else {
            clockHolder.onOffToggle.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        if (mClocks != null)
            return mClocks.size();
        return 0;
    }


    public class ClockHolder extends RecyclerView.ViewHolder{
        public final TextView hourView;
        public final TextView minuteView;
        public final TextView amPmView;
        public final Switch onOffToggle;
        final ClockAdapter clockAdapter;

        public ClockHolder(final View itemView, final ClockAdapter clockAdapter_in) {
            super(itemView);
            this.clockAdapter = clockAdapter_in;
            hourView = itemView.findViewById(R.id.hour_textview);
            minuteView = itemView.findViewById(R.id.minute_textview);
            amPmView = itemView.findViewById(R.id.ampm_textview);
            onOffToggle = itemView.findViewById(R.id.alarm_toggle);
            onOffToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ClockTimes temp = mClocks.get(getAdapterPosition());
                    temp.setmOn(isChecked);
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(itemView, getAdapterPosition());
                }
            });

            /*
            itemView.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    Toast.makeText(context.getApplicationContext(), getAdapterPosition()+" Moved Position", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });*/
        }

    }

    public void setOnItemClickListener(ClickListener clickListener){
        ClockAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View v, int position);
    }
}
