package top.tangtian.sharding.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author tangtian
 * @date 2025-10-20 09:12
 */
@Data
@TableName("t_user")
public class User {
		@TableId
		private Long id;
		private Long userId;
		private String name;
		private Integer age;
		private String email;
}
