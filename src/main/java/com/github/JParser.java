package com.github;

import java.util.Stack;

import com.github.StringO;

/**
 * JSON Service
 * 
 * @version 2.0 Fixed with \META.
 * @author qiaozhy
 *
 */
public class JParser {

	public static final char META_QUOTE = '\\';
	public static final char CLASS_START = '{';
	public static final char CLASS_END = '}';
	public static final char ARRAY_START = '[';
	public static final char ARRAY_END = ']';
	public static final char ELEMENT_SEPARATOR = ',';
	public static final char PAIR_SEPARATOR = ':';
	public static final char STRING_QUOTE = '\"';
	
	public static final String EMPTY = "{}";
	
	public static boolean isEmpty(String json) {
		if(0 == EMPTY.compareTo(json)) return true;
		return false;
	}

	private static boolean isMeta(String chars) {
		if (StringO.isNotBlank(chars)) {
			char[] token = chars.toCharArray();
			for (char ch : token) {
				switch (ch) {
				case CLASS_START:
					continue;
				case CLASS_END:
					continue;
				case ARRAY_START:
					continue;
				case ARRAY_END:
					continue;
				case ELEMENT_SEPARATOR:
					continue;
				case PAIR_SEPARATOR:
					continue;
				case STRING_QUOTE:
					continue;
				default:
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public static boolean isMeta(char ch) {
		switch (ch) {
		case CLASS_START:
			return true;
		case CLASS_END:
			return true;
		case ARRAY_START:
			return true;
		case ARRAY_END:
			return true;
		case ELEMENT_SEPARATOR:
			return true;
		case PAIR_SEPARATOR:
			return true;
		case STRING_QUOTE:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Simply validate JSON as in closed definition.
	 * {}
	 * :
	 * []
	 * ,
	 * ""
	 * 
	 * @return
	 */
	public static boolean validate(String json) {
		if (StringUtil.isBlank(json)) return false;

		Stack<Character> stack = new Stack<Character>();
		char[] jstring = json.toCharArray();
		int length = jstring.length;
		// for(char ch : jstring) {
		char chr = 0; //last double quote
		for (int i = 0; i < length; i++) {
			char ch = jstring[i];
			if (!isMeta(ch)) continue;
			if (STRING_QUOTE == chr) {
				if (STRING_QUOTE == ch) {
					if (i > 0 && META_QUOTE == jstring[i - 1]) continue;//"\""
					
					chr = 0;
					try {
						char meta = stack.pop();
						if (STRING_QUOTE != meta) return false;
					} catch (EmptyStackException e) {
						return false;
					}
				} else continue;
			} else if (i > 0 && META_QUOTE == jstring[i - 1]) return false;
			else if (STRING_QUOTE == ch) {
				chr = ch;
				stack.push(ch);
			}
			else if (CLASS_START == ch || ARRAY_START == ch) stack.push(ch);
			else if (CLASS_END == ch) {
				try {
					char meta = stack.pop();
					if (CLASS_START != meta) return false;
				} catch (EmptyStackException e) {
					return false;
				}
			} else if (ARRAY_END == ch) {
				try {
					char meta = stack.pop();
					if (ARRAY_START != meta) return false;
				} catch (EmptyStackException e) {
					return false;
				}
			}
		}

		if (stack.isEmpty()) return true;
		return false;
	}

	/**
	 * Using path to indicate location of JSON. For example: {[,
	 * 
	 * @param path
	 * @return
	 */
	public static String getValue(String path, String json) {
		if (StringO.isNotBlank(path) && isMeta(path) && StringO.isNotBlank(json) && validate(json) ) {
			char[] chars = path.toCharArray();

			int pos = -1;			
			for(char ch : chars) pos = json.indexOf(ch, (pos + 1));		

			String object = json.substring(++ pos);
			if (StringO.isNull(object) || StringO.isEmpty(object)) return StringO.EMPTY; //no found
			
			if (STRING_QUOTE == chars[chars.length - 1]) {
				do {
					pos = 0;
					pos = object.indexOf(STRING_QUOTE);
					if (pos < 0) return StringO.EMPTY; //error, not closed.
				} while (META_QUOTE == object.charAt(pos - 1)); //\meta
				
				object = object.substring(0, pos);
				return object;
			}
			
			chars = null;
			pos = 0;
			
			chars = object.toCharArray();
			//for(char ch : chars) {
			int length = object.length();
			for (int i = 0; i < length; i ++) {
				char ch = chars[i];
				if (isMeta(ch) && STRING_QUOTE != ch && i > 0 &&  META_QUOTE != chars[i - 1]) break; //\meta 
				else if (isMeta(ch) && STRING_QUOTE != ch) break; 

				pos ++;
			}
			object = object.substring(0, pos);
			if (object.startsWith(""+STRING_QUOTE)) object = StringO.subString(""+STRING_QUOTE, ""+STRING_QUOTE, object);
			
			return object;
		}
		return StringO.EMPTY; //no found
	}
	
	public static String getSub(String path, String json) {
		if (StringO.isNotBlank(path) && isMeta(path) && StringO.isNotBlank(json) && validate(json) ) {
			char[] chars = path.toCharArray();
			
			int pos = -1;			
			for(char ch : chars) pos = json.indexOf(ch, (pos + 1));
			if (pos < 0) return StringO.EMPTY;//no found
			String object = json.substring(++ pos);
			if (StringO.isNull(object) || StringO.isEmpty(object)) return StringO.EMPTY;//no found
			
			if (STRING_QUOTE == chars[chars.length - 1]) {
				/*pos = 0;
				pos = object.indexOf(STRING_QUOTE);*/
				do {
					pos = 0;
					pos = object.indexOf(STRING_QUOTE);
					if (pos < 0) return StringO.EMPTY; //error, not closed.
				} while (META_QUOTE == object.charAt(pos - 1)); //\meta
				
				object = object.substring(0, pos);
				return object;
			}
			
			chars = null;
			pos = 0;
			
			chars = object.toCharArray();
			Stack<Character> signs = new Stack<Character>();
			/*for(char ch : chars) {*/
			int length = object.length();
			for (int i = 0; i < length; i ++) {
				pos ++;
				char ch = chars[i];
				
				if (isMeta(ch) && STRING_QUOTE != ch && (i > 0 &&  META_QUOTE != chars[i - 1] || 0 == i)) {// \meta					
					if (signs.empty()) {
						//check if normal signs
						if (CLASS_END == ch || ARRAY_END == ch /*|| ELEMENT_SEPARATOR == ch*/) break;
						else if (CLASS_START == ch || ARRAY_START == ch) signs.push(ch);
					} else {
						if (CLASS_START == ch || ARRAY_START == ch) signs.push(ch);
						else if (CLASS_END == ch || ARRAY_END == ch){
							/*char prev =*/ signs.pop();//if json is validated, then automatically matched.							
							if (signs.empty()) break;
						}
					}
				}
			}
			
			object = object.substring(0, pos);
			if (object.startsWith(""+STRING_QUOTE)) object = StringO.subString(""+STRING_QUOTE, ""+STRING_QUOTE, object);
			
			return object;
		}
		return StringO.EMPTY; //no found
	}
	
}
