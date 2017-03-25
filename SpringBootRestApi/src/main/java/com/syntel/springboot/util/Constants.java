package com.syntel.springboot.util;

import java.util.regex.Pattern;

public class Constants {
	public static final String Regx1 = "(.*?)(<<)\\s*(.*?)\\s*(>>)";
	public static final Pattern pattern = Pattern.compile(Regx1, Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL);
	public static final String rootPath="E:\\FileRepo";
	public static final String tempRootPath="E:\\FileRepo\\tempx";
	public static final String seperator = "\\";

}
