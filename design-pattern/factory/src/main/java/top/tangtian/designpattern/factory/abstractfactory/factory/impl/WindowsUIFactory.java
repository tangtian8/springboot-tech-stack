package top.tangtian.designpattern.factory.abstractfactory.factory.impl;

import top.tangtian.designpattern.factory.abstractfactory.factory.UIFactory;
import top.tangtian.designpattern.factory.abstractfactory.product.Button;
import top.tangtian.designpattern.factory.abstractfactory.product.CheckBox;
import top.tangtian.designpattern.factory.abstractfactory.product.TextBox;
import top.tangtian.designpattern.factory.abstractfactory.product.impl.windows.WindowsButton;
import top.tangtian.designpattern.factory.abstractfactory.product.impl.windows.WindowsCheckBox;
import top.tangtian.designpattern.factory.abstractfactory.product.impl.windows.WindowsTextBox;

/**
 * Windows UI工厂
 * 创建Windows风格的UI组件族
 */
public class WindowsUIFactory implements UIFactory {

    @Override
    public Button createButton() {
        System.out.println("Creating Windows Button...");
        return new WindowsButton();
    }

    @Override
    public TextBox createTextBox() {
        System.out.println("Creating Windows TextBox...");
        return new WindowsTextBox();
    }

    @Override
    public CheckBox createCheckBox() {
        System.out.println("Creating Windows CheckBox...");
        return new WindowsCheckBox();
    }

    @Override
    public String getThemeName() {
        return "Windows 11 Fluent Design";
    }
}
