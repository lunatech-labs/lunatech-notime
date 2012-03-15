package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;

public class Transformers {

	/**
	 * Function to get an int, between square brackets, out of a String.
	 * 
	 * Is used to get the index out of a form's key field. When a List is
	 * submitted by a form, the keys need to be this format: foo[0]. This method
	 * will get the 0 out of foo[0]
	 */
	public static final Function<String, Integer> indexTransformer = new Function<String, Integer>() {

		@Override
		public Integer apply(String input) {
			Pattern numberBetweenSquareBrackets = Pattern
					.compile("\\[(\\d+)\\]");
			Matcher m = numberBetweenSquareBrackets.matcher(input);
			m.find(1);
			return Integer.parseInt(m.group(1));
		}

	};

}
