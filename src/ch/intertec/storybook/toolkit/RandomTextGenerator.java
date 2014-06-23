/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008-2009 Martin Mustun

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.intertec.storybook.toolkit;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;


public class RandomTextGenerator {
	public static String generateText(int length) {
		Random randomGenerator = new Random();
		StringBuffer buf = new StringBuffer();
		int M = length;

		String s = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum.";
		String[] ss = s.split("\\s");
		shuffle(ss);
		s = StringUtils.join(ss, ' ');
		int k = s.length();
		int N = s.length(); // length of input string
		s = s + s.substring(0, k); // cyclic wrap-around to avoid dead-ending

		// generate pseudo-random text
		String current = s.substring(0, k);
		buf.append(current);
		for (int i = 0; i < M; i++) {
			String next = "";
			int matches = 0;
			for (int j = 0; j < N; j++) {
				if (current.equals(s.substring(j, j + k))) {
					matches++;
					int r = (int) (Math.random() * matches);
					if (r == 0)
						next = s.substring(j + 1, j + k + 1);
				}
			}
			buf.append(next.charAt(k - 1));
			current = next;
		}
		return StringUtils.left(buf.toString(),
				length + randomGenerator.nextInt(length / 5));
	}

	public static void shuffle(String[] a) {
		int N = a.length;
		for (int i = 0; i < N; i++) {
			int r = i + (int) (Math.random() * (N - i)); // between i and N-1
			exch(a, i, r);
		}
	}

	public static void exch(String[] a, int i, int j) {
		String swap = a[i];
		a[i] = a[j];
		a[j] = swap;
	}
}
