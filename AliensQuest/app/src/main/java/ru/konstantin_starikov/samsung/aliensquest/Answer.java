package ru.konstantin_starikov.samsung.aliensquest;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Answer {
    public String text;
    private Replica nextReplica;
    public Action action;

    public Answer(String text, Replica nextReplica) {
        this.text = text;
        this.nextReplica = nextReplica;
    }

    public Answer(String text, Replica nextReplica, Action action) {
        this.text = text;
        this.nextReplica = nextReplica;
        this.action = action;
    }

    public void playNextReplica(TextView text, Button nextReplicaButton, LinearLayout buttonsLayout, Context context) throws IOException {
        text.setText(nextReplica.getText());
        if(nextReplica instanceof ReplicaWithAnswers)
        {
            String userInput = "1";/*new Scanner(System.in).nextLine();*/
            ((ReplicaWithAnswers) nextReplica).playNextReplica(text, nextReplicaButton, buttonsLayout, context);
        }
        else if(nextReplica instanceof ReplicaWithoutAnswers) {
            nextReplicaButton.setEnabled(true);
            nextReplica = ((ReplicaWithoutAnswers) nextReplica).nextReplica;
            nextReplicaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        playNextReplica(text, nextReplicaButton, buttonsLayout, context);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else if(nextReplica instanceof EndReplica)
        {
            System.out.println("nextSituation.play");
            ((EndReplica) nextReplica).nextSituation.play(text, nextReplicaButton, buttonsLayout, context);
        }
        if(action != null) action.run();
    }
}

@FunctionalInterface
interface Action {
    void run() throws IOException;
}
