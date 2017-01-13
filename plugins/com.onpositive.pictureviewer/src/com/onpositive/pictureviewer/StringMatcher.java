/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package com.onpositive.pictureviewer;

import java.util.Vector;

public class StringMatcher {
	protected String fPattern;
	protected int fLength;
	protected boolean fIgnoreWildCards;
	protected boolean fIgnoreCase;
	protected boolean fHasLeadingStar;
	protected boolean fHasTrailingStar;
	protected String[] fSegments;
	protected int fBound = 0;
	protected static final char fSingleWildCard = 0;

	public StringMatcher(String pattern, boolean ignoreCase,
			boolean ignoreWildCards) {
		if (pattern == null) {
			throw new IllegalArgumentException();
		}
		this.fIgnoreCase = ignoreCase;
		this.fIgnoreWildCards = ignoreWildCards;
		this.fPattern = pattern;
		this.fLength = pattern.length();

		if (this.fIgnoreWildCards)
			parseNoWildCards();
		else
			parseWildCards();
	}

	public Position find(String text, int start, int end) {
		if (text == null) {
			throw new IllegalArgumentException();
		}

		int tlen = text.length();
		if (start < 0) {
			start = 0;
		}
		if (end > tlen) {
			end = tlen;
		}
		if ((end < 0) || (start >= end)) {
			return null;
		}
		if (this.fLength == 0) {
			return new Position(start, start);
		}
		if (this.fIgnoreWildCards) {
			int x = posIn(text, start, end);
			if (x < 0) {
				return null;
			}
			return new Position(x, x + this.fLength);
		}

		int segCount = this.fSegments.length;
		if (segCount == 0) {
			return new Position(start, end);
		}

		int curPos = start;
		int matchStart = -1;
		int i = 0;
		for (; (i < segCount) && (curPos < end); ++i) {
			String current = this.fSegments[i];
			int nextMatch = regExpPosIn(text, curPos, end, current);
			if (nextMatch < 0) {
				return null;
			}
			if (i == 0) {
				matchStart = nextMatch;
			}
			curPos = nextMatch + current.length();
		}
		if (i < segCount) {
			return null;
		}
		return new Position(matchStart, curPos);
	}

	public boolean match(String text) {
		if (text == null) {
			return false;
		}
		return match(text, 0, text.length());
	}

	public boolean match(String text, int start, int end) {
		if (text == null) {
			throw new IllegalArgumentException();
		}

		if (start > end) {
			return false;
		}

		if (this.fIgnoreWildCards) {
			return ((end - start == this.fLength) && (this.fPattern
					.regionMatches(this.fIgnoreCase, 0, text, start,
							this.fLength)));
		}
		int segCount = this.fSegments.length;
		if ((segCount == 0)
				&& (((this.fHasLeadingStar) || (this.fHasTrailingStar)))) {
			return true;
		}
		if (start == end) {
			return (this.fLength == 0);
		}
		if (this.fLength == 0) {
			return (start == end);
		}

		int tlen = text.length();
		if (start < 0) {
			start = 0;
		}
		if (end > tlen) {
			end = tlen;
		}

		int tCurPos = start;
		int bound = end - this.fBound;
		if (bound < 0) {
			return false;
		}
		int i = 0;
		String current = this.fSegments[i];
		int segLength = current.length();

		if (!(this.fHasLeadingStar)) {
			if (!(regExpRegionMatches(text, start, current, 0, segLength))) {
				return false;
			}
			++i;
			tCurPos += segLength;
		}

		if ((this.fSegments.length == 1) && (!(this.fHasLeadingStar))
				&& (!(this.fHasTrailingStar))) {
			return (tCurPos == end);
		}
		do {
			current = this.fSegments[i];

			int k = current.indexOf(0);
			int currentMatch;
			if (k < 0) {
				currentMatch = textPosIn(text, tCurPos, end, current);
				if (currentMatch < 0)
					return false;
			} else {
				currentMatch = regExpPosIn(text, tCurPos, end, current);
				if (currentMatch < 0) {
					return false;
				}
			}
			tCurPos = currentMatch + current.length();
			++i;
		} while (i < segCount);

		if ((!(this.fHasTrailingStar)) && (tCurPos != end)) {
			int clen = current.length();
			return regExpRegionMatches(text, end - clen, current, 0, clen);
		}
		return (i == segCount);
	}

