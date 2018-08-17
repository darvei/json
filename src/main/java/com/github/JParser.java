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
		if (StringO.isBlank(json)) return false;

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
	 * Common interface for seek.
	 * 
	 * @param path
	 * @param json
	 * @return pos
	 */
	public static int locateMeta(String path, String json) {
		if (StringO.isBlank(json)) return -1;
		if (StringO.isBlank(path)) return 0;
		
		char[] ps = path.toCharArray();
		char[] js = json.toCharArray();
		
		int p = 0;
		int pLength = ps.length;
		int jsLength = js.length;
		
		char chr = 0; //for string quote
		for (int i = 0; i < jsLength; i ++) {
			if (!isMeta(js[i])) continue;
			
			if (STRING_QUOTE == chr) {
				if (STRING_QUOTE == js[i]) {
					if (i > 0 && META_QUOTE == js[i - 1]) continue;//"\""
					
					chr = 0;
					if (STRING_QUOTE == ps[p]) p++;
				}
			} else {
				if (STRING_QUOTE == js[i]) chr = STRING_QUOTE;
				if (js[i] == ps[p]) p++;
			} 
			if (p == pLength) return i;//found
		}
		return -1;
	}
	
	/**
	 * Common method to find a inner defined unit.
	 * 
	 * @param json
	 * @return
	 */
	public static int locateInner(String json) {
		if (StringO.isBlank(json)) return -1;
		
		Stack<Character> stack = new Stack<Character>();
		int length = json.length();
		int i = 0;
		char ch = 0;
		char chr = 0;
		for (; i < length; i ++) {
			ch = json.charAt(i);			
			if (!isMeta(ch)) continue;
			
			if (STRING_QUOTE == chr) {
				if (STRING_QUOTE == ch) {					
					if (i > 0 && META_QUOTE == json.charAt(i - 1)) continue;//"\""
					
					chr = 0;
					try {
						char meta = stack.pop();
						if (STRING_QUOTE != meta) return -1;
					} catch (EmptyStackException e) {
						return -1;
					}
					
					//if  (stack.isEmpty()) break; //found
				} else continue;
			} else if (STRING_QUOTE == ch) {
				chr = ch;
				stack.push(ch);
				continue;
			} else if (CLASS_START == ch || ARRAY_START == ch) {
				stack.push(ch);
				continue;
			} else if (CLASS_END == ch) {
				try {
					char meta = stack.pop();
					if (CLASS_START != meta) return -1;
				} catch (EmptyStackException e) {
					return -1;
				}
				
				if (stack.isEmpty()) break;
			} else if (ARRAY_END == ch) {
				try {
					char meta = stack.pop();
					if (ARRAY_START != meta)  return -1;
				} catch (EmptyStackException e) {
					return -1;
				}
				
				if (stack.isEmpty()) break;
			} else if (ELEMENT_SEPARATOR == ch) {
				if (stack.isEmpty()) stack.push(ch);
				else {
					char tmp = stack.pop();
					if ((ELEMENT_SEPARATOR == tmp || PAIR_SEPARATOR == tmp )&& stack.isEmpty()) break;
					else stack.push(tmp);
				}
			} else if (PAIR_SEPARATOR == ch && stack.isEmpty()) stack.push(ch);
		}
		if ( i == length) return -1;
		else return i;
	}
	
	/**
	 * Get a quoted string
	 * 
	 * @param json
	 * @return pair name or value, array element value, not quoted.
	 */
	public static String getAString(String json) {
		String js = StringO.trim(json);
		if (StringO.isEmpty(js) || !js.startsWith(""+STRING_QUOTE)) return StringO.EMPTY;
		
		int length = js.length();
		int i = 1;
		for (; i < length; i ++) {
			char ch = js.charAt(i);
			
			if (STRING_QUOTE == ch) {
				char chr = js.charAt(i - 1);
				if (META_QUOTE == chr) continue; 
				else break;//fond
			}
		}
		if (length == i) return StringO.EMPTY;
		js = js.substring(1, i);
		
		return js;
	}
	
	/**
	 * Skip a sub for navigation
	 * 
	 * @param json
	 * @return
	 */
	public static String skipASub(String json) {
		String js = StringO.trim(json);
		if (StringO.isEmpty(js)) return StringO.EMPTY;
				
		int pos = locateInner(js);
		if (pos < 0) return StringO.EMPTY;
		else {
			js = StringO.trim(js.substring(pos + 1));
			return js;
		}
	}
	
	public static String getASub(String json) {
		String js = StringO.trim(json);
		if (StringO.isEmpty(js)) return StringO.EMPTY;
				
		int pos = locateInner(js);
		if (pos < 0) return StringO.EMPTY;
		else {
			js = StringO.trim(js.substring(0, pos));
			return js;
		}
	}

	/**
	 * Generic method to parse a string.
	 * 
	 * @param path
	 * @param json
	 * @return
	 */
	public static String parse(String path, String json) {
		if (StringO.isBlank(path) || StringO.isBlank(json) || !isMeta(path)) return StringO.EMPTY;		
		String p = StringO.trim(path);
		String js = StringO.trim(json);		
		
		int pos = locateMeta(p, js);
		if (pos < 0) return StringO.EMPTY;	
		js = js.substring(pos);
		
		return getASub(js);
	}
	
	/**
	 * get value by name
	 */
	public static String getPairValue(String name, String pair) {
		if (StringO.isEmpty(pair)) return StringO.EMPTY;
		
		String p = StringO.trim(pair);		
		if (StringO.isEmpty(name) && p.startsWith(PAIR_SEPARATOR+"")) return p.substring(1);
		else {
			p = StringO.between(STRING_QUOTE+name+STRING_QUOTE, "", p);
			if (StringO.isEmpty(p)) return StringO.EMPTY;
			p = StringO.trim(p);
			if (p.startsWith(PAIR_SEPARATOR+"")) return p.substring(1);
			else return p;
		}
	}
	
	/**
	 * Query a value.
	 * 
	 * @param path
	 * name1:name2:[3]:name4
	 * :name1:
	 * :
	 * 
	 * @param json
	 * @return
	 */
	public static String query(String path, String json) {
		if (StringO.isBlank(path) || StringO.isBlank(json)) return StringO.EMPTY;
		
		String p = StringO.trim(path);
		String j = StringO.trim(json);
		
		while (StringO.isNotBlank(p)) {
			if (p.startsWith(""+STRING_QUOTE) || !p.startsWith(""+PAIR_SEPARATOR) || !p.startsWith(""+ARRAY_START)) {
				String aname = getAString(p);
				//with double-quote
				p = getPairValue(aname, p);

				j = trim(j);			
				while (StringO.isNotBlank(j)) { //hasNext
					String name = getAString(j);
					if (name.compareTo(aname) == 0) {
						j = getASub(j);
						p = getPairValue(name, p);
						
						break;
					} else j = skipASub(j);
				}
				if (StringO.isEmpty(j)) return StringO.EMPTY;
			} if (p.startsWith(""+ARRAY_START)) {
				j = StringO.trim(j);
				if (!j.startsWith(""+ARRAY_START)) return StringO.EMPTY; 				
				j = trim(j);
				
				int pos = p.indexOf(""+ARRAY_END);
				if (pos < 0) return StringO.EMPTY;
				String num = p.substring(1, pos);
				int n = -1;
				try {
					n = Integer.parseInt(num);
				} catch (NumberFormatException e) {}				
				if (-1 == n) return StringO.EMPTY;
				else {
					while (n-- > 0) j = skipASub(j);
					j = getASub(j);
					if (StringO.isEmpty(j)) return StringO.EMPTY;
				}
			} else if (p.startsWith(""+PAIR_SEPARATOR)) {//default one means first one
				p = p.substring(1);
				
				j = trim(j);
				j = StringO.trim(j);
				if (j.startsWith(""+PAIR_SEPARATOR)) {
					j = getASub(j);
					return getPairValue("", j);
				}
			} else return StringO.EMPTY;
		}
		
		return StringO.EMPTY;
	}
	
	/**
	 * Trim leading and ending metas.
	 * 
	 * @param json
	 * @return
	 */
	public static String trim(String json) {
		String js = StringO.trim(json);
		
		char ch = js.charAt(0);
		if (isMeta(ch)) js = js.substring(1);
		ch = 0;
		int length = js.length();
		if (length-- > 0) {
			ch = js.charAt(length);
			if (isMeta(ch)) js = js.substring(0, length);
		}
		return js;
	}
	
	class StringO {

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

	}
	
}
