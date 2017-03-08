package htl.at.awesomeproject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import htl.at.awesomeproject.adapter.ProductAdapter;
import htl.at.awesomeproject.entity.Product;

public class MainActivity extends AppCompatActivity {
    private String url ="http://"+BuildConfig.SERVER_HOST+":8080/api/product/getproducts";
    final Context context = this;
    //region fields
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Product> moneyrain = new ArrayList<>();
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    //endregion

    //dNlP6-pS95Q:APA91bEngPuBK3qoiKcsnzHKTv6zRCVLaSbU4vIlYtJHUQPxysSeCGWPD-cTaOAhTkKvyimnf49ZoUCCpVqzcazMrusu8tyKZB2f_W8pQhZz431Va5A4eFJXKReNxMyvZdMfrJ0sm3cU
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //FirebaseCrash.report(new Exception("this is a test theres no error actually trust me I am an engineer^^"));
        //region recyclerview
        mAdapter = new ProductAdapter(new LinkedList<Product>());
        mRecyclerView = (RecyclerView) findViewById(R.id.bestview);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //endregion
        //region UserLoggedIn?
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LogInActivity.class));
            finish();
            return;
        } else {
            Log.d("logged in"," finally");

        }
        //endregion
        //region GoogleLogOutClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d("Connection ", connectionResult.toString());
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        //endregion
        FirebaseMessaging.getInstance().subscribeToTopic("foo");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final RequestQueue queue = Volley.newRequestQueue(this);
        //region GET
        url= "http://"+BuildConfig.SERVER_HOST+":8080/api/product/getproducts";
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("GET RESPONSE: ",response);
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            JSONArray data = responseObject.getJSONArray("Value");
                            for(int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                Product product = new Product();
                                product.setProductname(obj.getString("Name"));
                                product.setQuantity(obj.getString("Quantity"));
                                product.setUsername(obj.getString("Username"));
                                Log.d("MONEYRAIN : ",product.getProductname());
                                moneyrain.add(product);

                            }
                            ((ProductAdapter) mAdapter).setProducts(moneyrain);
                            mRecyclerView.smoothScrollToPosition(0);
                            /*mRecyclerView.setAdapter(mAdapter);*/
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            FirebaseCrash.report(e);
                            //e.printStackTrace();
                        }
                        //response.split()

                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
                FirebaseCrash.report(error);
            }
        });
        queue.add(stringRequest);
        //endregion
        //region FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(context)
                        .title("Add Product")
                        .input("Text","",new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, final CharSequence input) {
                                url = "http://"+BuildConfig.SERVER_HOST+":8080/api/product/postproduct";
                                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                        new Response.Listener<String>()
                                        {
                                            @Override
                                            public void onResponse(String response) {
                                                // response
                                                Log.d("Response", response);
                                                moneyrain.add(new Product(input.toString(),"1",
                                                        mFirebaseUser.getDisplayName()));
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        },
                                        new Response.ErrorListener()
                                        {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // error
                                                FirebaseCrash.report(error);
                                                //Log.d("Error.Response", "ERROR");
                                            }
                                        }
                                ) {
                                    @Override
                                    protected Map<String, String> getParams()
                                    {
                                        Map<String, String>  params = new HashMap<String, String>();
                                        params.put("name", input.toString());
                                        params.put("username", mFirebaseUser.getDisplayName());

                                        return params;
                                    }
                                };
                                queue.add(postRequest);
                            }
                        }).show();
            }
        });
        //endregion
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                startActivity(new Intent(this, LogInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
