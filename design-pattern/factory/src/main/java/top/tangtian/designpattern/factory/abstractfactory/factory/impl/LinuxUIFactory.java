package top.tangtian.designpattern.factory.abstractfactory.factory.impl;

import top.tangtian.designpattern.factory.abstractfactory.factory.UIFactory;
import top.tangtian.designpattern.factory.abstractfactory.product.Button;
import top.tangtian.designpattern.factory.abstractfactory.product.CheckBox;
import top.tangtian.designpattern.factory.abstractfactory.product.TextBox;
import top.tangtian.designpattern.factory.abstractfactory.product.impl.linux.LinuxButton;
import top.tangtian.designpattern.factory.abstractfactory.product.impl.linux.LinuxCheckBox;
import top.tangtian.designpattern.factory.abstractfactory.product.impl.linux.LinuxTextBox;

/**
 * Linux UI工厂
 * 创建Linux风格的UI组件族
 */
public class LinuxUIFactory implements UIFactory {

    @Override
    public Button createButton() {
        System.out.println("Creating Linux Button...");
        return new LinuxButton();
    }

    @Override
    public TextBox createTextBox() {
        System.out.println("Creating Linux TextBox...");
        return new LinuxTextBox();
    }

    @Override
    public CheckBox createCheckBox() {
        System.out.println("Creating Linux CheckBox...");
        return new LinuxCheckBox();
    }

    @Override
    public String getThemeName() {
        return "GTK/GNOME Theme";
    }
}
