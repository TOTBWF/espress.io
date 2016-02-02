package io.reed.dripr.Presenters;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import io.reed.dripr.Models.DatabaseHelper;
import io.reed.dripr.Models.YieldTdsTarget;
import io.reed.dripr.Presenters.Interfaces.IVisualizerPresenter;
import io.reed.dripr.Views.Interfaces.IVisualizerView;

/**
 * Created by reed on 2/1/16.
 */
public class VisualizerPresenter implements IVisualizerPresenter {
    private IVisualizerView view;
    private Context context;
    private DatabaseHelper dbHelper;
    private ArrayList<YieldTdsTarget> targets;
    private YieldTdsTarget selectedTarget;

    @Override
    public void onTakeView(IVisualizerView view, Context context) {
        this.view = view;
        this.context = context;
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.targets = dbHelper.getStoredTargets();
    }

    @Override
    public void updateTargets() {
        ArrayList<String> targetNames = new ArrayList<>();
        for(YieldTdsTarget target: targets) {
            targetNames.add(target.getName());
        }
        view.updateTargetSpinner(targetNames);
    }

    @Override
    public void setSelectedTarget(int index) {
        selectedTarget = targets.get(index);
        view.drawFormulaLine(selectedTarget);
        view.updateGraphbounds(selectedTarget);
        view.clearPoints();
        Cursor cursor = dbHelper.getEntries(selectedTarget.getName());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            double input = cursor.getDouble(1);
            double output = cursor.getDouble(2);
            double tds = cursor.getDouble(3);
            double yield = tds * output / input;
            view.plotPoint(yield, tds);
            cursor.moveToNext();
        }
        cursor.close();
    }
}
