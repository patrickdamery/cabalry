package com.cabalry.app;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cabalry.R;
import com.cabalry.base.BindableActivity;
import com.cabalry.base.CabalryActivity;
import com.cabalry.base.HistoryItem;

import static com.cabalry.util.PreferencesUtil.SetAlarmID;
import static com.cabalry.util.PreferencesUtil.SetAlarmUserID;

public class AlarmHistoryActivity extends BindableActivity {
    private static final String TAG = "AlarmHistoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_history);

        final TextView noHistoryText = (TextView) findViewById(R.id.noHistoryText);
        final ListView listview = (ListView) findViewById(R.id.listview);

        // TODO get history from server
        final HistoryItem[] values = new HistoryItem[0];//CabalryAppService.getHistoryValues(getApplicationContext());

        if (values.length != 0) {
            final HistoryArrayAdapter adapter = new HistoryArrayAdapter(this, values);

            listview.setAdapter(adapter);

            // don't show divider
            listview.setDivider(null);
            listview.setDividerHeight(0);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                    Log.i(TAG, "onItemClick pos: " + position);
                    final HistoryItem item = (HistoryItem) parent.getItemAtPosition(position);

                    view.animate().setDuration(2000).alpha(0)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    // Join alarm
                                    Intent intent = new Intent();
                                    intent.putExtra("alarmId", item.getAlarmId());
                                    intent.putExtra("userId", item.getUserId());
                                    intent.setAction("com.cabalry.action.ALARM_JOIN");
                                    sendBroadcast(intent);
                                }
                            });
                }

            });

        } else {
            listview.setVisibility(View.GONE);
            noHistoryText.setVisibility(View.VISIBLE);
        }
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
            //ImageButton remove = (ImageButton) rowView.findViewById(R.id.remove);

            HistoryItem item = values[position];

            label.setText(item.getUsername() + " - Alarm ID: " + item.getAlarmId());
            description.setText(item.getTimestamp());

            icon.setImageResource(R.drawable.ic_alarm_inactive);

            return rowView;
        }
    }
}
