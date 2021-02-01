package ru.konstantin_starikov.samsung.aliensquest;

import java.io.IOException;

public abstract class Replica {
    public String text;

    abstract String getText() throws IOException;
}
