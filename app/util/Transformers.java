package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;

public class Transformers {
	
	/*
	 * Returns the integer between square brackets in a String.
	 * Useful for forms with inputs with an index, as: foo[0], foo[1].
	 * Input: foo[0]. Output: 0
	 */
	public static final Function<String, Integer> indexTransformer = new Function<String, Integer>() {

		@Override
		public Integer apply(String input) {
			System.out.println(input);
			Pattern numberBetweenSquareBrackets = Pattern.compile("\\[(\\d+)\\]");
			Matcher m = numberBetweenSquareBrackets.matcher(input);
			m.find(1);
			return Integer.parseInt(m.group(1));
		}

	};

}
