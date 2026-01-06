核心要点总结
1. Lambda 表达式语法规则

可推断类型时,参数类型可省略
单个参数时括号可省略: x -> x * 2
单条语句时花括号和 return 可省略
多条语句需要花括号和明确的 return

常用函数式接口
接口方法签名用途Function<T, R>R apply(T t)转换数据
Predicate<T>boolean test(T t)判断条件
Consumer<T>void accept(T t)消费数据
Supplier<T>T get()提供数据
BiFunction<T,U,R>R apply(T t, U u)两参数转换