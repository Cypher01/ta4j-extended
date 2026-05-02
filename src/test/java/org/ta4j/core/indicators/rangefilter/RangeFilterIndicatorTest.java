package org.ta4j.core.indicators.rangefilter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.TestdataReader;
import org.ta4j.core.indicators.AbstractIndicatorTest;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.NumFactory;

public class RangeFilterIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
  private BarSeries data;

  public RangeFilterIndicatorTest(NumFactory numFactory) {
    super(numFactory);
  }

  @Before
  public void setUp() throws IOException {
    data = new TestdataReader(numFactory).readCsv("btcusdt-1h.csv");
  }

  @Test
  public void calculationTest() {
    var indicator = new RangeFilterIndicator(new ClosePriceIndicator(data), 100, 3.0);

    int index;
    Num value;

    // TV: 88,086.2; Date: 2025-Dec-31 23:00
    index = data.getEndIndex();
    value = indicator.getValue(index);
    assertEquals(88086.2, value.doubleValue(), 0.1);

    // TV: 87,516.0; Date: 2025-Dec-27 19:00
    index = data.getEndIndex() - 100;
    value = indicator.getValue(index);
    assertEquals(87516.0, value.doubleValue(), 0.1);

    // using default close price indicator
    indicator = new RangeFilterIndicator(data, 100, 3.0);

    // TV: 88,086.2; Date: 2025-Dec-31 23:00
    index = data.getEndIndex();
    value = indicator.getValue(index);
    assertEquals(88086.2, value.doubleValue(), 0.1);

    // TV: 87,516.0; Date: 2025-Dec-27 19:00
    index = data.getEndIndex() - 100;
    value = indicator.getValue(index);
    assertEquals(87516.0, value.doubleValue(), 0.1);
  }
}
