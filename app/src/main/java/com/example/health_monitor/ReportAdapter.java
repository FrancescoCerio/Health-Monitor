package com.example.health_monitor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.health_monitor.DB.Report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportHolder> {

    private List<Report> reports = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public ReportHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_item, parent, false);
        return new ReportHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportHolder holder, int position) {
        Report currentReport = reports.get(position);
        //holder.textViewTitle.setText(String.valueOf(currentReport.getId()));
        if(!currentReport.getNote().isEmpty())
            holder.textViewDescription.setText(currentReport.getNote());
        else
            holder.textViewDescription.setText("Nessuna nota");
        Date date = currentReport.getDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd' 'MMM' 'yyyy", Locale.ITALY);
        holder.textViewTitle.setText(String.valueOf(dateFormat.format(date.getTime())));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public void setReports(List<Report> reports){
        this.reports = reports;
        notifyDataSetChanged();
    }

    class ReportHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDescription;
        //private TextView textViewPriority;

        public ReportHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            //textViewPriority = itemView.findViewById(R.id.text_view_priority);
            textViewDescription = itemView.findViewById(R.id.text_view_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(reports.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Report report);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
