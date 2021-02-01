package ru.konstantin_starikov.samsung.aliensquest;

import java.io.Console;
import java.io.IOException;
import java.util.Scanner;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Situation {
    public Replica currentReplica;


    public Situation(Replica currentReplica) {
        this.currentReplica = currentReplica;
    }

    public void play(TextView text, Button nextReplicaButton, LinearLayout buttonsLayout, Context context) throws IOException {
        System.out.println(currentReplica.getText());
        text.setText(currentReplica.getText().toString());
        if(currentReplica instanceof  ReplicaWithAnswers)
        {
            ((ReplicaWithAnswers) currentReplica).playNextReplica(text, nextReplicaButton, buttonsLayout, context);
        }
        else if(currentReplica instanceof ReplicaWithoutAnswers) {
            nextReplicaButton.setEnabled(true);
            currentReplica = ((ReplicaWithoutAnswers) currentReplica).nextReplica;
            nextReplicaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        play(text, nextReplicaButton, buttonsLayout, context);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else if(currentReplica instanceof EndReplica)
        {
            System.out.println("nextSituation.play");
            ((EndReplica) currentReplica).nextSituation.play(text, nextReplicaButton, buttonsLayout, context);
        }
    }
}
