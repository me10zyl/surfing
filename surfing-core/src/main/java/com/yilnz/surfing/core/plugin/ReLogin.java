package com.yilnz.surfing.core.plugin;

import com.yilnz.surfing.core.basic.Page;

public interface ReLogin {
	CookieProvider getCookie(Page page);
	boolean isLoginSuccess(Page page);
	String getCookieKey();
}
