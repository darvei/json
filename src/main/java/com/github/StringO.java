package com.github;

import java.nio.charset.Charset;
import java.util.regex.PatternSyntaxException;

/**
 * Based StringO
 * 
 * @author qiaozhy
 *
 */
public class StringO extends ObjectO {

	public static final String EMPTY = "";
	
	public static final String TAB = "\t";
	public static final String NEWLINE = "\n";
	public static final String ENTER = "\r";
	public static final String SPACE = "\b";
	public static final String PAGE = "\f";

	public StringO(Object object) {
		super(object);
	}

	public static boolean isNull(String string) {
		if (NULL == string) return true;
		return false;
	}

	public String getString() {
		if (isNull()) return EMPTY;

		Object obj = this.get();
		if (obj instanceof String) return (String) obj;
		else return EMPTY;
	}

	public static String trim(String string) {
		if (!isNull(string)) return string.trim();
		return EMPTY;
	}

	public String trim() {
		if (isNull()) return EMPTY;
		return this.getString().trim();
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
	
	public boolean isEmpty() {
		if(!isNull() && this.getString().length() == 0) return true;
		return false;
	}

	public static boolean isNotEmpty(String string) {
		if (!isNull(string) && string.length() != 0) return true;
		return false;
	}
	
	public boolean isNotEmpty() {
		if (!isNull() && this.getString().length() != 0) return true;
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
	
	public boolean isWhites() {
		if (!isNull() && this.getString().length() != 0 && this.getString().trim().length() == 0) return true;
		return false;
	}

	public static boolean isNotWhites(String string) {
		if (isNull(string) || string.length() == 0 || string.trim().length() != 0) return true;
		return false;
	}
	
	public boolean isNotWhites() {
		if (isNull() || this.getString().length() == 0 || this.getString().trim().length() != 0) return true;
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
	
	public boolean isBlank() {
		if (isNull() || this.getString().length() == 0 || this.getString().trim().length() == 0) return true;
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
	
	public boolean isNotBlank() {
		if (!isNull() || this.getString().length() != 0 || this.getString().trim().length() != 0) return true;
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
	
	public String subString(String prefix, String suffix) {
		String string = this.getString();
		return subString(prefix, suffix, string);
	}
	
	/**
	 * String between a start and an end tag. Eg:
	 * 1) [[abc] between [[] is abc.
	 * 2) [abc] between [[] is [abc] 
	 * 
	 * @param prefix
	 * @param suffix
	 * @param string
	 * @return
	 */
	public static String between(String prefix, String suffix, String string) {
		String s = string;
		if (isNotEmpty(prefix) && isNotEmpty(suffix) && isNotEmpty(s)) {
			int start = 0;
			start = string.indexOf(prefix);
			if (start < 0) return string;
			
			start += prefix.length();
			s = string.substring(start);
			
			start = 0;			
			start = s.indexOf(suffix);
			if (start < 0) return string;
			
			start += suffix.length() - 1;
			s = s.substring(0, start);
		}
		return s;
	}
	
	public String between(String prefix, String suffix) {
		String string = this.getString();
		return between(prefix, suffix, string);
	}
	
	/**
	 * The first after part.
	 * @param begin
	 * @param string
	 * @return
	 */
	/*public static String firstIndexAfter(String begin, String string) {
		if (isNotEmpty(string) && isNotEmpty(begin)) {
			int pos = string.indexOf(begin);
			if (pos > 0) {
				pos += begin.length();
				return string.substring(pos, string.length());
			}
		}
		return string;
	}*/
	
	/**
	 * The last before part.
	 */
	public static String lastIndexBefore(String tag, String string) {
		if (isNotEmpty(string) && isNotEmpty(tag)) {
			int pos = string.lastIndexOf(tag);
			if (pos < string.length() && pos >= 0) {
				return string.substring(0, pos);
			}
		}
		return string;
	}
	
	/**
	 * The last after part.
	 */
	public static String lastIndexAfter(String tag, String string) {
		if (isNotEmpty(string) && isNotEmpty(tag)) {
			int pos = string.lastIndexOf(tag);
			if (pos < string.length() && pos >= 0) {
				pos ++;
				return string.substring(pos, string.length());
			}
		}
		return string;
	}
	
	public static String[] split(String string, String exp) {
		if (StringO.isBlank(string)) return new String[0];
		
		try {
			return string.split(exp);
		} catch (PatternSyntaxException e) {
			return new String[0];
		}
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