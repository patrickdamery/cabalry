package com.cabalry.base;

/**
 * MovingAverage
 */
public class MovingAverage {

    private double[] mSamples;
    private double mSum;
    private int mCurrentSample;
    private int mSampleCount;

    public MovingAverage(int size) {
        mSamples = new double[size];
        mSum = 0;
        mCurrentSample = 0;
        mSampleCount = 0;
    }

    public void addSample(double sample) {
        if (mSampleCount == mSamples.length) {
            mSum -= mSamples[mCurrentSample];
        } else {
            mSampleCount++;
        }

        mSamples[mCurrentSample] = sample;
        mSum += sample;
        mCurrentSample++;

        if (mCurrentSample >= mSamples.length) {
            mCurrentSample = 0;
        }
    }

    public double getCurrentAverage() {
        if (mSampleCount != 0) {
            return mSum / mSampleCount;
        }

        return 0;
    }
}
