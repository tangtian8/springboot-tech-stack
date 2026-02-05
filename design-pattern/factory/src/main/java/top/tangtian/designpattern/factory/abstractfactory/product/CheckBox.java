package top.tangtian.designpattern.factory.abstractfactory.product;

/**
 * 抽象产品 - 复选框
 */
public interface CheckBox {
    void render();
    void setChecked(boolean checked);
    boolean isChecked();
    String getStyle();
}