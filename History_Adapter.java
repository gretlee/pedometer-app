package com.demo.example.adapter;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.example.R;

import com.demo.example.DB.MySQLiteHelper;
import com.demo.example.DB.SingleRow;
public class History_Adapter extends RecyclerView.Adapter<History_Adapter.ViewHolder> {
    Context mContext;
    private SingleRow[] notes;
    Typeface type;

    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCalore;
        TextView txtDate;
        TextView txtDistenc;
        TextView txtSteps;
        TextView txtcalorie;
        TextView txtdis;
        TextView txtstep;

        public ViewHolder(View view) {
            super(view);
            this.txtDate = (TextView) view.findViewById(R.id.Date);
            this.txtCalore = (TextView) view.findViewById(R.id.Calore);
            this.txtDistenc = (TextView) view.findViewById(R.id.distence);
            this.txtSteps = (TextView) view.findViewById(R.id.stepsCount);
            this.txtstep = (TextView) view.findViewById(R.id.txtstep);
            this.txtdis = (TextView) view.findViewById(R.id.txtdis);
            this.txtcalorie = (TextView) view.findViewById(R.id.txtcalorie);
        }
    }

    public History_Adapter(Context context, int i, SingleRow[] singleRowArr) {
        this.notes = singleRowArr;
        this.mContext = context;
        this.type = Typeface.createFromAsset(context.getAssets(), "Titillium-Semibold.otf");
    }

    @Override 
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_history, viewGroup, false));
    }
    @Override 
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        SingleRow singleRow = this.notes[i];
        TextView textView = viewHolder.txtDate;
        textView.setText(singleRow.getDaysOfMonth() + "/" + (singleRow.getMonth() + 1) + "/" + singleRow.getYear() + "");
        TextView textView2 = viewHolder.txtSteps;
        StringBuilder sb = new StringBuilder();
        sb.append(singleRow.getStepsForWeekView());
        sb.append(this.mContext.getResources().getString(R.string.steps));
        textView2.setText(sb.toString());
        TextView textView3 = viewHolder.txtDistenc;
        textView3.setText(singleRow.getDistence() + this.mContext.getResources().getString(R.string.miles));
        TextView textView4 = viewHolder.txtCalore;
        textView4.setText(singleRow.getCaloriesBurn() + this.mContext.getResources().getString(R.string.calories_burned));
        viewHolder.txtDate.setTypeface(this.type);
        viewHolder.txtCalore.setTypeface(this.type);
        viewHolder.txtDistenc.setTypeface(this.type);
        viewHolder.txtSteps.setTypeface(this.type);
        viewHolder.txtstep.setTypeface(this.type);
        viewHolder.txtdis.setTypeface(this.type);
        viewHolder.txtcalorie.setTypeface(this.type);
        viewHolder.txtDate.setVisibility(TextUtils.isEmpty("date") ? View.GONE : View.VISIBLE);
        viewHolder.txtSteps.setVisibility(TextUtils.isEmpty(MySQLiteHelper.COLUMN_STEPS) ? View.GONE : View.VISIBLE);
        viewHolder.txtDistenc.setVisibility(TextUtils.isEmpty("cal") ? View.GONE : View.VISIBLE);
        viewHolder.txtDistenc.setVisibility(TextUtils.isEmpty("Miles") ? View.GONE : View.VISIBLE);
        if (viewHolder.txtDate.getVisibility() != View.VISIBLE) {
            return;
        }
        viewHolder.itemView.getContext().getResources().getDimensionPixelSize(R.dimen.note_content_spacing);
    }
    @Override 
    public int getItemCount() {
        return this.notes.length;
    }
}
