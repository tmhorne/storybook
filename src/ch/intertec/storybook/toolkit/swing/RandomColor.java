package ch.intertec.storybook.toolkit.swing;

import java.awt.Color;
import java.util.Random;

public class RandomColor {
	private Random rand;

	public RandomColor() {
		rand = new Random();
	}

	public Color randomColor() {
		return (new Color(rand.nextInt(256), rand.nextInt(256),
				rand.nextInt(256)));
	}

	public Color randomPastelColor() {
		return (new Color(128 + rand.nextInt(128), 128 + rand.nextInt(128),
				128 + rand.nextInt(128)));
	}

	public Color randomGray() {
		int intensity = rand.nextInt(256);
		return (new Color(intensity, intensity, intensity));
	}
}
