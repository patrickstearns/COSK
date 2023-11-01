package com.oblong.af.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class StringUtils {

    private StringUtils(){}

    public static String join(List<String> strings, String delimiter){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strings.size()-1; i++){
            sb.append(strings.get(i)).append(delimiter);
        }
        if (strings.size() > 0) sb.append(strings.get(strings.size()-1));
        return sb.toString();
    }

    public static List<String> split(String string, String delimiter){
        List<String> strings = new ArrayList<String>();
        for (StringTokenizer st = new StringTokenizer(string, delimiter, false); st.hasMoreTokens(); ){
            strings.add(st.nextToken());
        }
        return strings;
    }
}
