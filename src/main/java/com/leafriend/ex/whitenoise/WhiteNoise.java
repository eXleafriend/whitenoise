package com.leafriend.ex.whitenoise;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class WhiteNoise implements AutoCloseable {

	private static final int SAMPLE_BUFFER_SIZE = 4096;

	private static final int BYTES_PER_SAMPLE = 2; // 16-bit audio

	private static final double MAX_16_BIT = 32768;

	//

	public static final int SAMPLE_RATE = 44100;

	private static final int BITS_PER_SAMPLE = 16; // 16-bit audio
	
	private static final int MONO = 1;

	private static final boolean SIGNED = true;

	private static final boolean LITTLE_ENDIAN = false;

	private final byte[] buffer = new byte[SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE / 3];

	private int bufferSize = 0;

	private final AudioFormat format = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, MONO, SIGNED,
			LITTLE_ENDIAN);

	private final DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

	private SourceDataLine line;

	public void play(final double[] samples) throws LineUnavailableException {

		if (samples == null)
			throw new IllegalArgumentException("argument to play() is null");
		for (int i = 0; i < samples.length; i++) {
			play(samples[i]);
		}

	}

	public void play(double sample) throws LineUnavailableException {

		if (line == null) {
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.start();
		}

		if (Double.isNaN(sample))
			throw new IllegalArgumentException("sample is NaN");

		// System.out.println("buffer.length: " + buffer.length);

		// clip if outside [-1, +1]
		if (sample < -1.0)
			sample = -1.0;
		if (sample > +1.0)
			sample = +1.0;

		// convert to bytes
		short s = (short) (MAX_16_BIT * sample);
		if (sample == 1.0)
			s = Short.MAX_VALUE; // special case since 32768 not a short

		buffer[bufferSize++] = (byte) s;
		buffer[bufferSize++] = (byte) (s >> 8); // little endian

		if (bufferSize >= buffer.length) {
			System.out.println("Flush");
			line.write(buffer, 0, bufferSize);
			bufferSize = 0;
		}

	}

	@Override
	public void close() {
		if (line != null) {
			line.drain();
			line.stop();
		}
	}

}
