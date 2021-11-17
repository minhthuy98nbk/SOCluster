package com.zingplay.cron.comparator;

import com.zingplay.cron.beans.job.ACronJob;

import java.util.Comparator;

public class JobComparator implements Comparator<ACronJob> {
    private static final JobComparator instance = new JobComparator();

    public static JobComparator getInstance() {
        return instance;
    }

    @Override
    public int compare(ACronJob o1, ACronJob o2) {
        return o1.getTimeStart() >= o2.getTimeStart() ? 1 : -1;
    }
}
