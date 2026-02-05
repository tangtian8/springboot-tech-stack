package top.tangtian.designpattern.factory.abstractfactory.product.impl.linux;

import top.tangtian.designpattern.factory.abstractfactory.product.Button;

public class LinuxButton implements Button {

    @Override
    public void render() {
        System.out.println("  [Linux Button]");
        System.out.println("  +-------------+");
        System.out.println("  |  Click Me   |");
        System.out.println("  +-------------+");
        System.out.println("  Style: Sharp corners, Flat, Ubuntu font");
    }

    @Override
    public void onClick() {
        System.out.println("  Linux Button clicked (no animation)");
    }

    @Override
    public String getStyle() {
        return "GTK/GNOME Theme";
    }
}