package io.jenkins.plugins.blueking.utils;

import java.io.PrintStream;

public class Logger {

    private PrintStream ps;

    public Logger(PrintStream ps) {
        this.ps = ps;
    }

    public void log(String msg) {
        ps.println("[Blueking] " + msg);
    }
}
