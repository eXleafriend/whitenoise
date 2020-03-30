package com.leafriend.ex.whitenoise;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class App { // extends TimerTask {

	/**
	 * The sample rate: 44,100 Hz for CD quality audio.
	 */
	public static final int SAMPLE_RATE = 44100;

	public static final int BYTES_PER_SAMPLE = 2; // 16-bit audio
	public static final int BITS_PER_SAMPLE = 16; // 16-bit audio
	public static final double MAX_16_BIT = 32768;
	public static final int SAMPLE_BUFFER_SIZE = 4096;

	public static final int BUFFER_SIZE = SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE;

	public static final int MONO = 1;
	public static final int STEREO = 2;
	public static final boolean LITTLE_ENDIAN = false;
	public static final boolean BIG_ENDIAN = true;
	public static final boolean SIGNED = true;
	public static final boolean UNSIGNED = false;

	public static void main(String[] args) throws LineUnavailableException {

		final double hz = 261.6 / 4;
		final double amplitude = 0.01;
		final long period = 1000; // in millisecond

		final AudioFormat format = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, MONO, SIGNED, LITTLE_ENDIAN);
		final DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
		line.open(format, BUFFER_SIZE);
		line.start();

		final Timer timer = new Timer();
		final TimerTask task = new TimerTask() {

			private int cursor = 0;
			private final byte[] buffer = new byte[line.available()];

			@Override
			public void run() {

				for (int i = 0; i <= 1 + SAMPLE_RATE * period / 1000; i++) {
					double sample = amplitude * Math.sin(2 * Math.PI * i * hz / SAMPLE_RATE);

					// clip if outside [-1, +1]
					if (sample < -1.0)
						sample = -1.0;
					if (sample > +1.0)
						sample = +1.0;

					// convert to bytes
					short s = (short) (MAX_16_BIT * sample);
					if (sample == 1.0)
						s = Short.MAX_VALUE; // special case since 32768 not a short
					buffer[cursor++] = (byte) s;
					buffer[cursor++] = (byte) (s >> 8); // little endian

					// send to sound card if buffer is full
					if (cursor >= buffer.length) {
						line.write(buffer, 0, buffer.length);
						cursor = 0;
					}
				}

			}

		};
		timer.scheduleAtFixedRate(task, new Date(), period);

		// line.drain();
		// line.stop();
		// line.close();

	}

}
