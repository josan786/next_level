package ru.konstantin_starikov.samsung.izhhelper.models.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.Account;
import ru.konstantin_starikov.samsung.izhhelper.models.CropSquareTransformation;
import ru.konstantin_starikov.samsung.izhhelper.models.Helper;

public class LeaderboardListAdapter extends ArrayAdapter<Account> {

    private List<Account> accountList;

    private Context context;

    private int resource;

    public LeaderboardListAdapter(Context context, int resource, List<Account> accountList) {
        super(context, resource, accountList);
        this.context = context;
        this.resource = resource;
        this.accountList = accountList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(resource, null, false);

        View background = view.findViewById(R.id.background);
        View container = view.findViewById(R.id.container);
        ImageView userAvatar = view.findViewById(R.id.avatar);
        TextView nameText = view.findViewById(R.id.name);
        TextView scoreText = view.findViewById(R.id.score);

        Account account = accountList.get(position);

        Resources res = context.getResources();
        nameText.setText(account.firstName + " " + account.lastName);
        scoreText.setText(Integer.toString(account.getScore()));
        if(account.getAvatarURL() != null) Picasso.get().load(account.getAvatarURL()).transform(new CropSquareTransformation()).into(userAvatar);
        else{
            userAvatar.setImageDrawable(ResourcesCompat.getDrawable(res, R.drawable.avatar, null));
        }
        switch (position)
        {
            case 0: {
                background.setBackground(ResourcesCompat.getDrawable(res, R.drawable.layout_gold_round_background, null));
                ViewGroup.LayoutParams params = container.getLayoutParams();
                params.height = Helper.convertDpInPixels(110, context);
                container.setLayoutParams(params);
                break;
            }
            case 1: {
                background.setBackground(ResourcesCompat.getDrawable(res, R.drawable.layout_silver_round_background, null));
                break;
            }
            case 2: {
                background.setBackground(ResourcesCompat.getDrawable(res, R.drawable.layout_copper_round_background, null));
                break;
            }
        }
        return view;
    }

    private void removeHero(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure you want to delete this?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                accountList.remove(position);

                notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
