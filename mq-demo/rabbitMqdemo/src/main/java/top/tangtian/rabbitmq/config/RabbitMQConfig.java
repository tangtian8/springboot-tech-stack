package top.tangtian.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author tangtian
 * @date 2025-11-03 19:31
 */
@Configuration
public class RabbitMQConfig {

	public static final String EXCHANGE_NAME = "direct.exchange.esb";
	public static final String QUEUE_VISIT = "queue.visit";
	public static final String QUEUE_DIAGNOSIS = "queue.diagnosis";
	public static final String QUEUE_OUT_ORDER = "queue.out.order";
	public static final String QUEUE_IN_ORDER = "queue.in.order";
	public static final String ROUTING_VISIT = "visit";
	public static final String ROUTING_DIAGNOSIS = "diagnosis";
	public static final String ROUTING_OUT_ORDER = "out.order";
	public static final String ROUTING_IN_ORDER = "in.order";

	// 2. 声明 Direct 交换机
	@Bean
	public DirectExchange directExchange() {
		return new DirectExchange(EXCHANGE_NAME, true, false);
	}
	// 1. 声明队列
	@Bean
	public Queue visitQueue() {
	return new Queue(QUEUE_VISIT, false);
	}

	@Bean
	public Queue diagnosisQueue() {
		return new Queue(QUEUE_DIAGNOSIS, false);
	}

	// 3. 绑定队列到交换机,指定 routing key
	@Bean
	public Binding bindingVisit(Queue visitQueue, DirectExchange directExchange) {
		return BindingBuilder.bind(visitQueue).to(directExchange).with(ROUTING_VISIT);
	}

	@Bean
	public Binding bindingDiagnosis(Queue diagnosisQueue, DirectExchange directExchange) {
		return BindingBuilder.bind(diagnosisQueue).to(directExchange).with(ROUTING_DIAGNOSIS);
	}




	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
		jackson2JsonMessageConverter.setCreateMessageIds(true);
		rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
		rabbitTemplate.setExchange(EXCHANGE_NAME);
		return rabbitTemplate;
	}


	public static final String Publish = "exchange.fanout";

	//发布订阅
	@Bean
	public FanoutExchange fanoutExchange() {
		return new FanoutExchange(Publish);
	}

	@Bean
	public Queue fanoutQueue1() {
		return new Queue(QUEUE_IN_ORDER,false);
	}

	@Bean
	public Queue fanoutQueue2() {
		return new Queue(QUEUE_OUT_ORDER,false);
	}

	@Bean
	public Binding binding1(FanoutExchange fanoutExchange, Queue fanoutQueue1) {
		return BindingBuilder.bind(fanoutQueue1).to(fanoutExchange);
	}

	@Bean
	public Binding binding2(FanoutExchange fanoutExchange, Queue fanoutQueue2) {
		return BindingBuilder.bind(fanoutQueue2).to(fanoutExchange);
	}


}
