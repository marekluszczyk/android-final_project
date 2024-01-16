package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private ListView lvItems;
    private ArrayList<ListItem> items;
    protected Toast toastMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

//        Intent intent = getIntent();
//        if (intent != null && intent.hasExtra("listItems")) {
//            items = (ArrayList<ListItem>) intent.getSerializableExtra("listItems");
//        }
        items = loadList();
        lvItems = findViewById(R.id.itemsListView);

        fillListView(R.layout.custom_list_item, R.id.mainItem, R.id.subItem);
    }

    protected void showToast(String text) {

        if (toastMessage!= null) {
            toastMessage.cancel();
        }
        toastMessage = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toastMessage.show();
    }

    protected void fillListView(int layout, int row1, int row2) {
        lvItems.setAdapter(new ArrayAdapter<ListItem>(
                this,
                layout,
                items
        ) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item, parent, false);
                }

                ListItem listItem = getItem(position);

                TextView mainItem = convertView.findViewById(row1);
                TextView subItem = convertView.findViewById(row2);

                if (listItem != null) {
                    mainItem.setText(listItem.getName());
                    subItem.setText(String.valueOf(listItem.getSum()));
                }

                convertView.setOnClickListener(v -> showToast(listItem.getName() + "\n" + listItem.getSum()));
                return convertView;
            }
        });
    }

    public ArrayList<ListItem> loadList() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String json = prefs.getString("listItems", null);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ListItem>>() {}.getType();
        ArrayList<ListItem> list = gson.fromJson(json, type);

        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    private void showNotification(int stopCount) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel("CHANNEL_LIST", "List activity stop counter", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_LIST")
                .setContentTitle("List activity")
                .setContentText("total stops so far: " + stopCount)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setAutoCancel(true);

        notificationManager.notify(2, builder.build());
    }

    private void incrementAndShowNotification() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int stopCount = prefs.getInt("listStopCount", 0);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("listStopCount", ++stopCount);
        editor.apply();

        showNotification(stopCount);
    }

    @Override
    protected void onStop() {
        super.onStop();
        incrementAndShowNotification();
    }
}