package com.cabalry.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cabalry.R;
import com.cabalry.base.BindableActivity;
import com.cabalry.base.HistoryItem;
import com.cabalry.util.TasksUtil;

import java.util.ArrayList;

public class AlarmHistoryActivity extends BindableActivity {
    private static final String TAG = "AlarmHistoryActivity";

    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_history);

        final TextView noHistoryText = (TextView) findViewById(R.id.noHistoryText);
        final ListView listview = (ListView) findViewById(R.id.listview);

        progressBar = new ProgressDialog(this) {
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                AlarmHistoryActivity.this.onBackPressed();
            }
        };
        progressBar.setCancelable(false);
        progressBar.setMessage(getResources().getString(R.string.msg_loading));
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        // Get history
        new TasksUtil.GetAlarmHistory(getApplicationContext()) {
            @Override
            protected void onPostExecute(ArrayList<HistoryItem> result) {
                progressBar.dismiss();
                if (!result.isEmpty()) {
                    final HistoryItem values[] = result.toArray(new HistoryItem[result.size()]);
                    final HistoryArrayAdapter adapter = new HistoryArrayAdapter(getApplicationContext(), values);

                    listview.setAdapter(adapter);

                    // don't show divider
                    listview.setDivider(null);
                    listview.setDividerHeight(0);

                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                            final HistoryItem item = (HistoryItem) parent.getItemAtPosition(position);

                            if (item.getState().equals("active")) {
                                Log.i(TAG, "Joining alarm id: " + item.getAlarmId());

                                Intent intent = new Intent();
                                intent.putExtra("alarmId", item.getAlarmId());
                                intent.putExtra("userId", item.getUserId());
                                intent.setAction("com.cabalry.action.ALARM_JOIN");
                                sendBroadcast(intent);

                            } else {
                                // Notify alarm is inactive
                                Log.i(TAG, "Can't join, alarm is inactive");
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_alarm_inactive),
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                    });

                } else {
                    listview.setVisibility(View.GONE);
                    noHistoryText.setVisibility(View.VISIBLE);
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        // Return to home
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    public class HistoryArrayAdapter extends ArrayAdapter<HistoryItem> {
        private final Context context;
        private final HistoryItem[] values;

        public HistoryArrayAdapter(Context context, HistoryItem[] values) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.history_list_item, parent, false);
            TextView label = (TextView) rowView.findViewById(R.id.label);
            TextView description = (TextView) rowView.findViewById(R.id.description);
            ImageView icon = (ImageView) rowView.findViewById(R.id.icon);

            HistoryItem item = values[position];

            label.setText(item.getUsername() + " - Alarm ID: " + item.getAlarmId());
            description.setText(item.getTimestamp());

            if (item.getState().equals("active")) {
                icon.setImageResource(R.drawable.ic_alarm_active);
            } else {
                icon.setImageResource(R.drawable.ic_alarm_inactive);
            }

            return rowView;
        }
    }
}
