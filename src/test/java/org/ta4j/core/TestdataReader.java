package org.ta4j.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import org.ta4j.core.num.NumFactory;

public class TestdataReader {

    private static NumFactory numFactory;

    public TestdataReader(NumFactory numFactory) {
        this.numFactory = numFactory;
    }

    public static BarSeries readCsv(String filename, Duration duration) throws IOException {
        BarSeries series = new BaseBarSeriesBuilder().withName("test").withNumFactory(numFactory).build();
        File file = new File("src/test/resources/" + filename);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            int i = 0;
            while ((line = br.readLine()) != null) {
                if (i == 0) {
                    i++;
                    continue;
                }
                String[] values = line.split(",");
                Instant instant = Instant.ofEpochMilli(Long.parseLong(values[0]));
                double open = Double.parseDouble(values[2]);
                double high = Double.parseDouble(values[3]);
                double low = Double.parseDouble(values[4]);
                double close = Double.parseDouble(values[5]);
                double volume = Double.parseDouble(values[6]);

                series.addBar(new BaseBar(duration, instant, numFactory.numOf(open), numFactory.numOf(high),
                        numFactory.numOf(low), numFactory.numOf(close), numFactory.numOf(volume), numFactory.zero(),
                        0));
            }

        }

        return series;
    }
}