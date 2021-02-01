package ru.konstantin_starikov.samsung.aliensquest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReplicaWithoutAnswers extends Replica {
    public Replica nextReplica;

    public ReplicaWithoutAnswers(String text, Replica nextReplica) {
        this.nextReplica = nextReplica;
        this.text = text;
    }

    @Override
    String getText() throws IOException {
        String result = text;
        return result;
    }
}
