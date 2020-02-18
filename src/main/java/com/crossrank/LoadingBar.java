/*
 * Copyright 2020 Conor Gregg Escalante
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

        progress = -1;

        updateProgress();

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
