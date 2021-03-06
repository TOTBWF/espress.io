package io.reed.dripr.Presenters.Interfaces;

import android.content.Context;

import io.reed.dripr.Views.Interfaces.ISettingsView;

/**
 * Created by Reed Mullanix on 1/29/16.
 */
public interface ISettingsPresenter {
    void onTakeView(ISettingsView view, Context context);
    void dumpDbToCsv();
    void clearDb();
    void settingNotYetImplemented();
}
