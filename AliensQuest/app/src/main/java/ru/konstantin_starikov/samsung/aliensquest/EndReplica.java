package ru.konstantin_starikov.samsung.aliensquest;

public class EndReplica extends Replica{

    public Situation nextSituation;

    public EndReplica(Situation nextSituation) {
        this.nextSituation = nextSituation;
    }

    @Override
    String getText() {
        return "dvb";
    }
}
