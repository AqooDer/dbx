# dbx

#### 介绍

一个用来迁移数据库数据的工具类库。

+ 支持不同数据库之间的数据迁移 , [数据库支持表](./docs/数据库支持表.md)。
+ [支持表数据结构变化差异大的迁移](./docs/测试模型.md) , 支持表名修改，字段新增，修改，删除，以及表拆分合并等各种复杂的表数据迁移。

#### 软件架构

使用springJDBC,lombok,guava等技术。

#### 安装教程

```maven
    <dependencies>
        <dependency>
            <groupId>com.AqooDer</groupId>
            <artifactId>dbx-bean</artifactId>
            <version>${dbx.version}</version>
        </dependency>
        
        <!--导入你依赖的数据库驱动-->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.5.0</version>
        </dependency>

        <!--8的驱动-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.28</version>
        </dependency>
    </dependencies>
```

#### 使用说明

使用教程可以参考 `dbx-test`中的demo示列

1. 导入依赖包`dbx-bean`和驱动包即可。
2. 创建你的数据库表数据迁移的描述模型。
3. 启动执行。
