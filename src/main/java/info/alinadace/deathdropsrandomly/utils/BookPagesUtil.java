package info.alinadace.deathdropsrandomly.utils;

/**
 * @author Kane
 * @date 2022/10/29 20:49
 */
public class BookPagesUtil {
	public static String newPage(String text){
		String replace = text.replace("\n", "\\n");
		return "{\"text\": \"" + replace + "\"}";
	}
}
