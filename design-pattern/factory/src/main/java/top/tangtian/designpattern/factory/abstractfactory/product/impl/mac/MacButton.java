package top.tangtian.designpattern.factory.abstractfactory.product.impl.mac;

import top.tangtian.designpattern.factory.abstractfactory.product.Button;

public class MacButton implements Button {

    @Override
    public void render() {
        System.out.println("  [Mac Button]");
        System.out.println("  ╭──────────────╮");
        System.out.println("  │   Click Me   │");
        System.out.println("  ╰──────────────╯");
        System.out.println("  Style: Rounded, Gradient, San Francisco font");
    }

    @Override
    public void onClick() {
        System.out.println("  Mac Button clicked with bounce effect");
    }

    @Override
    public String getStyle() {
        return "macOS Aqua Design";
    }
}
