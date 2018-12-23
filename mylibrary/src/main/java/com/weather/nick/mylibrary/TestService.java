package com.weather.nick.mylibrary;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * TODO feature
 *
 * @author Alex <a href="mailto:zhilong.lzl@alibaba-inc.com">Contact me.</a>
 * @version 1.0
 * @since 2017/1/3 10:26
 */
public interface TestService extends IProvider {
    void sayHello(String name);
}