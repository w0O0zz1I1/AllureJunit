package service;

import java.util.regex.Pattern;




public class Patterns {

    static public final Pattern reOpen = Pattern.compile("^\\$\\(\\\"open\\\"\\) (.*)");
    static public final Pattern reAssertThat = Pattern.compile("^\\$\\(\\\"(assertThat|assert)\\\"\\) (.*)");
    static private final String reLocator = "^\\$\\(\\\"(.*)\\\"\\) ";
    static public final Pattern reShouldHave = Pattern.compile(reLocator + "should have\\((.*)\\)");
    static public final Pattern reShouldBe = Pattern.compile(reLocator + "should be\\((.*)\\)");
    static public final Pattern reShould = Pattern.compile(reLocator + "should\\((.*)\\)");
    static public final Pattern reShouldNotHave = Pattern.compile(reLocator + "should not have\\((.*)\\)");
    static public final Pattern reShouldNotBe = Pattern.compile(reLocator + "should not be\\((.*)\\)");
    static public final Pattern reShouldNot = Pattern.compile(reLocator + "should not\\((.*)\\)");
    static public final Pattern reClick = Pattern.compile(reLocator + "click\\(\\)");
    static public final Pattern reSetValue = Pattern.compile(reLocator + "set value\\((.*)\\)");
    static public final Pattern reSendKeys = Pattern.compile(reLocator + "send keys\\((.*)\\)");
    static public final Pattern reScrollIntoView = Pattern.compile(reLocator + "scroll into view\\((.*)\\)");
    static public final Pattern reInfo = Pattern.compile("^\\$\\(\\\"\\[info\\] (.*)\\\"\\) (.*)");
    static public final Pattern reText = Pattern.compile(reLocator + "text\\((.*)\\)");
    static public final Pattern reCreate = Pattern.compile(reLocator + "create\\((.*)\\)");
    static public final Pattern reIs = Pattern.compile(reLocator + "is\\((.*)\\)");
    static public final Pattern reMatch = Pattern.compile(reLocator + ".*match.*");
}
