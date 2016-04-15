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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cabalry.R;
import com.cabalry.base.CabalryActivity;
import com.cabalry.util.TasksUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.cabalry.net.CabalryServer.REQ_USER_NAME;
import static com.cabalry.util.PreferencesUtil.GetHistory;
import static com.cabalry.util.PreferencesUtil.SaveHistory;

public class AlarmHistoryActivity extends CabalryActivity.Compat {
    private static final String TAG = "AlarmHistoryActivity";

    public static Set<String> historySet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_history);

        final TextView noHistoryText = (TextView) findViewById(R.id.noHistoryText);
        final Button clearAll = (Button) findViewById(R.id.clearAll);
        final ListView listview = (ListView) findViewById(R.id.listview);

        if (historySet == null)
            historySet = GetHistory(getApplicationContext());

        if (historySet != null) {
            Log.i(TAG, "historySet size: " + AlarmHistoryActivity.historySet.size());

            String[] values = historySet.toArray(new String[historySet.size()]);
            final HistoryArrayAdapter adapter = new HistoryArrayAdapter(this, values);
            listview.setAdapter(adapter);
            listview.setDivider(null);
            listview.setDividerHeight(0);

            clearAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO add clear all feature
                    //historySet.clear();
                    //adapter.clear();
                    //adapter.notifyDataSetChanged();
                    //SaveHistory(getApplicationContext(), historySet);
                }
            });

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    Log.i(TAG, "onItemClick pos: " + position);

                    final String item = (String) parent.getItemAtPosition(position);
                    view.animate().setDuration(2000).alpha(0)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO fix listener
                                    historySet.remove(item);
                                    adapter.remove(item);
                                    adapter.notifyDataSetChanged();
                                    view.setAlpha(1);
                                    SaveHistory(getApplicationContext(), historySet);
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

    public static void addHistoryEntry(final Context context, final int userID, final int alarmID) {
        final SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        if (historySet == null) {
            historySet = new HashSet<>();
        }

        new TasksUtil.GetUserInfoTask(context, userID) {
            @Override
            protected void onPostExecute(Bundle result) {
                if (result != null) {
                    String str = result.getString(REQ_USER_NAME) + "~" + alarmID + "~" + f.format(new Date());
                    historySet.add(str);
                    SaveHistory(context, historySet);

                } else {

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

    public class HistoryArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public HistoryArrayAdapter(Context context, String[] values) {
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

            String[] result = values[position].split("~");

            if (result.length == 3) {
                label.setText(result[0] + " - Alarm ID: " + result[1]);
                description.setText(result[2]);

            } else {
                label.setText(values[position]);
            }

            icon.setImageResource(R.drawable.ic_alarm_inactive);

            return rowView;
        }
    }
}
