package ru.konstantin_starikov.samsung.izhhelper.models.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.Account;
import ru.konstantin_starikov.samsung.izhhelper.models.Achievement;


//Todo: создание адаптера с достижениями
public class AccountAchievementsAdapter extends RecyclerView.Adapter<AccountAchievementsAdapter.ViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    private List<Achievement> achievements;
    private ArrayList<Achievement> availableAchievements;

    public AccountAchievementsAdapter(Context context, Account account) {
        this.context = context;
        this.achievements = account.getAchievements();
        this.inflater = LayoutInflater.from(context);
        availableAchievements = Achievement.getAllAvailableAchievements(context);
    }

    @Override
    public AccountAchievementsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.achievement_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AccountAchievementsAdapter.ViewHolder holder, int position) {
        Achievement achievement = availableAchievements.get(position);
        Resources res = context.getResources();
        if (hasAchievementInUser(achievement)) {
            holder.achievementIcon.setImageDrawable((ResourcesCompat.getDrawable(res, achievement.colorIconID, null)));
        } else {
            holder.achievementIcon.setImageDrawable((ResourcesCompat.getDrawable(res, achievement.wbIconID, null)));
        }
    }

    private boolean hasAchievementInUser(Achievement achievement)
    {
        for(Achievement userAchievement : achievements)
        {
            if(achievement.ID.equals(userAchievement.ID)) return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return availableAchievements.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView achievementIcon;
        ViewHolder(View view){
            super(view);
            achievementIcon = view.findViewById(R.id.achievementIcon);
        }
    }
}
