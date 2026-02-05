package top.tangtian.designpattern.factory.abstractfactory.factory.impl;

import top.tangtian.designpattern.factory.abstractfactory.factory.UIFactory;
import top.tangtian.designpattern.factory.abstractfactory.product.Button;
import top.tangtian.designpattern.factory.abstractfactory.product.CheckBox;
import top.tangtian.designpattern.factory.abstractfactory.product.TextBox;
import top.tangtian.designpattern.factory.abstractfactory.product.impl.mac.MacButton;
import top.tangtian.designpattern.factory.abstractfactory.product.impl.mac.MacCheckBox;
import top.tangtian.designpattern.factory.abstractfactory.product.impl.mac.MacTextBox;

/** Mac UI工厂
 * 创建Mac风格的UI组件族
 */
public class MacUIFactory implements UIFactory {

    @Override
    public Button createButton() {
        System.out.println("Creating Mac Button...");
        return new MacButton();
    }

    @Override
    public TextBox createTextBox() {
        System.out.println("Creating Mac TextBox...");
        return new MacTextBox();
    }

    @Override
    public CheckBox createCheckBox() {
        System.out.println("Creating Mac CheckBox...");
        return new MacCheckBox();
    }

    @Override
    public String getThemeName() {
        return "macOS Aqua Design";
    }
}
