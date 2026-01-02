package top.tangtian.rocketmqdemo.producer.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import top.tangtian.rocketmqdemo.producer.entity.OrderMessage;
import top.tangtian.rocketmqdemo.producer.service.MessageProducerService;

/**
 * @author tangtian
 * @date 2026-01-02 17:06
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {

	@Resource
	private MessageProducerService producerService;

	@PostMapping("/sync")
	public String sendSync(@RequestBody OrderMessage message) {
		producerService.sendSyncMessage(message);
		return "同步发送成功";
	}

	@PostMapping("/async")
	public String sendAsync(@RequestBody OrderMessage message) {
		producerService.sendAsyncMessage(message);
		return "异步发送成功";
	}

	@PostMapping("/oneway")
	public String sendOneWay(@RequestBody OrderMessage message) {
		producerService.sendOneWayMessage(message);
		return "单向发送成功";
	}

	@PostMapping("/tag/{tag}")
	public String sendWithTag(@PathVariable String tag, @RequestBody OrderMessage message) {
		producerService.sendMessageWithTag(message, tag);
		return "发送成功: " + tag;
	}
}
