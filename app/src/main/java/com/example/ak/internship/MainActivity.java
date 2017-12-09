package com.example.ak.internship;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class MainActivity extends Activity {
    Button search;
    EditText string;
    TextView kz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Typeface fs1 = Typeface.createFromAsset(getAssets(), "3Dumb.ttf");
        Typeface fs2 = Typeface.createFromAsset(getAssets(), "cc.ttf");
        Typeface fs3 = Typeface.createFromAsset(getAssets(), "kidz.ttf");
        search = (Button) findViewById(R.id.search);
        string = (EditText) findViewById(R.id.editText2);
        kz = (TextView) findViewById(R.id.kidzone);
        search.setTypeface(fs1);
        string.setTypeface(fs2);
        kz.setTypeface(fs3);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                mVibrator.vibrate(75);
                if (isInternetOn()) {

                    String q = "";
                    q = string.getText().toString();
                    String query = q.replace(" ", "+");
                    if (q.length() == 0) {
                        string.setHint("Not be Empty");
                    } else {
                        if (Pattern.matches("[a-zA-Z]+", q)) {

                            new AsyncTaskParseJson(query, MainActivity.this).execute();


                        } else {
                            string.setText("");
                            string.setHint("Only Alphabets");
                        }
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Need Internet Connection")
                            .setCancelable(false)
                            .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("ALERT");
                    alert.show();
                }
            }
        });
    }

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            // if connected with internet


            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {


            return false;
        }
        return false;
    }

    public class AsyncTaskParseJson extends AsyncTask<String, String, String> {
        final String TAG = "AsyncTaskParseJson.java";
        String que = "";
        // set your json string url here
        String yourJsonStringUrl;
        JSONArray dataJsonArr = null;
        ProgressDialog dialog;
        private Context context;

        public AsyncTaskParseJson(String query, Context cxt) {
            context = cxt;
            dialog = new ProgressDialog(context);
            que = query;
            yourJsonStringUrl = "https://pixabay.com/api/?key=7323778-2933162d67f1926edaf3f822a&q=" + que + "&image_type=photo";

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Please hold..........");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                // instantiate our json parser
                JsonParser jParser = new JsonParser();

                // get json string from url
                JSONObject json = jParser.getJSONFromUrl(yourJsonStringUrl);

                // get the array of users
                dataJsonArr = json.getJSONArray("hits");
                SharedPreferences pref = getApplicationContext().getSharedPreferences("urls", 0); // 0 - for private mode
                SharedPreferences pref1 = getApplicationContext().getSharedPreferences("tags", 0);
                SharedPreferences pref2 = getApplicationContext().getSharedPreferences("len", 0);// 0 - for private mode
                // loop through all users
                SharedPreferences.Editor editor = pref.edit();
                SharedPreferences.Editor editor1 = pref1.edit();
                SharedPreferences.Editor editor2 = pref2.edit();
                int length = 0;

                for (int i = 0; i < dataJsonArr.length(); i++) {
                    length = length + 1;
                    JSONObject c = dataJsonArr.getJSONObject(i);
                    editor.putString("url" + i, "" + c.getString("previewURL"));
                    editor1.putString("tag" + i, "" + c.getString("tags"));// Storing string
                    editor.commit();
                    editor1.commit();
                }
                editor2.putInt("length", length);
                editor2.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg) {
            super.onPostExecute(strFromDoInBg);
            dialog.dismiss();

            context.startActivity(new Intent(context, imagedisplay.class));
        }
    }
}
