package top.tangtian.designpattern.builder.query;

/**
 * @program: springboot-tech-stack
 * @description: SQL查询对象
 * @author: tangtian
 * @create: 2026-02-09 16:57
 **/

import java.util.ArrayList;
import java.util.List;

/**
 * SQL查询对象
 * 不可变对象 - 所有字段都是final
 */
public class SqlQuery {
    private final String table;
    private final List<String> columns;
    private final List<String> whereClauses;
    private final List<String> joinClauses;
    private final String orderBy;
    private final String groupBy;
    private final Integer limit;
    private final Integer offset;

    // 私有构造函数 - 只能通过Builder创建
    private SqlQuery(Builder builder) {
        this.table = builder.table;
        this.columns = builder.columns;
        this.whereClauses = builder.whereClauses;
        this.joinClauses = builder.joinClauses;
        this.orderBy = builder.orderBy;
        this.groupBy = builder.groupBy;
        this.limit = builder.limit;
        this.offset = builder.offset;
    }

    /**
     * 生成SQL语句
     */
    public String toSql() {
        StringBuilder sql = new StringBuilder();

        // SELECT
        sql.append("SELECT ");
        if (columns.isEmpty()) {
            sql.append("*");
        } else {
            sql.append(String.join(", ", columns));
        }

        // FROM
        sql.append(" FROM ").append(table);

        // JOIN
        for (String join : joinClauses) {
            sql.append(" ").append(join);
        }

        // WHERE
        if (!whereClauses.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(String.join(" AND ", whereClauses));
        }

        // GROUP BY
        if (groupBy != null) {
            sql.append(" GROUP BY ").append(groupBy);
        }

        // ORDER BY
        if (orderBy != null) {
            sql.append(" ORDER BY ").append(orderBy);
        }

        // LIMIT
        if (limit != null) {
            sql.append(" LIMIT ").append(limit);
        }

        // OFFSET
        if (offset != null) {
            sql.append(" OFFSET ").append(offset);
        }

        return sql.toString();
    }

    // Getters
    public String getTable() { return table; }
    public List<String> getColumns() { return new ArrayList<>(columns); }
    public List<String> getWhereClauses() { return new ArrayList<>(whereClauses); }
    public List<String> getJoinClauses() { return new ArrayList<>(joinClauses); }
    public String getOrderBy() { return orderBy; }
    public String getGroupBy() { return groupBy; }
    public Integer getLimit() { return limit; }
    public Integer getOffset() { return offset; }

    @Override
    public String toString() {
        return toSql();
    }

    /**
     * 静态内部类 - 建造者
     */
    public static class Builder {
        // 必需参数
        private final String table;

        // 可选参数 - 初始化默认值
        private List<String> columns = new ArrayList<>();
        private List<String> whereClauses = new ArrayList<>();
        private List<String> joinClauses = new ArrayList<>();
        private String orderBy;
        private String groupBy;
        private Integer limit;
        private Integer offset;

        /**
         * 构造函数 - 必需参数
         */
        public Builder(String table) {
            if (table == null || table.trim().isEmpty()) {
                throw new IllegalArgumentException("Table name cannot be empty");
            }
            this.table = table;
        }

        /**
         * 选择列
         */
        public Builder select(String... columns) {
            for (String column : columns) {
                if (column != null && !column.trim().isEmpty()) {
                    this.columns.add(column);
                }
            }
            return this;
        }

        /**
         * WHERE条件
         */
        public Builder where(String condition) {
            if (condition != null && !condition.trim().isEmpty()) {
                this.whereClauses.add(condition);
            }
            return this;
        }

        /**
         * WHERE条件 - 带参数
         */
        public Builder where(String column, String operator, Object value) {
            if (value instanceof String) {
                whereClauses.add(column + " " + operator + " '" + value + "'");
            } else {
                whereClauses.add(column + " " + operator + " " + value);
            }
            return this;
        }

        /**
         * JOIN
         */
        public Builder join(String table, String condition) {
            this.joinClauses.add("JOIN " + table + " ON " + condition);
            return this;
        }

        /**
         * LEFT JOIN
         */
        public Builder leftJoin(String table, String condition) {
            this.joinClauses.add("LEFT JOIN " + table + " ON " + condition);
            return this;
        }

        /**
         * ORDER BY
         */
        public Builder orderBy(String column) {
            this.orderBy = column;
            return this;
        }

        /**
         * ORDER BY DESC
         */
        public Builder orderByDesc(String column) {
            this.orderBy = column + " DESC";
            return this;
        }

        /**
         * GROUP BY
         */
        public Builder groupBy(String column) {
            this.groupBy = column;
            return this;
        }

        /**
         * LIMIT
         */
        public Builder limit(int limit) {
            if (limit < 0) {
                throw new IllegalArgumentException("Limit must be non-negative");
            }
            this.limit = limit;
            return this;
        }

        /**
         * OFFSET
         */
        public Builder offset(int offset) {
            if (offset < 0) {
                throw new IllegalArgumentException("Offset must be non-negative");
            }
            this.offset = offset;
            return this;
        }

        /**
         * 分页
         */
        public Builder page(int pageNumber, int pageSize) {
            if (pageNumber < 1) {
                throw new IllegalArgumentException("Page number must be >= 1");
            }
            if (pageSize < 1) {
                throw new IllegalArgumentException("Page size must be >= 1");
            }
            this.limit = pageSize;
            this.offset = (pageNumber - 1) * pageSize;
            return this;
        }

        /**
         * 构建查询对象
         */
        public SqlQuery build() {
            return new SqlQuery(this);
        }
    }
}