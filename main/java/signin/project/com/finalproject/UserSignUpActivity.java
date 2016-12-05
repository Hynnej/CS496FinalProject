package signin.project.com.finalproject;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//File Contains Volley Requests for Login
public class UserSignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //Checks to see if User already exsists in DB, if not adds
    public void searchForUser(GoogleSignInAccount acc){
        final GoogleSignInAccount acct = acc;
        final ProgressDialog mDialog = ProgressDialog.show(this, "Please wait...", "Retrieving data", true);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://finalprojectapi.herokuapp.com/index.php/users/" + acct.getId();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mDialog.dismiss();
                        if(response.has("response"))
                        {
                            addUser(acct);
                            searchForUser(acct);
                        }

                        else
                        {
                            Toast.makeText(UserSignUpActivity.this, "User Logged In.", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mDialog.dismiss();
                        Toast.makeText(UserSignUpActivity.this, "That didn't work!", Toast.LENGTH_LONG).show();

                    }
                });

        queue.add(jsObjRequest);

    }

    //adds new user
    private void addUser(GoogleSignInAccount acct){
        String gId = acct.getId();
        String fname = acct.getGivenName();
        String lname = acct.getFamilyName();
        String email = acct.getEmail();


        final ProgressDialog mDialog = ProgressDialog.show(this, "Please wait...", "Retrieving data", true);
        Map<String, String> jsonParams = new HashMap<String, String>();

        jsonParams.put("gId", gId);
        jsonParams.put("fname", fname);
        jsonParams.put("lname", lname);
        jsonParams.put("email", email);


        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://finalprojectapi.herokuapp.com/index.php/users";

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,

                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mDialog.dismiss();
                        Toast.makeText(UserSignUpActivity.this, "User Added.", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mDialog.dismiss();
                        Toast.makeText(UserSignUpActivity.this, "That didn't work!", Toast.LENGTH_LONG).show();
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