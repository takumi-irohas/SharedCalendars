package higa.sharedcalendars.dao;


import android.content.Context;
import android.support.annotation.NonNull;

import com.github.gfx.android.orma.AccessThreadConstraint;

import higa.sharedcalendars.BuildConfig;
import higa.sharedcalendars.model.OrmaDatabase;


public class DatabaseHelper {
    public static OrmaDatabase db(@NonNull Context context) {
        return OrmaDatabase.builder(context)
                .readOnMainThread(AccessThreadConstraint.NONE)
                .writeOnMainThread(BuildConfig.DEBUG ? AccessThreadConstraint.WARNING : AccessThreadConstraint.NONE)
                .build();
    }
}
