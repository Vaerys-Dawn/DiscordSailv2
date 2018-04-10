package com.github.vaerys.utilobjects;


import ch.qos.logback.core.rolling.TriggeringPolicyBase;

import java.io.File;

public class TriggerOncePolicy<E> extends TriggeringPolicyBase<E> {
    private static boolean doRolling = true;

    @Override
    public boolean isTriggeringEvent(File activeFile, E event) {
        // roll the first time when the event gets called
        // if log is greater than 10mb exit
        if (activeFile.length() / 1000 > 10240) return true;
        //roll first
        if (doRolling) {
            doRolling = false;
            return true;
        }
        return false;
    }
}
