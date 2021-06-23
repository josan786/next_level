package ru.konstantin_starikov.samsung.izhhelper.models;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

import java.io.Serializable;
import java.util.ArrayList;

import ru.konstantin_starikov.samsung.izhhelper.R;

public class Achievement implements Serializable {
    public String ID; // по ID из строковых значений берётся name
    private String name;
    public int colorIconID;
    public int wbIconID; // white and black icon
    public boolean userHas;
    public int scoreValue;
    public String description;

    public Achievement(String ID, boolean userHas, Context context) {
        this.ID = ID;
        this.userHas = userHas;
        this.colorIconID = getColorIconIDFromID(ID, context);
        this.wbIconID = getWBIconIDFromID(ID, context);
        this.name = getNameFromID(ID, context);
        this.scoreValue = getScoreFromID(ID);
        this.description = getDescriptionFromID(ID, context);
    }

    public static ArrayList<Achievement> getAllAvailableAchievements (Context context)
    {
        ArrayList<Achievement> result = new ArrayList<Achievement>();
        result.add(new Achievement("FirstReport", false, context));
        result.add(new Achievement("InLeaderboard", false, context));
        return result;
    }

    private static String getNameFromID(String ID, Context context)
    {
        String result = "";
        switch (ID)
        {
            case "FirstReport":
            {
                result = context.getString(R.string.FirstReportName);
                break;
            }
            case "InLeaderboard":
            {
                result = context.getString(R.string.InLeaderboardName);
                break;
            }
        }
        return result;
    }

    private static String getDescriptionFromID(String ID, Context context)
    {
        String result = "";
        switch (ID)
        {
            case "FirstReport":
            {
                result = context.getString(R.string.FirstReportDescription);
                break;
            }
            case "InLeaderboard":
            {
                result = context.getString(R.string.InLeaderboardDescription);
                break;
            }
        }
        return result;
    }

    private static int getScoreFromID(String ID)
    {
        int result = -1;
        switch (ID)
        {
            case "FirstReport":
            {
                result = 120;
                break;
            }
            case "InLeaderboard":
            {
                result = 250;
                break;
            }
        }
        return result;
    }

    private static int getColorIconIDFromID(String ID, Context context)
    {
        int result = -1;
        switch (ID)
        {
            case "FirstReport":
            {
                result = R.drawable.ach_first_report;
                break;
            }
            case "InLeaderboard":
            {
                result = R.drawable.ach_entered_in_leaderboard;
                break;
            }
        }
        return result;
    }

    private static int getWBIconIDFromID(String ID, Context context)
    {
        int result = -1;
        switch (ID)
        {
            case "FirstReport":
            {
                result = R.drawable.ach_first_report_bw;
                break;
            }
            case "InLeaderboard":
            {
                result = R.drawable.ach_entered_in_leaderboard_bw;
                break;
            }
        }
        return result;
    }
}
