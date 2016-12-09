package newbee.morningGlory.http.servlets.sys.bsh


import static groovy.json.JsonTokenType.*
import groovy.*
import groovy.json.*

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.text.DateFormat
import java.text.SimpleDateFormat

import sophia.foundation.property.PropertyDictionary

/**
 * Class responsible for the actual String serialization of the possible values of a JSON structure.
 * This class can also be used as a category, so as to add <code>toJson()</code> methods to various types.
 *
 * @author Guillaume Laforge
 * @author Roshan Dawrani
 * @since 1.8.0
 */
class HtmlOutput {

	/**
	 * Date formatter for outputting dates to a string
	 * that can be parsed back from JavaScript with:
	 * <code>Date.parse(stringRepresentation)</code>
	 */
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)

	private static List<Object> objectChecker = new ArrayList<Object>();
	
	static {
		dateFormat.timeZone = TimeZone.getTimeZone('GMT')
	}

	static void clear()
	{
		objectChecker.clear();
	}
	
	/**
	 * @return "true" or "false" for a boolean value
	 */
	static String toJson(Boolean bool) {
		bool.toString()
	}

	/**
	 * @return a string representation for a number
	 * @throws JsonException if the number is infinite or not a number.
	 */
	static String toJson(Number n) {
		if (n.class in [Double, Float] && (n.isInfinite() || n.isNaN())) {
			throw new JsonException("Number ${n} can't be serialized as JSON: infinite or NaN are not allowed in JSON.")
		}
		n.toString()
	}

	/**
	 * @return a JSON string representation of the character
	 */
	static String toJson(Character c) {
		"\"$c\""
	}

	/**
	 * @return a properly encoded string with escape sequences
	 */
	static String toJson(String s) {
		"\"${StringEscapeUtils.escapeJava(s)}\""
	}

	/**
	 * Format a date that is parseable from JavaScript, according to ISO-8601.
	 *
	 * @param date the date to format to a JON string
	 * @return a formatted date in the form of a string
	 */
	static String toJson(Date date) {
		"\"${dateFormat.format(date)}\""
	}

	/**
	 * Format a calendar instance that is parseable from JavaScript, according to ISO-8601.
	 *
	 * @param cal the calendar to format to a JSON string
	 * @return a formatted date in the form of a string
	 */
	static String toJson(Calendar cal) {
		"\"${dateFormat.format(cal.time)}\""
	}

	/**
	 * @return the string representation of an uuid
	 */
	static String toJson(UUID uuid) {
		"\"${uuid.toString()}\""
	}

	/**
	 * @return the string representation of the URL
	 */
	static String toJson(URL url) {
		"\"${url.toString()}\""
	}

	/**
	 * @return an object representation of a closure
	 */
	static String toJson(Closure closure) {
		toJson(JsonDelegate.cloneDelegateAndGetContent(closure))
	}

	static String toJsonHtml(object,code,depth)
	{
		if (object == null) {
			"null"
		}
		else
		{
			if( object.getClass() == null )
			{
				System.out.println(object.toString())
				return "\"class Null\""
			}
			else if (object instanceof PropertyDictionary) {
				return JsonOutput.toJson(object);
			}
			// TODO FIXME 暂时屏蔽
//			else if (object instanceof PropertyDictionaryWapper) {
//				return JsonOutput.toJson(object);
//			} 
			else if (object instanceof Enum) {
				'"' + object.name() + '"'
			}
			else if( isBaseType(object) )
			{
				return JsonOutput.toJson(object.toString());
			}
//			else if (object instanceof Collection ||
//					object.getClass().isArray() ||
//					object instanceof Iterator ||
//					object instanceof Enumeration) {
//				"[" + object.collect {
//						if( PrintObject.isBaseType(it) ) 
//							toJson(it)
//						else
//							toJson( it.getClass().getSimpleName() , it , code,depth)
//					}.join(',') + "]"
//			}
			else {
				try{
					return toJson(toList(object),code,depth)
				}
				catch(Exception ex)
				{
					ex.printStackTrace()
					System.out.println(object.getClass())
					return "\"Error\""
				}
			}
		}
	}
	
	/**
	 * @return "null" for a null value, or a JSON array representation for a collection, array, iterator or enumeration.
	 */
	static String toJson(key,object,code,depth) {
		if (object == null) {
			"null"
		}
		else 
		{
			String url = "bsh?code=" + URLEncoder.encode(code, "utf-8") + "&depth=" + URLEncoder.encode(depth+"." + key, "utf-8");
			"\"<a target='_blank' href='" + url + "'>"+object.getClass().getSimpleName()+"</a>\"";
		}
	}

	/**
	 * @return a JSON object representation for a map
	 */
	static String toJson(Map m,code,depth) {
		"{" + m.collect { k, v ->
				if (k == null) {
					throw new IllegalArgumentException('Null key for a Map not allowed')
				}
				if( v == null )
				{
					'"' + k.toString() + '":null'
				}
				else if( isBaseType(v) )
				{
					'"' + k.toString() + '":' + toJson(v.toString())
				}
				else if (v instanceof PropertyDictionary) {
					"\"<font style='color:blue'><b>" + k.toString() + '</b></font>":' + toJson(k,v,code,depth)
				}
				// TODO FIXME 暂时屏蔽
//				else if (v instanceof PropertyDictionaryWapper) {
//					"\"<font style='color:blue'><b>" + k.toString() + '</b></font>":' + toJson(k,v,code,depth)
//				}
				else
				{
					'"' + k.toString() + '":' + toJson(k,v,code,depth)
				}
		}.join(',') + "}";
	}

	
	/**
	 * Pretty print a JSON payload
	 *
	 * @param jsonPayload
	 * @return
	 */
	static String prettyPrint(String jsonPayload) {

		int indent = 0
		def output = new StringBuilder()
		def lexer = new JsonLexer(new StringReader(jsonPayload))

		while (lexer.hasNext()) {
			JsonToken token = lexer.next()
			if (token.type == OPEN_CURLY) {
				indent += 4
				output.append('{\n')
				output.append(' ' * indent)
			} else if (token.type == CLOSE_CURLY) {
				indent -= 4
				output.append('\n')
				output.append(' ' * indent)
				output.append('}')
			} else if(token.type == OPEN_BRACKET) {
				indent += 4
				output.append('[\n')
				output.append(' ' * indent)
			} else if(token.type == CLOSE_BRACKET) {
				indent -= 4
				output.append('\n')
				output.append(' ' * indent)
				output.append(']')
			} else if (token.type == COMMA) {
				output.append(',\n')
				output.append(' ' * indent)
			} else if (token.type == COLON) {
				output.append(': ')
			} else {
				output.append(token.text)
			}
		}

		return output.toString()
	}
	
	public static Map<String,Object> toList(Object obj) throws IllegalArgumentException, IllegalAccessException {
		
		Map<String,Object> map = new LinkedHashMap<String,Object>();
		if (obj instanceof Collection ||
				obj.getClass().isArray() ||
				obj instanceof Iterator ||
				obj instanceof Enumeration) {

				int index = 0;
				
				obj.collect { 
						map.put( "["+String.valueOf(index)+"]",it )
						index ++;
					}
				
		}
		else
		{
			List<Field> fields = getFields(obj.getClass());
			for (Field field : fields) {
//				if (!Modifier.isStatic(field.getModifiers())&& !Modifier.isVolatile(field.getModifiers()) && !Modifier.isInterface(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
				
				String cls = "";
				if( Modifier.isStatic(field.getModifiers()) )
					cls = "Static";
					field.setAccessible(true);
					if( !cls.equals("") )
						map.put(field.getName()+" # "+cls, field.get(obj));
					else
						map.put(field.getName(), field.get(obj));
//				}
			}
		}
		return map;
	}
	
