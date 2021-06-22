package ru.konstantin_starikov.samsung.izhhelper.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.Achievement;


//Todo: создание адаптера с достижениями
public class AccountAchievementsAdapter extends RecyclerView.Adapter<AccountAchievementsAdapter.ViewHolder>{

    private LayoutInflater inflater;
    private List<Achievement> achievements;

    public AccountAchievementsAdapter(Context context, List<Achievement> achievements) {
        this.achievements = achievements;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public AccountAchievementsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.achievement_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AccountAchievementsAdapter.ViewHolder holder, int position) {
        Achievement achievement = achievements.get(position);
/*        holder.title.setText(form.title);
        holder.questionsCount.setText(Integer.toString(form.questions.size()));*/
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title = null;
        final TextView questionsCount = null;
        ViewHolder(View view){
            super(view);
/*            title = view.findViewById(R.id.availableFormTitle);
            questionsCount = view.findViewById(R.id.availableFormQuestionsCount);*/
        }
    }
}
