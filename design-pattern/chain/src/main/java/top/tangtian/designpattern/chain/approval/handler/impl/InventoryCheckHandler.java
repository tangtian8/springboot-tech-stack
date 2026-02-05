package top.tangtian.designpattern.chain.approval.handler.impl;

import top.tangtian.designpattern.chain.approval.handler.AbstractApprovalHandler;
import top.tangtian.designpattern.chain.approval.model.ApprovalResult;
import top.tangtian.designpattern.chain.approval.model.Order;
import top.tangtian.designpattern.chain.approval.model.OrderStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 库存检查
 */
public class InventoryCheckHandler extends AbstractApprovalHandler {
    private Map<String, Integer> inventory;  // 模拟库存数据库

    public InventoryCheckHandler() {
        super("Inventory Check");
        initInventory();
    }

    private void initInventory() {
        inventory = new HashMap<>();
        inventory.put("PROD001", 100);
        inventory.put("PROD002", 50);
        inventory.put("PROD003", 200);
        inventory.put("PROD004", 0);   // 缺货商品
        inventory.put("PROD005", 10);
    }

    @Override
    protected ApprovalResult doApprove(Order order) {
        order.setStatus(OrderStatus.INVENTORY_CHECK);

        for (String productId : order.getProductIds()) {
            Integer stock = inventory.get(productId);

            if (stock == null) {
                return ApprovalResult.reject(
                        "Product not found: " + productId, handlerName);
            }

            if (stock <= 0) {
                return ApprovalResult.reject(
                        "Product out of stock: " + productId, handlerName);
            }

            System.out.println("  - " + productId + ": Stock available (" + stock + " units)");
        }

        return ApprovalResult.approve(
                "Inventory check passed for all products", handlerName);
    }
}