//	public static String toJson(Object obj) throws IllegalArgumentException, IllegalAccessException{
//		return HtmlOutput.toJson(toList(obj));
//	}
	
	public static List<Field> getFields(Class<?> type){
		List<Field> fields = new ArrayList<Field>();
		for (Class<?> class1 : getAllSuper(type)) {
			if (!class1.equals(Object.class)) {
				fields.addAll(Arrays.asList(class1.getDeclaredFields()));
			}
		}
		return fields;
	}
	public static List<Class<?>> getAllSuper(Class<?> type){
		List<Class<?>> list = new ArrayList<Class<?>>();
		Class<?> superC = type;
		while(!superC.equals(Object.class)){
			list.add(superC);
			superC = superC.getSuperclass();
		}
		return list;
	}
	

	public static boolean isBaseType(Object o)
	{
		@SuppressWarnings("rawtypes")
		Class c = o.getClass();
		if( c.isPrimitive() )
			return true;
		if( c.isEnum() )
			return true;
		if( c == Integer.class || c == Boolean.class || c == Short.class || c == Byte.class || c == Long.class || c == Float.class || c == Double.class || c == String.class || c == Character.class )
			return true;
		
		if( c == Date.class )
			return true;
		
		if( o instanceof java.lang.Number )
			return true;
			
		return false;
	}
	
}