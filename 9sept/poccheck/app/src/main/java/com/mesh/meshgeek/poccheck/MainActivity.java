package com.mesh.meshgeek.poccheck;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISession;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.errors.LIDeepLinkError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.utils.Scope;



import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.mesh.meshgeek.poccheck.R;

import com.twitter.sdk.android.Twitter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.linkedin.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Arrays;

import io.fabric.sdk.android.Fabric;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;





public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "rpDY80pch7aYW6a3K0YyV4xHC";
    private static final String TWITTER_SECRET = "I7GJvO0rLnxx3q0svo0Uoe4aRyTW7rGaag6Wvj8BuIZG4lyIkD";
    TwitterLoginButton loginButton;
//    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;


    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String PACKAGE_MOBILE_SDK_SAMPLE_APP = "com.mesh.meshgeek.poccheck";


       /*
        * A flag indicating that a PendingIntent is in progress and prevents us
        * from starting further intents.
        */

//    private boolean mIntentInProgress;
    TextView textView;

    com.google.android.gms.common.SignInButton signInBtn;

    SharedPreferences shared;
    private static final int RC_SIGN_IN = 0;
    private boolean mSignInClicked;

    private boolean mIntentInProgress;

    private boolean mShouldResolve;


    private Button signOutButton;
    private TextView tvName, tvMail, tvNotSignedIn;
    private LinearLayout viewContainer;



    Button helloButton;
    private LoginButton loginbutton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity=this;
        final Activity thisActivity = this;
        setUpdateState();

        //  LISessionManager.getInstance(getApplicationContext()).init(AccessToken access);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
