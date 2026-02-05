``````
场景背景
在微服务架构中，我们需要调用各种第三方API：

支付接口（支付宝、微信）
短信服务（阿里云、腾讯云）
物流接口（顺丰、圆通）
地图服务（高德、百度）

每个服务的HTTP请求配置不同：

超时时间不同
重试策略不同
认证方式不同
SSL证书要求不同


项目结构
src/main/java/com/example/httpclient/
├── client/
│   ├── HttpClient.java              # HTTP客户端接口
│   └── impl/
│       ├── StandardHttpClient.java  # 标准客户端
│       ├── FastHttpClient.java      # 快速客户端
│       ├── SecureHttpClient.java    # 安全客户端
│       └── RetryableHttpClient.java # 重试客户端
├── factory/
│   └── HttpClientFactory.java       # 简单工厂
├── config/
│   └── HttpClientConfig.java        # 配置类
└── HttpClientDemo.java              # 测试
``````