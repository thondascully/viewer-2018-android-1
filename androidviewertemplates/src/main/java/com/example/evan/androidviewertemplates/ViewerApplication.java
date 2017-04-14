package com.example.evan.androidviewertemplates;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.evan.androidviewertemplates.firebase_classes.TeamTemplate;
import com.example.evan.androidviewertemplates.utils.SpecificConstants;
import com.example.evan.androidviewertools.ViewerApplicationTemplate;
import com.example.evan.androidviewertools.firebase_classes.Match;
import com.example.evan.androidviewertools.firebase_classes.Team;
import com.example.evan.androidviewertools.firebase_classes.TeamInMatchData;
import com.example.evan.androidviewertools.services.PhotoSync;
import com.example.evan.androidviewertools.services.StarManager;
import com.example.evan.androidviewertools.utils.firebase.FirebaseList;
import com.example.evan.androidviewertools.utils.firebase.FirebaseLists;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;


public class ViewerApplication extends ViewerApplicationTemplate {


    @Override
    public void onCreate() {
        super.onCreate();
        //todo
        //instabug is life, hopefully by the time ur reading this u guys have
        // the ultra gold-plated diamonds-studded platinum version, and don't have to use fake emails like me
        startListListeners(getApplicationContext(), com.example.evan.androidviewertemplates.firebase_classes.Match.class, TeamTemplate.class, com.example.evan.androidviewertemplates.firebase_classes.TeamInMatchData.class);
        //setupFirebaseAuth(this);
        new Instabug.Builder(this, "f56c6f16e2c9965920019f8eb52e7b6e")
                .setInvocationEvent(InstabugInvocationEvent.SHAKE)
                .build();
        restoreFromSharedPreferences();
        startService(new Intent(this, new StarManager() {
            @Override
            public Class<? extends Match> getMatchClass() {
                return com.example.evan.androidviewertemplates.firebase_classes.Match.class;
            }
            @Override
            public Class<?> getMainActivityClass() {
                return MainActivity.class;
            }
        }.getClass()));
        startService(new Intent(this, PhotoSync.class));
        lifeSaver();
    }


    public static void startListListeners(final Context context, Class<? extends Match> matchClass, Class<? extends Team> teamClass, Class<? extends TeamInMatchData> teamInMatchClass) {
        FirebaseLists.matchesList = new FirebaseList<>(SpecificConstants.MATCHES_PATH, new FirebaseList.FirebaseUpdatedCallback() {
            @Override
            public void execute(String key, String previousValue) {
                Log.i("MATCHES", key);
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(SpecificConstants.MATCHES_UPDATED_ACTION).putExtra("key", key).putExtra("previousValue", previousValue));
            }
        }, matchClass);

        FirebaseLists.teamsList = new FirebaseList<>(SpecificConstants.TEAMS_PATH, new FirebaseList.FirebaseUpdatedCallback() {
            @Override
            public void execute(String key, String previousValue) {
                Log.i("TEAMS", key);
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(SpecificConstants.TEAMS_UPDATED_ACTION).putExtra("key", key));
            }
        }, teamClass);

        FirebaseLists.teamInMatchDataList = new FirebaseList<>(SpecificConstants.TEAM_IN_MATCH_DATAS_PATH, new FirebaseList.FirebaseUpdatedCallback() {
            @Override
            public void execute(String key, String previousValue) {
                Log.i("TEAMS_IN_MATCHES", key);
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(SpecificConstants.TEAM_IN_MATCH_DATAS_UPDATED_ACTION).putExtra("key", key));
            }
        }, teamInMatchClass);
    }
    private void lifeSaver(){
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Error"+Thread.currentThread().getStackTrace()[2],paramThrowable.getLocalizedMessage());
            }
        });
    }


    /*public static void setupFirebaseAuth(final Context context) {
        Firebase firebaseRef = new Firebase(SpecificConstants.ROOT_FIREBASE_PATH);

        Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                CharSequence text = "Authenticated!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Log.e("FireBase error", "Failed to auth");
            }
        };

        firebaseRef.authWithCustomToken(SpecificConstants.FIREBASE_KEYS.get(SpecificConstants.ROOT_FIREBASE_PATH), authResultHandler);
    }*/
}
