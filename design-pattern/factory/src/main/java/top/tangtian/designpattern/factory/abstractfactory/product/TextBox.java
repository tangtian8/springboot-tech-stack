package top.tangtian.designpattern.factory.abstractfactory.product;

/**
 * 抽象产品 - 文本框
 */
public interface TextBox {
    void render();
    void setText(String text);
    String getText();
    String getStyle();
}