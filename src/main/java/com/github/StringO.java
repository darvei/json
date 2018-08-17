package com.github;

import java.nio.charset.Charset;
import java.util.regex.PatternSyntaxException;

/**
 * Based StringO
 * 
 * @author qiaozhy
 *
 */
public class StringO {

	public static final String EMPTY = "";
	
	public static final String TAB = "\t";
	public static final String NEWLINE = "\n";
	public static final String ENTER = "\r";
	public static final String SPACE = "\b";
	public static final String PAGE = "\f";

	public static boolean isNull(String string) {
		if (null == string) return true;
		return false;
	}

	public static String trim(String string) {
		if (!isNull(string)) return string.trim();
		return EMPTY;
	}

	/**
	 * if ""
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isEmpty(String string) {
		if (!isNull(string) && string.length() == 0) return true;
		return false;
	}

	public static boolean isNotEmpty(String string) {
		if (!isNull(string) && string.length() != 0) return true;
		return false;
	}

	/**
	 * if WHITES, trim to zero length
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isWhites(String string) {
		if (!isNull(string) && string.length() != 0 && string.trim().length() == 0) return true;
		return false;
	}

	public static boolean isNotWhites(String string) {
		if (isNull(string) || string.length() == 0 || string.trim().length() != 0) return true;
		return false;
	}


	/**
	 * If blank, is null or empty or whites.
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isBlank(String string) {
		if (isNull(string) || string.length() == 0 || string.trim().length() == 0) return true;
		return false;
	}

	/**
	 * Sometimes means useful.
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isNotBlank(String string) {
		if (!isNull(string) && string.length() != 0 && string.trim().length() != 0) return true;
		return false;
	}
	
	/**
	 * A standard substring function.
	 * 
	 * @param prefix
	 * @param suffix
	 * @param string
	 * @return
	 */
	public static String subString(String prefix, String suffix, String string) {
		if (isEmpty(string)) return EMPTY;		
		if (isEmpty(prefix) && isEmpty(suffix)) return string;
		
		if (isEmpty(prefix) && isNotEmpty(suffix)) {
			int posd = string.lastIndexOf(suffix);
			return string.substring(0, posd);
		}
		
		if (isEmpty(suffix) && isNotEmpty(prefix)) {
			int poss = string.indexOf(prefix);
			if (++ poss == string.length()) return EMPTY;
			return string.substring(poss);
		}
		
		int poss = string.indexOf(prefix);
		int posd = string.lastIndexOf(suffix);
		if (poss == posd || ++ poss == posd) return EMPTY;
		else if (poss < posd && poss > 0) return string.substring(poss, posd);
		else {
			if (0 == poss) return string.substring(0, posd);
			else if (-1 == posd) return string.substring(poss);
		}
		return EMPTY;
	}
	
	/**
	 * String between a start and/or an end tag. Eg:
	 * 1) [[abc] between [[] is abc.
	 * 2) [abc] between [[] is [abc] 
	 * 
	 * @param prefix
	 * @param suffix
	 * @param string
	 * @return
	 */
	public static String between(String prefix, String suffix, String string) {
		if (isEmpty(string)) return EMPTY;
		if (isEmpty(prefix) && isEmpty(suffix)) return string;
		
		String s = string;		
		int pos = 0;
		
		if (isNotEmpty(prefix)) {
			pos = string.indexOf(prefix);
			if (pos < 0) return EMPTY;
			
			pos += prefix.length();
			s = string.substring(pos);
		}
		
		pos = 0;	
		if (isNotEmpty(suffix)) {
			pos = s.lastIndexOf(suffix);
			if (pos < 0) return EMPTY;
			
			s = s.substring(0, pos);
		}
		return s;
	}
	
	/**
	 * System default using UTF-8, that is for chinese ,there could be 2-3-4 digits.
	 * 
	 */
	public static Charset getDefaultCharset() {
		return Charset.defaultCharset();
	}
	
	public static final int NIL = Integer.MIN_VALUE;
	public static final long NLL = Long.MIN_VALUE;
	
	public static int parseInt(String num) {
		try {
			return Integer.parseInt(num);
		} catch (NumberFormatException e) {
			return NIL;
		}
	}
	
	public static long parseLong(String num) {
		try {
			return Long.parseLong(num);
		} catch (NumberFormatException e) {
			return NLL;
		}
	}

}
