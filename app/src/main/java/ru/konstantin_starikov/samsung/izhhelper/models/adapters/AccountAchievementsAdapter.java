package ru.konstantin_starikov.samsung.izhhelper.models.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    private Dialog achievementDialog;

    public AccountAchievementsAdapter(Context context, Account account) {
        this.context = context;
        this.achievements = account.getAchievements();
        this.inflater = LayoutInflater.from(context);
        availableAchievements = Achievement.getAllAvailableAchievements(context);
        achievementDialog = new Dialog(context);
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
            holder.achievementIcon.setImageDrawable(ResourcesCompat.getDrawable(res, achievement.colorIconID, null));
        } else {
            holder.achievementIcon.setImageDrawable((ResourcesCompat.getDrawable(res, achievement.wbIconID, null)));
        }
        holder.achievementIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAchievementDialog(achievement);
            }
        });
    }

    private void showAchievementDialog(Achievement achievement)
    {
        Resources res = context.getResources();
        achievementDialog.setContentView(R.layout.achievement_popup);
        TextView title = achievementDialog.findViewById(R.id.achievementTitle);
        TextView score = achievementDialog.findViewById(R.id.achievementScore);
        ImageView icon = achievementDialog.findViewById(R.id.achievementPopupIcon);
        TextView description = achievementDialog.findViewById(R.id.achievementDescriprion);
        Button closeButton = achievementDialog.findViewById(R.id.closePopupButton);
        title.setText(achievement.getName());
        icon.setImageDrawable(ResourcesCompat.getDrawable(res, achievement.colorIconID, null));
        description.setText(achievement.description);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                achievementDialog.dismiss();
            }
        });
        achievementDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        achievementDialog.show();
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
