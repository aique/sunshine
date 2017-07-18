package com.example.android.sunshine.sync;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

public class SunshineSyncUtils {
    private Context context;

    private boolean isInitialized;

    private static final int SYNC_INTERVAL_SECONDS = 12; // segundos entre ejecución
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static final String SUNSHINE_SYNC_TAG = "sunshine-sync"; // etiqueta para identificar el job

    public SunshineSyncUtils(@NonNull final Context context) {
        isInitialized = false;
        this.context = context;
    }

    /**
     * Programa el job que ejecutará la tarea de sincronización.
     *
     * La clase que contiene el código a ejecutarse en el job
     * está definida en el mainfest.xml.
     */
    public void scheduleFirebaseJobDispatcherSync() {
        Log.d("sunshine_sync", "Programando el proceso de sincronización");

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job syncSunshineJob = dispatcher.newJobBuilder()
                .setService(SunshineFirebaseJobService.class)
                .setTag(SUNSHINE_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(syncSunshineJob);
    }

    /**
     * Ejecuta la sincronización al arrancar la aplicación
     * y programa el job para ejecuciones periodicas de esta
     * tarea.
     */
    synchronized public void initialize() {
        if(isInitialized) {
            return;
        }

        Log.d("sunshine_sync", "Inicializando el proceso de sincronización");

        isInitialized = true;

        scheduleFirebaseJobDispatcherSync();
        startImmediateSync();
    }

    /**
     * Inicializa el proceso de sincronización mediante un IntentService.
     */
    public void startImmediateSync() {
        Log.d("sunshine_sync", "Ejecutando el proceso de sincronización");
        Intent intentStartImmediateSync = new Intent(context, SunshineSyncIntentService.class);
        context.startService(intentStartImmediateSync);
    }
}
