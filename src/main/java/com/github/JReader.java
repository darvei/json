package com.github;

import java.util.EmptyStackException;
import java.util.Stack;

import com.github.StringO;

/**
 * Using name to get value.
 * 
 * @author qiaozhy
 *
 */
public class JReader {
	
	/**
	 * Using pattern: 
	 * 1) name1:name2
	 * 2) name1:[2]
	 * 3) name1:[2]:name3
	 * 
	 * @param name
	 * @return
	 */
	public static boolean validate(String name) {
		if (StringO.isBlank(name)) return false;
		
		char[] chars = name.toCharArray();
		int length = chars.length;
		for (int i = 0; i < length; i ++) {
			if (JParser.isMeta(chars[i])) {
				if (i > 0 && JParser.META_QUOTE == chars[i - 1]) continue;
				else if (chars[i] != JParser.PAIR_SEPARATOR || chars[i] != JParser.ARRAY_START || chars[i] != JParser.ARRAY_END) return false;
			}
		}
		return true;
	}
	
	/**
	 * Get a sub element.
	 * 
	 * @param json
	 * @return
	 */
	public static String getASub(String json) {
		if (StringO.isBlank(json)) return StringO.EMPTY;
		String jstring = StringO.trim(json);
		
		StringBuffer sbuf = new StringBuffer();
		char[] chs = jstring.toCharArray();
		char chr = 0;
		Stack<Character> stack = new Stack<Character>();
		int length = chs.length;
		for (int i = 0; i < length; i ++) {
			char ch = chs[i];
					
			if ((JParser.CLASS_START == ch || JParser.ARRAY_START == ch) && (0 == i || i > 0 && JParser.META_QUOTE != chs[i - 1])) {
				stack.push(ch);
				try {
					chr = stack.peek();
				} catch (EmptyStackException e) {
					return StringO.EMPTY;
				}
			} else if ((JParser.CLASS_END == ch || JParser.ARRAY_END == ch) && (0 == i || i > 0 && JParser.META_QUOTE != chs[i - 1])) {
				if (JParser.CLASS_END == ch && JParser.CLASS_START == chr || JParser.ARRAY_END == ch && JParser.ARRAY_START == chr) {
					try {
						stack.pop();
						
						chr = 0;
						if (stack.size() > 0) chr = stack.peek();
					} catch (EmptyStackException e) {
						return StringO.EMPTY;
					}
				} else return sbuf.toString();
			} else if (JParser.ELEMENT_SEPARATOR == ch && (0 == i || i > 0 && JParser.META_QUOTE != chs[i - 1]) && stack.size() == 0) return sbuf.toString();

			sbuf.append(ch);
		}
		
		return sbuf.toString();
	}
	
	/**
	 * Skip a sub element.
	 * 
	 * @param json
	 * @return
	 */
	public static String skipASub(String json) {
		if (StringO.isBlank(json)) return StringO.EMPTY;
		String jstring = StringO.trim(json);
		
		char[] chs = jstring.toCharArray();
		Stack<Character> stack = new Stack<Character>();
		char chr = 0;
		int length = chs.length;
		for (int i = 0; i < length; i ++) {
			char ch = chs[i];
			
			if ((JParser.CLASS_START == ch || JParser.ARRAY_START == ch) && (0 == i || i > 0 && JParser.META_QUOTE != chs[i - 1])) {
				chr = ch;
				stack.push(ch);
			} else if ((JParser.CLASS_END == ch || JParser.ARRAY_END == ch) && (0 == i || i > 0 && JParser.META_QUOTE != chs[i - 1])) {
				if (JParser.CLASS_END == ch && JParser.CLASS_START == chr || JParser.ARRAY_END == ch && JParser.ARRAY_START == chr) {
					try {
						stack.pop();
						chr = 0;
						if (stack.size() > 0) chr = stack.peek();
					} catch (EmptyStackException e) {
						return StringO.EMPTY;
					}
				} else return jstring.substring(i + 1);
			} else if (JParser.ELEMENT_SEPARATOR == ch && (0 == i || i > 0 && JParser.META_QUOTE != chs[i - 1]) && stack.size() == 0) {
				try {
					return jstring.substring(i + 1);
				} catch (IndexOutOfBoundsException e) {
					break;
				}
			}
		}
		
		return StringO.EMPTY;
	}
	
	/**
	 * Either : and not \:
	 */
	private static final String REGEX_PAIR = "(?<![\\\\])[" + JParser.PAIR_SEPARATOR + "]";
	
	/**
	 * Using pattern: 
	 * 1) name1:[2]:...
	 * 2) [1]:...
	 * 3) :...
	 * 
	 * @param name
	 * @return value
	 */
	public static String getValue(String name, String json) {
		if (StringO.isBlank(name) || StringO.isBlank(json) || !JParser.validate(json)) return StringO.EMPTY;
		
		String jstring = json;
		
		String[] names = name.split(REGEX_PAIR);
		int length = names.length;
		for (int i = 0; i < length; i ++) {			
			String aname = StringO.trim(names[i]);
			if (StringO.isEmpty(aname)) {
				if (StringO.isBlank(jstring)) return StringO.EMPTY;
				jstring = StringO.trim(jstring);
				
				if (jstring.startsWith("" + JParser.ARRAY_START)) jstring = JParser.getSub("" + JParser.ARRAY_START, jstring);
				else if (jstring.startsWith("" + JParser.CLASS_START)) jstring = JParser.getSub("" + JParser.CLASS_START, jstring);
				else if (jstring.startsWith("" + JParser.PAIR_SEPARATOR)) jstring = JParser.getSub("" + JParser.PAIR_SEPARATOR, json);
				else return StringO.EMPTY;//format error.
			} else if (aname.contains("" + JParser.ARRAY_START) && aname.contains("" + JParser.ARRAY_END)) {
				int num = 0;
				try {
					num = Integer.parseInt(StringO.trim(StringO.between("" + JParser.ARRAY_START, "" + JParser.ARRAY_END, aname)));	
				} catch (NumberFormatException e) {
					return StringO.EMPTY;//format error.
				}
				
				if (StringO.isBlank(jstring)) return StringO.EMPTY;
				jstring = StringO.trim(jstring);
				
				if (jstring.startsWith("" + JParser.ARRAY_START)) {
					jstring = JParser.getSub("" + JParser.ARRAY_START, jstring);
					for (; num > 0; num --) jstring = skipASub(jstring);
					jstring = getASub(jstring);
				} else return StringO.EMPTY;				
			} else {
				jstring = StringO.subString(aname, "", jstring);
				jstring = StringO.subString("" + JParser.PAIR_SEPARATOR, "", jstring);
				jstring = getASub(jstring);
			}
		}
		
		return jstring;
	}

}