	private void parseNoWildCards() {
		this.fSegments = new String[1];
		this.fSegments[0] = this.fPattern;
		this.fBound = this.fLength;
	}

	private void parseWildCards() {
		if (this.fPattern.startsWith("*")) {
			this.fHasLeadingStar = true;
		}
		if ((this.fPattern.endsWith("*")) && (this.fLength > 1)
				&& (this.fPattern.charAt(this.fLength - 2) != '\\')) {
			this.fHasTrailingStar = true;
		}

		Vector<String> temp = new Vector<String>();

		int pos = 0;
		StringBuffer buf = new StringBuffer();
		while (pos < this.fLength) {
			char c = this.fPattern.charAt(pos++);
			switch (c) {
			case '\\':
				if (pos >= this.fLength) {
					buf.append(c);
				} else {
					char next = this.fPattern.charAt(pos++);

					if ((next == '*') || (next == '?') || (next == '\\')) {
						buf.append(next);
					} else {
						buf.append(c);
						buf.append(next);
					}
				}
				break;
			case '*':
				if (buf.length() <= 0)
					continue;
				temp.addElement(buf.toString());
				this.fBound += buf.length();
				buf.setLength(0);

				break;
			case '?':
				buf.append('\0');
				break;
			default:
				buf.append(c);
			}

		}

		if (buf.length() > 0) {
			temp.addElement(buf.toString());
			this.fBound += buf.length();
		}

		this.fSegments = new String[temp.size()];
		temp.copyInto(this.fSegments);
	}

	protected int posIn(String text, int start, int end) {
		int max = end - this.fLength;

		if (!(this.fIgnoreCase)) {
			int i = text.indexOf(this.fPattern, start);
			if ((i == -1) || (i > max)) {
				return -1;
			}
			return i;
		}

		for (int i = start; i <= max; ++i) {
			if (text.regionMatches(true, i, this.fPattern, 0, this.fLength)) {
				return i;
			}
		}

		return -1;
	}

	protected int regExpPosIn(String text, int start, int end, String p) {
		int plen = p.length();

		int max = end - plen;
		for (int i = start; i <= max; ++i) {
			if (regExpRegionMatches(text, i, p, 0, plen)) {
				return i;
			}
		}
		return -1;
	}

	protected boolean regExpRegionMatches(String text, int tStart, String p,
			int pStart, int plen) {
		while (plen-- > 0) {
			char tchar = text.charAt(tStart++);
			char pchar = p.charAt(pStart++);

			if ((!(this.fIgnoreWildCards)) && (pchar == 0)) {
				continue;
			}

			if (pchar == tchar) {
				continue;
			}
			if (this.fIgnoreCase) {
				if (Character.toUpperCase(tchar) == Character
						.toUpperCase(pchar)) {
					continue;
				}

				if (Character.toLowerCase(tchar) == Character
						.toLowerCase(pchar)) {
					continue;
				}
			}
			return false;
		}
		return true;
	}

	protected int textPosIn(String text, int start, int end, String p) {
		int plen = p.length();
		int max = end - plen;

		if (!(this.fIgnoreCase)) {
			int i = text.indexOf(p, start);
			if ((i == -1) || (i > max)) {
				return -1;
			}
			return i;
		}

		for (int i = start; i <= max; ++i) {
			if (text.regionMatches(true, i, p, 0, plen)) {
				return i;
			}
		}

		return -1;
	}

	public static class Position {
		int start;
		int end;

		public Position(int start, int end) {
			this.start = start;
			this.end = end;
		}

		public int getStart() {
			return this.start;
		}

		public int getEnd() {
			return this.end;
		}
	}
}