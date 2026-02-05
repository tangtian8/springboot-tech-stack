package top.tangtian.designpattern.factory.abstractfactory.product;

/**
 * 抽象产品 - 按钮
 */
public interface Button {
    void render();
    void onClick();
    String getStyle();
}