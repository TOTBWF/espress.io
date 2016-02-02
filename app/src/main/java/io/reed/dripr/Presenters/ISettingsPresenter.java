package io.reed.dripr.Presenters;

import android.content.Context;

import io.reed.dripr.Views.ISettingsView;

/**
 * Created by reed on 1/29/16.
 */
public interface ISettingsPresenter {
    void onTakeView(ISettingsView view, Context context);
    void dumpDbToCsv();
    void clearDb();
    void settingNotYetImplemented();
}
