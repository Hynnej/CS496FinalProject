package signin.project.com.finalproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//Class Views account info and allows for changes to be made
public class ViewAccountInfomation extends AppCompatActivity {

    protected static final String TAG = "ViewAccountInfo";
    GoogleSignInAccount acct;
    EditText fname, lname, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account_infomation);

        //gets account info and prefills edit fields
        acct = getIntent().getExtras().getParcelable("user");

        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        email = (EditText) findViewById(R.id.email);

        getUserData();

    }

    //gets user data from database to prefill, since may be differenct than googles
    public void getUserData(){
        final ProgressDialog mDialog = ProgressDialog.show(this, "Please wait...", "Retrieving data", true);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://finalprojectapi.herokuapp.com/index.php/users/" + acct.getId();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mDialog.dismiss();
                        try {
                            fname.setText(response.getString("fname"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            lname.setText(response.getString("lname"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            email.setText(response.getString("email"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mDialog.dismiss();
                        Toast.makeText(ViewAccountInfomation.this, "That didn't work!", Toast.LENGTH_LONG).show();

                    }
                });

        queue.add(jsObjRequest);

    }

    //Changes the Users data
    public void ChangeUser(View v) {
        //extracts data from Google Account
        String gId = acct.getId();
        String finame = fname.getText().toString();;
        String laname = lname.getText().toString();
        String mail = email.getText().toString();

        String errMessage = "Change User: laname is: " + laname;
        Log.i(TAG, errMessage);

        final ProgressDialog mDialog = ProgressDialog.show(this, "Please wait...", "Retrieving data", true);
        Map<String, String> jsonParams = new HashMap<String, String>();

        //creates JSON to send to API
        jsonParams.put("gId", gId);
        jsonParams.put("fname", finame);
        jsonParams.put("lname", laname);
        jsonParams.put("email", mail);

        //Sets URL to send to Server
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://finalprojectapi.herokuapp.com/index.php/users/" + gId;

        //Sends and recieves request
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.PUT, url,

                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mDialog.dismiss();
                        Toast.makeText(ViewAccountInfomation.this, "User Info Changed.", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mDialog.dismiss();
                        Toast.makeText(ViewAccountInfomation.this, "That didn't work!", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }
        };
        queue.add(postRequest);
    }
}