//        btnSignIn.setOnClickListener(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);





        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);
        helloButton=(Button)findViewById(R.id.hello);

        helloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LISessionManager.getInstance(getApplicationContext()).init(thisActivity,buildScope(),new AuthListener(){
                    @Override
                    public void onAuthSuccess() {
                        setUpdateState();
                        String url = "https://api.linkedin.com/v1/people/~:(id, first-name, skills)";

                        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                        apiHelper.getRequest(getApplicationContext(), url, new ApiListener() {
                            @Override
                            public void onApiSuccess(ApiResponse apiResponse) {
                                System.out.println("connections" + apiResponse);
                                // Success!
                            }

                            @Override
                            public void onApiError(LIApiError liApiError) {
                                System.out.println("sadness");
                                // Error making GET request!
                            }
                        });
                        Toast.makeText(getApplicationContext(), "success" + LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken(), Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onAuthError(LIAuthError error) {
                        setUpdateState();

                        Toast.makeText(getApplicationContext(), "failed " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.mesh.meshgeek.poccheck",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
        loginbutton = (LoginButton)findViewById(R.id.login_button);
        signInBtn = (com.google.android.gms.common.SignInButton) findViewById(R.id.sign_in_button);
        signOutButton=(Button)findViewById(R.id.signout);
      signOutButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               if (view.getId() == R.id.signout) {
                                                   if (mGoogleApiClient.isConnected()) {
                                                       Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                                                       mGoogleApiClient.disconnect();
                                                       mGoogleApiClient.connect();
                                                   }
                                               }
                                           }
                                       });



          signInBtn.setOnClickListener(new View.OnClickListener() {

              @Override
              public void onClick(View arg0) {
                  mGoogleApiClient.connect();

              }
          });

      //  loginbutton.setReadPermissions("user_friends");
        loginbutton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email", "user_birthday"));

        loginbutton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(MainActivity.this, loginResult.getAccessToken().getPermissions() + "hua dost", Toast.LENGTH_LONG).show();
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                try {
                                    // String id=object.getString("id");

                                    String email = object.getString("email");
                                    //String gender=object.getString("gender");
                                    //String birthday=object.getString("birthday");
                                    System.out.print("digo" + email);
                                    //do something with the data here
                                } catch (JSONException e) {
                                    System.out.print("ds");
                                    e.printStackTrace(); //something's seriously wrong here
                                }
                                Log.e("LoginActivity please", response.toString());
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,gender,birthday,email,first_name,last_name,location,locale,timezone");
                request.setParameters(parameters);
                request.executeAsync();

//                request.executeAsync(ParseF)
                new GraphRequest(
                        loginResult.getAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                /* handle the result */

                                Log.e("Friend in List", "-------------" + response.toString());
                            }
                        }
                ).executeAsync();

//                btnSignIn.setOnClickListener();
                //  System.out.println("yo nigga"+parameters.getString("fields"));
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
//        LISessionManager.getInstance(getApplicationContext())

        shared = getSharedPreferences("demotwitter", Context.MODE_PRIVATE);
//        helloButton = (Button) findViewById(R.id.hello);
//
//        helloButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                linkedInLogin();
//            }
//        });
        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                Toast.makeText(getApplicationContext(), "logged in",
                        Toast.LENGTH_LONG).show();
                TwitterSession session = Twitter.getSessionManager()
                        .getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;

//Here we get all the details of user's twitter account

                System.out.println(result.data.getUserName()
                        + result.data.getUserId());
                Twitter.getApiClient(session).getAccountService()
                        .verifyCredentials(true, false, new Callback<User>() {

                            @Override
                            public void success(Result<User> userResult) {


                                User user = userResult.data;
//Here we get image url which can be used to set as image wherever required.      
                                System.out.println(user.friendsCount + "hey yo " + user.createdAt + "check" + user.followersCount + user.statusesCount);

                            }

                            @Override
                            public void failure(TwitterException e) {

                            }

                        });
                shared.edit().putString("tweetToken", token).commit();
                shared.edit().putString("tweetSecret", secret).commit();
                TwitterAuthClient authClient = new TwitterAuthClient();
                authClient.requestEmail(session, new Callback<String>() {
                    @Override
                    public void success(Result<String> result) {
                        // Do something with the result, which provides the
                        // email address
                        System.out.println(result.toString());
                        Log.d("Result", result.toString());
                        Toast.makeText(getApplicationContext(), result.data,
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        // Do something on failure
                        System.out.println(exception.getMessage());
                    }
                });
                MyTwitterApiClient apiclients = new MyTwitterApiClient(session);
                apiclients.getCustomService().list(result.data.getUserId(), new Callback<Response>() {

                    @Override
                    public void failure(TwitterException arg0) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void success(Result<Response> arg0) {
                        // TODO Auto-generated method stub
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        try {

                            reader = new BufferedReader(new InputStreamReader(arg0.response.getBody().in()));

                            String line;

                            try {
                                while ((line = reader.readLine()) != null) {
                                    sb.append(line);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        String result = sb.toString();
                        System.out.println("Response is>>>>>>>>>" + result);
                        try {
                            JSONObject obj = new JSONObject(result);
                            JSONArray ids = obj.getJSONArray("ids");
                            //This is where we get ids of followers
                            for (int i = 0; i < ids.length(); i++) {
                                System.out.println("Id of user " + (i + 1) + " is " + ids.get(i));
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                });
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
LISessionManager.getInstance(getApplicationContext()).onActivityResult(this,requestCode,resultCode,data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private void setUpdateState() {
        LISessionManager sessionManager = LISessionManager.getInstance(getApplicationContext());
        LISession session = sessionManager.getSession();
        boolean accessTokenValid = session.isValid();


    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE);
    }

    protected void onStart() {
        super.onStart();

    }

    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        signInBtn.setVisibility(View.GONE);

        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            int  name=person.getCircledByCount();

            Toast.makeText(this, String.valueOf(name), Toast.LENGTH_LONG).show();
            //textView.setText("Welcome : " + person.getDisplayName());
            try {
                JSONObject jsonObject = new JSONObject(person.getImage()
                        .toString());
                String imageUrl = jsonObject.getString("url");



            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        if (!mIntentInProgress && arg0.hasResolution()) {
            try {
                mIntentInProgress = true;
                arg0.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent. Return to the
                // default
                // state and attempt to connect to get an updated
                // ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
        // Log.e("error", "error code" + arg0.getResolution());
        Toast.makeText(this, "User is onConnectionFailed!", Toast.LENGTH_LONG)
                .show();
    }

    }
