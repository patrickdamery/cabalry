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
import com.cabalry.base.CabalryActivity;
import com.cabalry.base.HistoryItem;
import com.cabalry.util.TasksUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.cabalry.net.CabalryServer.REQ_USER_NAME;
import static com.cabalry.util.PreferencesUtil.GetHistory;
import static com.cabalry.util.PreferencesUtil.SaveHistory;

public class AlarmHistoryActivity extends CabalryActivity.Compat {
    private static final String TAG = "AlarmHistoryActivity";

    public static ArrayList<HistoryItem> historyItems;

    public static void addHistoryEntry(final Context context, final int userID, final int alarmID) {
        final SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        if (historyItems == null) {
            historyItems = GetHistory(context);

            if (historyItems == null) {
                historyItems = new ArrayList<>();
            }
        }

        new TasksUtil.GetUserInfoTask(context, userID) {
            @Override
            protected void onPostExecute(Bundle result) {
                if (result != null) {
                    historyItems.add(0, new HistoryItem(result.getString(REQ_USER_NAME), userID, alarmID, f.format(new Date())));
                    SaveHistory(context, historyItems);

                } else {
                    Log.e(TAG, "Error no user info found!");
                }
            }
        }.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_history);

        final TextView noHistoryText = (TextView) findViewById(R.id.noHistoryText);
        final Button clearAll = (Button) findViewById(R.id.clearAll);
        final ListView listview = (ListView) findViewById(R.id.listview);

        if (historyItems == null)
            historyItems = GetHistory(getApplicationContext());

        if (historyItems != null && !historyItems.isEmpty()) {

            final HistoryItem[] values = historyItems.toArray(new HistoryItem[historyItems.size()]);
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
                                    historyItems.remove(item);
                                    adapter.remove(item);
                                    adapter.notifyDataSetChanged();
                                    view.setAlpha(1);
                                }
                            });
                }

            });

        } else {
            listview.setVisibility(View.GONE);
            clearAll.setVisibility(View.GONE);
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
