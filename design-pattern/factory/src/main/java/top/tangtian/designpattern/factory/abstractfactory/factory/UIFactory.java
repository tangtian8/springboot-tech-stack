package top.tangtian.designpattern.factory.abstractfactory.factory;

import top.tangtian.designpattern.factory.abstractfactory.product.Button;
import top.tangtian.designpattern.factory.abstractfactory.product.CheckBox;
import top.tangtian.designpattern.factory.abstractfactory.product.TextBox;

public interface UIFactory {
    /**
     * 创建按钮
     */
    Button createButton();

    /**
     * 创建文本框
     */
    TextBox createTextBox();

    /**
     * 创建复选框
     */
    CheckBox createCheckBox();

    /**
     * 获取主题名称
     */
    String getThemeName();
}
