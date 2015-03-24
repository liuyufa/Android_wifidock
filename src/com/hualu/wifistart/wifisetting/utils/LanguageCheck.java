package com.hualu.wifistart.wifisetting.utils;

import java.util.Locale;

public class LanguageCheck {
	public static boolean isZh() {
        String language = Locale.getDefault().getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }
}
