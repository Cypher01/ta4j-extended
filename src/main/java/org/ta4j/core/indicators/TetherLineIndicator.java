/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2024 Ta4j Organization & respective
 * authors (see AUTHORS)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.LowestValueIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.NumFactory;
/*
////////////////////////////////////////////////////////////
//  Copyright by HPotter v1.0 16/05/2014
// Tether line indicator is the first component of TFS trading strategy.
// It was named this way because stock prices have a tendency to cluster
// around it. It means that stock prices tend to move away from the midpoint
// between their 50-day highs and lows, then return to that midpoint at some
// time in the future. On a chart, it appears as though the stock price is
// tethered to this line, and hence the name.
////////////////////////////////////////////////////////////
study(title="TFS: Tether Line", shorttitle="Tether Line", overlay = true )
Length = input(50, minval=1)
lower = lowest(Length)
upper = highest(Length)
xTether = avg(upper, lower)
plot(xTether, color=green, title="Tether Line")
 */

/**
 * Tether Line Indicator.
 * <a href="https://www.tradingview.com/v/F17UGy9K/">TradingView</a>
 */

public class TetherLineIndicator extends AbstractIndicator<Num> {
    private final Indicator<Num> highestValueIndicator;
    private final Indicator<Num> lowestValueIndicator;
    private final int barCount;

    public TetherLineIndicator(BarSeries series, int barCount) {
        this(new ClosePriceIndicator(series), barCount);
    }

    public TetherLineIndicator(Indicator<Num> indicator, int barCount) {
        super(indicator.getBarSeries());

        this.barCount = barCount;
        this.highestValueIndicator = new HighestValueIndicator(new HighPriceIndicator(getBarSeries()), barCount);
        this.lowestValueIndicator = new LowestValueIndicator(new LowPriceIndicator(getBarSeries()), barCount);
    }

    @Override
    public Num getValue(int index) {
        final NumFactory numFactory = getBarSeries().numFactory();
        return highestValueIndicator.getValue(index).plus(lowestValueIndicator.getValue(index)).dividedBy(
                numFactory.numOf(2));
    }

    @Override
    public int getCountOfUnstableBars() {
        return barCount;
    }
}
