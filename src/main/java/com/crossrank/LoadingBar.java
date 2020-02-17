package com.crossrank;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class LoadingBar {
    private int total;
    private int width;

    private int progress;

    private long startTime;

    public LoadingBar(int width, int total) {
        this.width = width;
        this.total = total;

        progress = 0;

        startTime = System.currentTimeMillis();
    }

    public void updateProgress() {
        progress++;

        long eta = progress == 0 ? 0 :
                (total - progress) * (System.currentTimeMillis() - startTime) / progress;

        String etaHms = progress == 0 ? "N/A" :
                String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                        TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

        int length = (progress * width / total);
        int percent = (progress * 100 / total);

        String string = '\r' +
                String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")) +
                String.format(" %d%% [", percent) +
                String.join("", Collections.nCopies(length, "=")) +
                '>' +
                String.join("", Collections.nCopies((int) (width - length), " ")) +
                ']' +
                String.join("", Collections.nCopies(progress == 0 ? (int) (Math.log10(total)) : (int) (Math.log10(total)) - (int) (Math.log10(progress)), " ")) +
                String.format(" %d/%d, ETA: %s", progress, total, etaHms);

        System.out.print(string);
    }

    public void end() {
        System.out.println();
    }
}
