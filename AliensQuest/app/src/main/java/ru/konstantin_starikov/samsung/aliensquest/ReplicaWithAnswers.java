package ru.konstantin_starikov.samsung.aliensquest;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

public class ReplicaWithAnswers extends Replica {
    public Answer[] answers;

    public ReplicaWithAnswers(String text, Answer... answers)
    {
        this.answers = answers.clone();
        this.text = text;
    }


    public void playNextReplica(TextView text, Button nextReplicaButton, LinearLayout buttonsLayout, Context context) throws IOException {
        nextReplicaButton.setEnabled(false);
        for (int i = 0; i < answers.length; i++) {
            Button button = new Button(context);
            button.setId(i);
            button.setText(answers[i].text);
            int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        buttonsLayout.removeAllViews();
                        answers[finalI].playNextReplica(text, nextReplicaButton, buttonsLayout, context);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            buttonsLayout.addView(button);
        }
    }

    @Override
    String getText() {
        String result = text;
        return result;
    }
}

