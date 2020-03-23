package com.leafriend.ex.whitenoise;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class App {

	public static void main(String[] args) {
		final Timer timer = new Timer();
		final TimerTask task = new TimerTask() {
			@Override
			public void run() {
				System.out.println("play() @ " + new Date().toString());
				play();
			}
		};
		timer.scheduleAtFixedRate(task, new Date(), 200);
	}

	public static double[] tone(double hz, double amplitude) {
		int n = (int) StdAudio.SAMPLE_RATE;
		double[] a = new double[n + 1];
		for (int i = 0; i <= n; i++) {
			a[i] = amplitude * Math.sin(2 * Math.PI * i * hz / StdAudio.SAMPLE_RATE);
		}
		return a;
	}

	public static void play() {
		double[] a = tone(80, 10);
		StdAudio.play(a);
	}

}
