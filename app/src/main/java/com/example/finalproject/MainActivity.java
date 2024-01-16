package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    protected Toast toastMessage;
    private ArrayList<ListItem> items;
    private TextView nameText;
    private TextView arg1Text;
    private TextView arg2Text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        items = loadList();
        nameText = findViewById(R.id.nameTextBox);
        arg1Text = findViewById(R.id.arg1TextBox);
        arg2Text = findViewById(R.id.arg2TextBox);
    }

    public void addButton(View view) {

        try {
            String name = nameText.getText().toString();
            int arg1 = Integer.parseInt(arg1Text.getText().toString());
            int arg2 = Integer.parseInt(arg2Text.getText().toString());

            TextView nameLabel = findViewById(R.id.nameTextView);
            TextView sumLabel = findViewById(R.id.sumTextView);
            int sum = arg1 + arg2;

            ListItem listItem = new ListItem(name, sum);
            items.add(listItem);
            saveList(items);
            nameLabel.setText(name);
            sumLabel.setText(String.valueOf(sum));
        } catch (NumberFormatException e) {
            showToast("Please enter valid numbers");
        }
    }

    public void displayButton(View view) {
        Intent intent = new Intent(this, ListActivity.class);
        intent.putExtra("listItems", new ArrayList<>(items));
        startActivity(intent);
    }

    public void clearButton(View view) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove("listItems");
        editor.apply();
        showToast("the list has been emptied");
    }

    protected void showToast(String text) {

        if (toastMessage!= null) {
            toastMessage.cancel();
        }
        toastMessage = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toastMessage.show();
    }

    public void saveList(ArrayList<ListItem> list) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("listItems", json);
        editor.apply();
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

        NotificationChannel channel = new NotificationChannel("CHANNEL_MAIN", "Main activity stop counter", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_MAIN")
                .setContentTitle("Main activity")
                .setContentText("total stops so far: " + stopCount)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }

    private void incrementAndShowNotification() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int stopCount = prefs.getInt("mainStopCount", 0);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("mainStopCount", ++stopCount);
        editor.apply();

        showNotification(stopCount);
    }

    @Override
    protected void onStop() {
        super.onStop();
        incrementAndShowNotification();
    }
}