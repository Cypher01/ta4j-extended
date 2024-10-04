package org.ta4j.core.indicators;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.LowestValueIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.num.Num;

/**
 * FantailVMA indicator by Bixord.
 * <a href="https://www.tradingview.com/script/O8tBKgX4-Bixord-FantailVMA/">TradingView</a>
 */
public class FantailVMAIndicator extends AbstractIndicator<Num> {
  private final Indicator<Num> fantailVMAIndicator;

  public FantailVMAIndicator(BarSeries series, int adxLength, int weighting, int barCount) {
    super(series);
    this.fantailVMAIndicator = new SMAIndicator(new VarMAIndicator(series, adxLength, weighting), barCount);
  }

  @Override
  public Num getValue(int index) {
    return fantailVMAIndicator.getValue(index);
  }

  @Override
  public int getUnstableBars() {
    return fantailVMAIndicator.getUnstableBars();
  }

  private static class VarMAIndicator extends CachedIndicator<Num> {
    private final Indicator<Num> adx;
    private final Indicator<Num> adxLow;
    private final Indicator<Num> diff;

    protected VarMAIndicator(BarSeries series, int adxLength, int weighting) {
      super(series);

      HighPriceIndicator highPrice = new HighPriceIndicator(series);
      LowPriceIndicator lowPrice = new LowPriceIndicator(series);
      PreviousValueIndicator previousHighPrice = new PreviousValueIndicator(highPrice);
      PreviousValueIndicator previousLowPrice = new PreviousValueIndicator(lowPrice);
      CombineIndicator highPriceChange = CombineIndicator.minus(highPrice, previousHighPrice);
      CombineIndicator lowPriceChange = CombineIndicator.minus(previousLowPrice, lowPrice);

      TransformIndicator bulls1 = TransformIndicator.divide(CombineIndicator.plus(TransformIndicator.abs(highPriceChange), highPriceChange), 2);
      TransformIndicator bears1 = TransformIndicator.divide(CombineIndicator.plus(TransformIndicator.abs(lowPriceChange), lowPriceChange), 2);

      SPDIIndicator sPDI = new SPDIIndicator(bulls1, bears1, weighting);
      SMDIIndicator sMDI = new SMDIIndicator(bulls1, bears1, weighting);
      STRIndicator sTR = new STRIndicator(series, weighting);

      this.adx = new ADXIndicator(sPDI, sMDI, sTR, weighting);
      HighestValueIndicator adxHigh = new HighestValueIndicator(adx, adxLength);
      this.adxLow = new LowestValueIndicator(adx, adxLength);
      this.diff = CombineIndicator.minus(adxHigh, adxLow);
    }

    @Override
    protected Num calculate(int index) {
      Num closePrice = getBarSeries().getBar(index).getClosePrice();

      if (index == 0) {
        return closePrice;
      }

      Num constVal = diff.getValue(index).isPositive() ? adx.getValue(index).minus(adxLow.getValue(index)).dividedBy(diff.getValue(index)) : getBarSeries().numFactory().zero();
      return getValue(index - 1)
        .multipliedBy(getBarSeries().numFactory().numOf(2).minus(constVal))
        .plus(constVal.multipliedBy(closePrice))
        .dividedBy(getBarSeries().numFactory().numOf(2));
    }

    @Override
    public int getUnstableBars() {
      return 1;
    }
  }

  private static class SPDIIndicator extends CachedIndicator<Num> {
    private final Indicator<Num> bulls1;
    private final Indicator<Num> bears1;
    private final Num weighting;

    protected SPDIIndicator(Indicator<Num> bulls1, Indicator<Num> bears1, int weighting) {
      super(bulls1);
      this.bulls1 = bulls1;
      this.bears1 = bears1;
      this.weighting = getBarSeries().numFactory().numOf(weighting);
    }

    @Override
    protected Num calculate(int index) {
      if (index == 0) {
        return getBarSeries().numFactory().zero();
      }

      Num bullsValue = bulls1.getValue(index).isLessThanOrEqual(bears1.getValue(index)) ? getBarSeries().numFactory().zero() : bulls1.getValue(index);
      return getValue(index - 1).multipliedBy(weighting).plus(bullsValue).dividedBy(weighting.plus(getBarSeries().numFactory().one()));
    }

    @Override
    public int getUnstableBars() {
      return 1;
    }
  }

  private static class SMDIIndicator extends CachedIndicator<Num> {
    private final Indicator<Num> bulls1;
    private final Indicator<Num> bears1;
    private final Num weighting;

    protected SMDIIndicator(Indicator<Num> bulls1, Indicator<Num> bears1, int weighting) {
      super(bulls1);
      this.bulls1 = bulls1;
      this.bears1 = bears1;
      this.weighting = getBarSeries().numFactory().numOf(weighting);
    }

    @Override
    protected Num calculate(int index) {
      if (index == 0) {
        return getBarSeries().numFactory().zero();
      }

      Num bearsValue = bulls1.getValue(index).isGreaterThanOrEqual(bears1.getValue(index)) ? getBarSeries().numFactory().zero() : bears1.getValue(index);
      return getValue(index - 1).multipliedBy(weighting).plus(bearsValue).dividedBy(weighting.plus(getBarSeries().numFactory().one()));
    }

    @Override
    public int getUnstableBars() {
      return 1;
    }
  }

  private static class STRIndicator extends CachedIndicator<Num> {
    private final Num weighting;

    protected STRIndicator(BarSeries series, int weighting) {
      super(series);
      this.weighting = getBarSeries().numFactory().numOf(weighting);
    }

    @Override
    protected Num calculate(int index) {
      Bar bar = getBarSeries().getBar(index);
      Num highPrice = bar.getHighPrice();
      Num lowPrice = bar.getLowPrice();

      if (index == 0) {
        return highPrice.minus(lowPrice);
      }

      Num prevClosePrice = getBarSeries().getBar(index - 1).getClosePrice();
      Num tr = highPrice.minus(lowPrice).max(highPrice.minus(prevClosePrice));
      return getValue(index - 1).multipliedBy(weighting).plus(tr).dividedBy(weighting.plus(getBarSeries().numFactory().one()));
    }

    @Override
    public int getUnstableBars() {
      return 1;
    }
  }

  private static class ADXIndicator extends CachedIndicator<Num> {
    private final Indicator<Num> sPDI;
    private final Indicator<Num> sMDI;
    private final Indicator<Num> sTR;
    private final Num weighting;

    protected ADXIndicator(Indicator<Num> sPDI, Indicator<Num> sMDI, Indicator<Num> sTR, int weighting) {
      super(sPDI);
      this.sPDI = sPDI;
      this.sMDI = sMDI;
      this.sTR = sTR;
      this.weighting = getBarSeries().numFactory().numOf(weighting);
    }

    @Override
    protected Num calculate(int index) {
      if (index == 0) {
        return getBarSeries().numFactory().zero();
      }

      Num sTRValue = sTR.getValue(index);
      Num pdi = sTRValue.isPositive() ? sPDI.getValue(index).dividedBy(sTRValue) : getBarSeries().numFactory().zero();
      Num mdi = sTRValue.isPositive() ? sMDI.getValue(index).dividedBy(sTRValue) : getBarSeries().numFactory().zero();
      Num dx = pdi.plus(mdi).isPositive() ? pdi.minus(mdi).abs().dividedBy(pdi.plus(mdi)) : getBarSeries().numFactory().zero();
      return getValue(index - 1).multipliedBy(weighting).plus(dx).dividedBy(weighting.plus(getBarSeries().numFactory().one()));
    }

    @Override
    public int getUnstableBars() {
      return 1;
    }
  }
}
