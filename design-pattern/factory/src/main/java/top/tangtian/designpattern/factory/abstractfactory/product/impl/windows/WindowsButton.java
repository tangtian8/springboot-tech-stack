package top.tangtian.designpattern.factory.abstractfactory.product.impl.windows;

import top.tangtian.designpattern.factory.abstractfactory.product.Button;

public class WindowsButton implements Button {

    @Override
    public void render() {
        System.out.println("  [Windows Button]");
        System.out.println("  ┌──────────────┐");
        System.out.println("  │   Click Me   │");
        System.out.println("  └──────────────┘");
        System.out.println("  Style: Flat, Blue border, Segoe UI font");
    }

    @Override
    public void onClick() {
        System.out.println("  Windows Button clicked with smooth animation");
    }

    @Override
    public String getStyle() {
        return "Windows 11 Fluent Design";
    }
}
