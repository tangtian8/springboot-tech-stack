package top.tangtian.designpattern.chain.approval;

import top.tangtian.designpattern.chain.approval.chain.ApprovalChain;
import top.tangtian.designpattern.chain.approval.model.ApprovalResult;
import top.tangtian.designpattern.chain.approval.model.Order;
import top.tangtian.designpattern.chain.approval.model.User;

import java.util.Arrays;

/**
 *订单审批流程的责任链模式练习，这是责任链模式在业务场景中的经典应用。
 * 业务场景
 * 一个电商平台的订单需要经过多个审批环节：
 *
 * 订单验证 - 检查订单信息完整性
 * 库存检查 - 验证商品库存是否充足
 * 价格审核 - 根据订单金额决定审批级别
 * 信用审核 - 检查用户信用额度
 * 风控审核 - 风险评估（高额订单）
 * 最终批准 - 订单确认
 */
public class ApprovalDemo {  public static void main(String[] args) {
    ApprovalChain chain = new ApprovalChain();

    // 测试1: 正常订单 - 应该全部通过
    testNormalOrder(chain);

    // 测试2: 缺货订单 - 应该在库存检查失败
    testOutOfStockOrder(chain);

    // 测试3: 超额订单 - 应该在价格审核失败
    testOverLimitOrder(chain);

    // 测试4: 低信用分订单 - 应该在信用检查失败
    testLowCreditOrder(chain);

    // 测试5: 高风险订单 - 应该在风控失败
    testHighRiskOrder(chain);
}

    private static void testNormalOrder(ApprovalChain chain) {
        System.out.println("\n\n### TEST 1: Normal Order ###");

        User user = new User("U001", "Alice", 750, 10000.0, 20);
        Order order = new Order(
                "ORD001",
                user,
                800.0,
                3,
                Arrays.asList("PROD001", "PROD002", "PROD003")
        );

        ApprovalResult result = chain.process(order);
        chain.printApprovalLogs(order);
    }

    private static void testOutOfStockOrder(ApprovalChain chain) {
        System.out.println("\n\n### TEST 2: Out of Stock Order ###");

        User user = new User("U002", "Bob", 800, 15000.0, 30);
        Order order = new Order(
                "ORD002",
                user,
                500.0,
                2,
                Arrays.asList("PROD001", "PROD004")  // PROD004 缺货
        );

        ApprovalResult result = chain.process(order);
        chain.printApprovalLogs(order);
    }

    private static void testOverLimitOrder(ApprovalChain chain) {
        System.out.println("\n\n### TEST 3: Over Limit Order ###");

        User user = new User("U003", "Charlie", 850, 20000.0, 50);
        Order order = new Order(
                "ORD003",
                user,
                60000.0,  // 超过最大金额限制
                10,
                Arrays.asList("PROD001", "PROD002", "PROD003", "PROD005")
        );

        ApprovalResult result = chain.process(order);
        chain.printApprovalLogs(order);
    }

    private static void testLowCreditOrder(ApprovalChain chain) {
        System.out.println("\n\n### TEST 4: Low Credit Order ###");

        User user = new User("U004", "David", 550, 5000.0, 15);  // 信用分低
        Order order = new Order(
                "ORD004",
                user,
                1200.0,
                4,
                Arrays.asList("PROD001", "PROD002")
        );

        ApprovalResult result = chain.process(order);
        chain.printApprovalLogs(order);
    }

    private static void testHighRiskOrder(ApprovalChain chain) {
        System.out.println("\n\n### TEST 5: High Risk Order ###");

        // 新用户 + 大额订单 = 高风险
        User user = new User("U005", "Eve", 650, 15000.0, 2);  // 只有2个历史订单
        Order order = new Order(
                "ORD005",
                user,
                12000.0,  // 大额订单
                8,
                Arrays.asList("PROD001", "PROD002", "PROD003", "PROD005")
        );

        ApprovalResult result = chain.process(order);
        chain.printApprovalLogs(order);
    }
}
