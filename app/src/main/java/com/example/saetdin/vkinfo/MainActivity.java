package com.example.saetdin.vkinfo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import static com.example.saetdin.vkinfo.utils.NetworkUtils.generateURL;
import static com.example.saetdin.vkinfo.utils.NetworkUtils.getResponseFromURL;

public class MainActivity extends AppCompatActivity {

    private EditText searchField;
    private Button searchButton;
    private TextView result;
    private TextView errorMissages;
    private ProgressBar progressBar;

    class VKQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = getResponseFromURL(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        protected void onPostExecute(String response) {
            String firstName = null;
            String lastName = null;
            JSONObject jsonResponse = null;
            if (response != null && !"".equals(response)) {
                try {
                    jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("response");
                    String resultingString = "";
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject userInfo = jsonArray.getJSONObject(i);
                        firstName = userInfo.getString("first_name");
                        lastName = userInfo.getString("last_name");
                        resultingString += "name " + firstName + "last name" + lastName;
                    }
                    result.setText(resultingString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showResultTextView();
            } else {
                showErrorMessages();
            }
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchField = findViewById(R.id.et_search_field);
        searchButton = findViewById(R.id.b_search_vk);
        result = findViewById(R.id.tv_result);
        errorMissages = findViewById(R.id.tv_error_messages);
        progressBar = findViewById(R.id.pb_loading_indicator);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URL generatedUrl = generateURL(searchField.getText().toString());
                new VKQueryTask().execute(generatedUrl);
            }
        };

        searchButton.setOnClickListener(onClickListener);
    }

    private void showResultTextView() {
        result.setVisibility(View.VISIBLE);
        errorMissages.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessages() {
        result.setVisibility(View.INVISIBLE);
        errorMissages.setVisibility(View.VISIBLE);
    }
}
