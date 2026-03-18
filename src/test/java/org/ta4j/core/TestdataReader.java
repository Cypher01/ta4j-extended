package org.ta4j.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import org.ta4j.core.mocks.MockBarSeriesBuilder;
import org.ta4j.core.num.NumFactory;

public class TestdataReader {

    private static NumFactory numFactory;

    public TestdataReader(NumFactory numFactory) {
        TestdataReader.numFactory = numFactory;
    }

    public BarSeries readCsv(String filename) throws IOException {
        BarSeries series = new MockBarSeriesBuilder().withNumFactory(numFactory).build();
        File file = new File("src/test/resources/" + filename);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            int i = 0;
            while ((line = br.readLine()) != null) {
                // skip header
                if (i == 0) {
                    i++;
                    continue;
                }
                String[] values = line.split(",");
                Instant beginTime = Instant.parse(values[1]);
                Instant endTime = Instant.parse(values[2]);
                double open = Double.parseDouble(values[4]);
                double high = Double.parseDouble(values[5]);
                double low = Double.parseDouble(values[6]);
                double close = Double.parseDouble(values[7]);
                double volume = Double.parseDouble(values[8]);

                series.barBuilder().beginTime(beginTime).endTime(endTime).openPrice(open).closePrice(close).highPrice(
                        high).lowPrice(low).volume(volume).add();
            }

        }

        return series;
    }
}