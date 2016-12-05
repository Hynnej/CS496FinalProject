package signin.project.com.finalproject;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewPlaces extends AppCompatActivity {

    protected static final String TAG = "ViewPeople";
    GoogleSignInAccount acct;
    // final TextView cat, name, add, rating, comment;
    TableLayout ll;
    String category, pname, address, prate, pcom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_places);

        ll = (TableLayout) findViewById(R.id.lin);

        acct = getIntent().getExtras().getParcelable("user");

        requestPlaces();


    }

    public void requestPlaces() {
        final ProgressDialog mDialog = ProgressDialog.show(this, "Please wait...", "Retrieving data", true);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://finalprojectapi.herokuapp.com/index.php/users/" + acct.getId() + "/places";

        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        mDialog.dismiss();
                        try {
                            fillPlaces(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mDialog.dismiss();
                        Toast.makeText(ViewPlaces.this, "That didn't work!", Toast.LENGTH_LONG).show();

                    }
                });

        queue.add(jsObjRequest);

    }

    public void fillPlaces(JSONArray places) throws JSONException {



        for (int i = 0; i < places.length(); i++) {
            JSONObject place = places.getJSONObject(i);

            if(place.has("category")) {
                category = place.getString("category");
            }else{category = "NULL";}

            if(place.has("name")) {
                pname = place.getString("name");
            }else{pname = "NULL";}

            if(place.has("address")) {
                address = place.getString("address");
            }else{address = "NULL";}

            if(place.has("rating")) {
                prate = place.getString("rating");
            }else{prate = "NULL";}

            if(place.has("comments")) {
                pcom = place.getString("comments");
            }else{pcom = "NULL";}

            TableRow TR = new TableRow(this);
            TR.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            TR.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String Display = category + " - " + pname + " - " + address  + " - " + prate  + " - " + pcom;
                            Toast.makeText(ViewPlaces.this, Display, Toast.LENGTH_LONG).show();

                }
            });

            ll.addView(TR);

            final TextView cat = new TextView(this);
            cat.setText(category);
            TR.addView(cat);

            final TextView name = new TextView(this);
            name.setText(pname);
            TR.addView(name);

            final TextView add = new TextView(this);
            add.setText(address);
            TR.addView(add);

            final TextView rating = new TextView(this);
            rating.setText(prate);
            TR.addView(rating);

            final TextView comment = new TextView(this);
            comment.setText(pcom);
            TR.addView(comment);



        }
    }
}


