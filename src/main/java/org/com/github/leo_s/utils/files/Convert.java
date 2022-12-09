package org.com.github.leo_s.utils.files;

import java.util.List;

import static org.com.github.leo_s.Christmas.config;

@SuppressWarnings("ConstantConditions")
public class Convert {
    public static String convert(String string){
        if(string.contains("%prefix%")){
            string = string.replace("%prefix%", config.getString("prefix"));
        }
        string = string.replace("&", "§");
        return string;
    }
    public static List<String> convert(List<String> list){
        list.replaceAll(Convert::convert);
        return list;
    }
}
