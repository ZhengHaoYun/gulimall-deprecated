# 谷粒商城—基础篇

## 一、项目简介

### 1、电商模式

市面上有5种常见的电商模式B2B、B2C、C2B、C2C、O2O

* B2B：指商家与商家之间建立的商业关系，如阿里巴巴。
* B2C：指供应商直接将商品售卖给客户，即“商对客”模式，也就是通常说的商业零售，直接将面向消费者销售产品和服务，如苏宁易购、京东。
* C2B：指消费者对企业，先有消费者需求产生，而后有企业生产，即先有消费者提出需求，后有生产企业根据需求组织生产。
* C2C：客户之间自己把东西放到网上去卖，如闲鱼。
* O2O：指将线下商务的机会与互联网结合在一起，让互联网成为了线下交易的前台。线上快速支付，线下优质服务，如饿了么，京东到家。

### 2、项目技术&特色

* 前后端分离开发、基于vue的后台管理系统
* SpringCloud的全新解决方案
* 应用监控、限流、网关、熔断降级等分布式方案，全方位涉及
* 分布式事务、分布式锁
* 高并发场景的编码方式、线程池、异步编排等的使用
* 压力测试与性能优化
* 各种集群技术的应用及区别
* CI/CD的使用
* ...

### 3、项目前置要求

* SpringCloud
* SpringBoot
* Git、Maven
* Linux、Redis、Docker
* Html、Css、Js、Vue
* 熟悉使用IDEA开发项目

## 二、分布式基础概念

### 1、微服务

微服务架构风格，就像是把一个单独的应用程序开发为一套小服务，每个小服务运行在自己的进程中，并使用轻量级机制通信，通常是HTTP API。这些服务围绕业务能力来构建，并通过完全自动化部署机制来独立部署。这些服务使用不同的编程语言书写，以及不同数据存储技术，并保持最低限度的集中式管理。

简而言之：拒绝大型单体应用，基于业务边界进行服务微化拆分，各个服务独立部署运行。

### 2、集群&分布式&节点

**集群**是个物理形态，分布式是个工作方式。

只要是一堆机器，就可以叫集群，他们是不是一起协作着干活，这个谁也不知道;

《分布式系统原理与范型》定义:

“分布式系统是若干独立计算机的集合，这些计算机对于用户来说就像单个相关系统”

**分布式系统**（distributed system）是建立在网络之上的软件系统。

分布式是指将不同的业务分布在不同的地方。

集群指的是将几台服务器集中在一起，实现同一业务。

例如，京东是一个分布式系统，众多业务运行在不同的机器，所有业务构成一个大型的业务集群。每一个小的业务，比如用户系统，访问压力大的时候一台服务器是不够的。我们就应该将用户系统部署到多个服务器，也就是每一个业务系统也可以做集群化;

分布式中的每一个节点，都可以做集群。而集群并不一定就是分布式的。

### 3、远程调用

在分布式系统中，各个服务之间可能处于不同的主机，但是不同服务之间不可避免的需要相互调用，我们称之为远程调用。

SpringCloud中使用HTTP+JSON的方式完成远程调用。

<img src="https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20210106185526.png" alt="image-20210106185451915" style="zoom:67%;" />

### 4、负载均衡

分布式系统中，A服务需要调用B服务，B服务在多台机器中都存在，A调用任意一个服务器均可完成功能。

为了使每一个服务器都不要太忙或者太闲，我们可以负载均衡的调用每一个服务器，提升网站的健壮性。

常见的负载均衡算法:

* 轮询：为第一个请求选择健康池中的第一个后端服务器，然后按顺序往后依次选择，直到最后一个，然后循环。
* 最小连接：优先选择连接数最少，也就是压力最小的后端服务器，在会话较长的情况下可以考虑采取这种方式。

### 5、服务注册/发现&注册中心

A服务调用B服务，A服务并不知道B服务当前在哪几台服务器有，哪些是正常的，哪些服务已经下线。解决这个问题可以引入注册中心。

配置中心用来几种管理微服务的配置信息。

### 6、服务熔断&服务降级

在微服务架构中，微服务之间通过网络进行通信，存在相互依赖，当其中一个服务不可用时，有可能会造成雪崩效应。要防止这样的情况，必须要有容错机制来保护服务。

**情景：**

订单服务 --> 商品服务 --> 库存服务

库存服务出现故障导致响应慢，导致商品服务需要等待，可能等到10s后库存服务才能响应。库存服务的不可用导致商品服务阻塞，商品服务等的期间，订单服务也处于阻塞。一个服务不可用导致整个服务链都阻塞。如果是高并发，第一个请求调用后阻塞10s得不到结果，第二个请求直接阻塞10s。更多的请求进来导致请求积压，全部阻塞，最终服务器的资源耗尽。导致雪崩。

**解决方案：**

1. 服务熔断

指定超时时间，库存服务3s没有响应就超时，如果经常失败，比如10s内100个请求都失败了。开启断路保护机制，下一次请求进来不调用库存服务了，因为上一次100%错误都出现了，我们直接在此中断，商品服务直接返回，返回一些默认数据或者null，而不调用库存服务了，这样就不会导致请求积压。

- 设置服务的超时时间，当被调用的服务经常失败到达某个阈值，我们可以开启断路保护机制，后来的请求不再去调用这个服务。本地直接返回默认的数据。

2. 服务降级

在运维期间，当系统处于高峰期，系统资源紧张，我们可以让非核心业务降级运行。

降级：某些服务不处理，或者简单的进行处理（抛异常、返回NULL、调用Mock数据、调用Fallback处理逻辑）

### 7、API网关

客户端发送请求到服务器路途中，设置一个网关，请求都先到达网关，网关对请求进行统一认证（合法非法）和处理等操作。他是安检。

在微服务架构中，API gateway作为整体架构的重要组件，它抽象了微服务中都需要的公共功能，同时提供了客户端负载均衡，服务自动熔断，灰度发布，统一认证，限流流控，日志统计等丰富的功能，帮助我们解决很多API管理难题。

### 8、谷粒商城微服务架构图

![img](https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20210106190609.png)

**架构描述：**

内网部署的是后端集群，前端在页面上操作发送请求到后端，在这途中会经过Nginx集群，Nginx把请求转交给API网关(SpringCloud Gateway)（网关可以根据当前请求动态地路由到指定的服务，看当前请求是想调用商品服务还是购物车服务还是检索服务），从路由过来如果请求很多，可以负载均衡地调用商品服务器中的一台（商品服务复制了多份），当商品服务器出现问题也可以在网关层面对服务进行熔断或降级（使用阿里的Sentinel组件），网关还有其他的功能如认证授权、限流（只放行部分到服务器）等。

到达服务器后进行处理（SpringBoot为微服务），服务与服务可能会相互调用（使用Feign组件），有些请求可能经过登录才能进行（基于OAuth2.0的认证中心。安全和权限使用SpringSecurity控制）

服务可能保存了一些数据或者需要使用缓存，我们使用Redis集群（分片+哨兵集群）。持久化使用MySQL，读写分离和分库分表。

服务和服务之间会使用消息队列（RabbitMQ），来完成异步解耦，分布式事务的一致性。有些服务可能需要全文检索，检索商品信息，使用ElasticSearch。

服务可能需要存取数据，使用阿里云的对象存储服务OSS。

项目上线后为了快速定位问题，使用ELK对日志进行处理，使用LogStash收集业务里的各种日志，把日志存储到ES中，用Kibana可视化页面从ES中检索出相关信息，帮助我们快速定位问题所在。

在分布式系统中，由于我们每个服务都可能部署在很多台机器，服务和服务可能相互调用，就得知道彼此都在哪里，所以需要将所有服务都注册到注册中心。服务从注册中心发现其他服务所在位置（使用阿里Nacos作为注册中心）。

每个服务的配置众多，为了实现改一处配置相同配置就同步更改，就需要配置中心，也使用阿里的Nacos，服务从配置中心中动态取配置。

服务追踪，追踪服务调用链哪里出现问题，使用SpringCloud提供的Sleuth、Zipkin、Metrics，把每个服务的信息交给开源的Prometheus进行聚合分析，再由Grafana进行可视化展示，提供Prometheus提供的AlterManager实时得到服务的告警信息，以短信/邮件的方式告知服务开发人员。

还提供了持续集成和持续部署。项目发布起来后，因为微服务众多，每一个都打包部署到服务器太麻烦，有了持续集成后开发人员可以将修改后的代码提交到Github，运维人员可以通过自动化工具Jenkins Pipeline将Github中获取的代码打包成Docker镜像，最终是由k8s集成Docker服务，将服务以Docker容器的方式运行。

![img](https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20210106191325.png)

反映了需要创建的微服务以及相关技术。

前后分离开发。前端项目分为admin-vue（工作人员使用的后台管理系统）、shop-vue（面向公众访问的web网站）、app（公众）、小程序（公众）

- 商品服务：商品的增删改查、商品的上下架、商品详情
- 支付服务
- 优惠服务
- 用户服务：用户的个人中心、收货地址
- 仓储服务：商品的库存
- 秒杀服务：
- 订单服务：订单增删改查
- 检索服务：商品的检索ES
- 中央认证服务：登录、注册、单点登录、社交登录
- 购物车服务：
- 后台管理系统：添加优惠信息等

## 三、环境搭建

### 1、安装Linux虚拟机

购买阿里云ECS服务器

### 2、安装Docker

[在CentOS上安装Docker](https://docs.docker.com/engine/install/centos/)

```shell
# 1.卸载旧版本
$ yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine
# 2.需要的安装包
$ yum install -y yum-utils

# 3.设置镜像仓库
$ yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo		# 默认是国外的!很慢!
    
$ yum-config-manager \
    --add-repo \
    http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo # 阿里云镜像 快

# 更新yum软件包索引
$ yum makecache fast

# 4.安装docker	docker-ce 社区版 ee 企业版
$ yum install docker-ce docker-ce-cli containerd.io

# 5.启动docker
$ systemctl start docker

# 6.使用docker version查看是否安装成功
$ docker version

# 7.使用hello owrld测试
$ docker run hello-world

# 判断images是否下载成功，可以看到hello-world镜像。
$ docker images
```

配置阿里云镜像加速器

针对Docker客户端版本大于 1.10.0 的用户

您可以通过修改daemon配置文件/etc/docker/daemon.json来使用加速器

```shell
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://owqdaahd.mirror.aliyuncs.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```

Docker自启动命令

```shell
systemctl enable docker
```

### 3、Docker安装MySQL

（1）下载MySQL镜像

```shell
[root@zhenghaoyun ~]# docker pull mysql:5.7
```

（2）启动MySQL容器

```shell
[root@zhenghaoyun ~]# docker run -p 3306:3306 --name mysql \
 -v /mydata/mysql/log:/var/log/mysql \
 -v /mydata/mysql/data:/var/lib/mysql \
 -v /mydata/mysql/conf:/etc/mysql \
 -e MYSQL_ROOT_PASSWORD=root \
 -d mysql:5.7
```

<img src="https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20210106194351.png" alt="image-20210106194351514" style="zoom:67%;" />

补充：-d表示使用哪个镜像启动的这个容器

（3）修改MySQL配置文件

```shell
[root@zhenghaoyun ~]# vi /mydata/mysql/conf/my.cnf

# my.cnf文件内容
[client]
default-character-set=utf8

[mysql]
default-character-set=utf8
[mysqld]
init_connect='SET collation_connection = utf8_unicode_ci'
init_connect='SET NAMES utf8'
character-set-server=utf8
collation-server=utf8_unicode_ci
skip-character-set-client-handshake
skip-name-resolve

# wq保存即可
```

（4）重启MySQL

```shell
[root@zhenghaoyun ~]# docker restart mysql
```

### 4、Docker安装Redis

（1）下载镜像文件

```shell
[root@zhenghaoyun ~]# docker pull redis
Using default tag: latest
latest: Pulling from library/redis
8559a31e96f4: Already exists 
85a6a5c53ff0: Pull complete 
b69876b7abed: Pull complete 
a72d84b9df6a: Pull complete 
5ce7b314b19c: Pull complete 
04c4bfb0b023: Pull complete 
Digest: sha256:800f2587bf3376cb01e6307afe599ddce9439deafbd4fb8562829da96085c9c5
Status: Downloaded newer image for redis:latest
docker.io/library/redis:latest

```

（2）创建实例并启动

```shell
# 自行创建好这个文件
mkdir -p /mydata/redis/conf
touch /mydata/redis/conf/redis.conf

# 启动
# 如果不创建上文所提到的文件，直接运行该命令的话，因为/etc/redis/redis.conf在容器中实际上是不存在的，所以通常会将下面的命令中的/conf/redis.conf当成一个文件夹（实际上是一个文件），所以我们需要先如上命令创建好文件
[root@zhenghaoyun conf]# docker run -p 6379:6379 --name redis -v /mydata/redis/data:/data \
 -v /mydata/redis/conf/redis.conf:/etc/redis/redis.conf \
 -d redis redis-server /etc/redis/redis.conf


# 查看容器是否成功启动
[root@zhenghaoyun conf]# docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                               NAMES
2eb968c5f42f        redis               "docker-entrypoint.s…"   2 seconds ago       Up 1 second         0.0.0.0:6379->6379/tcp              redis
17dc1df5bbca        mysql:5.7           "docker-entrypoint.s…"   24 minutes ago      Up 9 minutes        0.0.0.0:3306->3306/tcp, 33060/tcp   mysql

```

（3）使用redis镜像执行redis-cli命令连接

```shell
[root@zhenghaoyun conf]# docker exec -it redis redis-cli
127.0.0.1:6379> set a b
OK
127.0.0.1:6379> get a
"b"
127.0.0.1:6379> exit
```

（4）修改redis配置文件，启动AOF持久化，更多的配置项可以去访问redis官网

```shell
[root@zhenghaoyun conf]# vi /mydata/redis/conf/redis.conf

# redis.conf内容
appendonly yes

# 重启redis
[root@zhenghaoyun conf]# docker restart redis
```

（5）安装Redis可视化工具RedisDesktopManager

破解版链接：https://www.52pojie.cn/thread-1042770-1-1.html

### 5、开发环境统一

（1）Maven配置阿里云镜像与Java8编译环境

（2）IDEA安装Lombok插件、MyBatisX插件

（3）安装与配置Git

（4）使用VScode开发后台管理系统的前端

### 6、创建项目微服务

（1）从gitee初始化一个项目

<img src="https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20200720231038.png" alt="image-20200720231038418" style="zoom: 80%;" />

（2）打开idea克隆项目

<img src="https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20200720231858.png" alt="image-20200720231858300" style="zoom: 67%;" />

（3）采用Spring Initializr方式创建模块

```
Moudle名：
1. gulimall-product
2. gulimall-coupon
3. gulimall-order
4. gulimall-ware
5. gulimall-member

共同点：
1. 包名统一为com.atguigu.gulimall.xxx
2. 模块名统一为gulimall-xxx
3. 引入spring web和openFeign依赖
```

![img](https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20210110133220.png)

![1589463810761](https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20210110133220.png)

（4）在根目录gulimall下创建pom.xml文件，使其成为父模块

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.atguigu.gulimall</groupId>
    <artifactId>gulimall</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>gulimall</name>
    <packaging>pom</packaging>
    <modules>
        <module>gulimall-product</module>
        <module>gulimall-coupon</module>
        <module>gulimall-order</module>
        <module>gulimall-ware</module>
        <module>gulimall-member</module>
    </modules>

</project>

```

在maven窗口刷新，并点击+号，找到刚才的pom.xml添加进来，发现多了个root。这样的话运行root的clean命令，其他项目也一起clean了。

（5）修改根目录下的.gitignore文件

```
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

**/mvnw
**/mvnw.cmd
**/.mvn
**/target/
.idea
**/.gitignore
```

在Git面板中点击刷新，最后只剩下21个Unversioned Files，右键将其全部纳入版本控制。

![image-20210110134953098](https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20210110134954.png)

（6）安装Gitee插件

https://plugins.jetbrains.com/plugin/11491-gitee

![image-20210110135847571](https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20210110135847.png)

再重启IDEA

（6）提交

![image-20210110140306486](https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20210110140306.png)

<img src="https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20210110140510.png" alt="image-20210110140509467" style="zoom:67%;" />

最后commit & push。

### 7、数据库设计

（1）创建数据库，字符集选utf8mb4，他能兼容utf8且能解决一些乱码的问题。分别建立了下面数据库。

![image-20200721095010519](https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20200721100720.png)

（2）运行SQL文件，如果报错，就采用复制命令的方式而不是直接运行SQL文件的方式

![image-20200721095114943](https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20200721100722.png)

<img src="https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20210110141659.png" alt="image-20210110141658790" style="zoom:67%;" />



### 8、人人开源框架搭建

（1）gitee下载[renren-fast](https://gitee.com/renrenio/renren-fast)与[renren-fast-vue](https://gitee.com/renrenio/renren-fast-vue)两个项目后删除其中的.git文件，拖入gulimall文件夹中，在项目的pom文件中添加一个。

```xml
<modules>
    <module>gulimall-coupon</module>
    <module>gulimall-member</module>
    <module>gulimall-order</module>
    <module>gulimall-product</module>
    <module>gulimall-ware</module>
    <module>renren-fast</module>
</modules>
```

（2）创建数据库gulimall-admin，运行renren-fast/db/mysql.sql。

（3）修改项目里renren-fast中的“application-dev.yml”文件，默认为dev环境，修改连接MySQL的URL、用户名和密码。

（4）安装Node.js，安装完后可使用node -v检查版本

安装node：http://nodejs.cn/download/ 选择windows下载。下载完安装。

（3）进入renren-fast-vue目录下，安装并启动renren-fast-vue

```shell
# 安装依赖
# 1
$ npm install -g cnpm --registry=https://registry.npm.taobao.org
# 2
$ cnpm install

# 启动服务
$ npm run dev
```

```
关于前端项目使用npm install报错的问题，首先确保安装了python3.0以上版本，并配置全局变量
其次大部分错误是报node-sass4.9.0安装失败。
执行以下步骤可以完美解决
首先把项目文件夹下的package.json里面的node-sass4.9.0改成4.9.2（不改可能也没关系，不过我改了，防止踩坑）
然后项目文件夹下打开cmd命令窗口（和Visual Studio Code的终端命令是一样的）（我在VScode中不成功，还是用cmd吧，在cmd中注意切换到renren-fast-vue项目目录）
执行：
npm i node-sass --sass_binary_site=https://npm.taobao.org/mirrors/node-sass/

等待挺长时间，执行成功看看有没有报错，如果没报错执行下面命令
npm install ，
没报错就是安装成功，然后在下面目录下使用npm run dev （运行项目）就ok了
注：这么做得原理就是先单独从淘宝镜像吧nod-sass下载下来，然后再进行编译，因为这句命令好像是不成功的，（npm config set registry http://registry.npm.taobao.org/），默认从github下载，导致报错的
如果之前安装失败的。先清理 缓存
清理缓存：npm rebuild node-sass
npm uninstall node-sass

另一个人的评论：

先把node_modules全部删除，然后再npm install chromedriver --chromedriver_cdnurl=http://cdn.npm.taobao.org/dist/chromedriver，最后npm install
```

（4）启动renren-fast服务，测试是否成功

### 9、逆向工程搭建

（1）下载[renren-generator](https://gitee.com/renrenio/renren-generator)项目到桌面，然后移动到我们IDEA项目目录中，同样配置好pom.xml。

```
git clone https://gitee.com/renrenio/renren-generator.git
```

```xml
<modules>
		<module>gulimall-coupon</module>
		<module>gulimall-member</module>
		<module>gulimall-order</module>
		<module>gulimall-product</module>
		<module>gulimall-ware</module>
		<module>renren-fast</module>
		<module>renren-generator</module>
</modules>
```

点 Maven中刷新一下

（2）修改application.yml中的MySQL配置

```yaml
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    #MySQL配置
    driverClassName: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://123.57.110.109:3306/gulimall_pms?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
```

（3）修改generator.properties

```properties
mainPath=com.atguigu
#包名
package=com.atguigu.gulimall
moduleName=product
#作者
author=zhenghaoyun
#Email
email=zhenghaoyun@foxmail.com
#表前缀(类名不会包含表前缀)
tablePrefix=pms_
```

（4）注释renren-generator\src\main\resources\template/Controller中所有的`@RequiresPermissions`以及`import org.apache.shiro.authz.annotation.RequiresPermissions;`

（5）运行RenrenApplication生成代码

运行成功后进入http://localhost/#generator.html

在网页上下方点击每页显示50个（pms库中的表），以让全部都显示，然后点击全部，点击生成代码。

![image-20210111153818073](https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20210111153821.png)

生成代码后是一个压缩包，解压压缩包，把main放到gulimall-product的同级目录下。

（6）gulimall-common模块的搭建

new modules— maven—在name上输入gulimall-common

在pom.xml中也自动添加了`<module>gulimall-common</module>`

在gulimall-common项目的pom.xml中添加

```xml
    <dependencies>

        <!-- mybatisPLUS-->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.3.2</version>
        </dependency>
        <!--简化实体类，用@Data代替getset方法-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.8</version>
        </dependency>
        <!-- httpcomponent包https://mvnrepository.com/artifact/org.apache.httpcomponents/httpcore -->
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpcore</artifactId>
            <version>4.4.13</version>
        </dependency>
        <dependency>
            <groupId>commons-lang</groupId>
            <artifactId>commons-lang</artifactId>
            <version>2.6</version>
        </dependency>

    </dependencies>
```

在gulimall-product的pom文件中添加

```xml
<dependency>
    <groupId>com.atguigu.gulimall</groupId>
    <artifactId>gulimall-common</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

```

（7）复制工具类

- renren-fast----utils包下的Query和PageUtils、R、Constant、Query复制到common项目的java/com.atguigu.common.utils下
- 复制renren-fast中的xss包的SQLFilter与exception包的RRException复制到common的com.atguigu.common目录下。
- 修改java/com.atguigu.common.utils下中Query类中`import io.renren.common.xss.SQLFilter;`为`import com.atguigu.common.xss.SQLFilter;`
- 将SQLFilter中的`import io.renren.common.exception.RRException;`修改为`import com.atguigu.common.utils.RRException;`

总之在product项目内哪里报错了，就去renrenfast里面找相关的类，复制进来即可。



（1）在common的pom.xml中导入导入MySQL和Servlet API

MySQL驱动8.0会自动适配5.7版本

```
<!-- 数据库驱动 https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.17</version>
</dependency>
<!--tomcat里一般都带-->
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>servlet-api</artifactId>
    <version>2.5</version>
    <scope>provided</scope>
</dependency>
```

设置scope为provided，为目标环境已存在，打包时就不带上啦，因为tomcat自带

（2）在product项目的resources目录下新建application.yml

```
spring:
  datasource:
    username: root
    password: root
    url: jdbc:mysql://123.57.110.109:3306/gulimall_pms
    driver-class-name: com.mysql.jdbc.Driver

# MapperScan
# sql映射文件位置
mybatis-plus:
  mapper-locations: classpath:/mapper/**/*.xml
  global-config:
    db-config:
      id-type: auto

```

> classpath 和 classpath* 区别：
>
> classpath：只会到你的class路径中查找找文件;
>
> classpath*：不仅包含class路径，还包括jar文件中(class路径)进行查找;
>
> classpath*的使用：当项目中有多个classpath路径，并同时加载多个classpath路径下（此种情况多数不会遇到）的文件，`*`就发挥了作用，如果不加\*，则表示仅仅加载第一个classpath路径。

（3）在主启动类上增加以下内容

```java
package com.atguigu.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.atguigu.gulimall.product.dao")
@SpringBootApplication
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
```

（4）进行测试

```java
package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Test
    public void test() {
        BrandEntity entity = new BrandEntity();
//        entity.setName("华为");
//        brandService.save(entity);
//        System.out.println("保存成功");

//        entity.setBrandId(1L);
//        entity.setDescript("插入测试");
//        entity.setName("插入测试name");
//        brandService.updateById(entity);
//        BrandEntity entity1 = brandService.getOne(new QueryWrapper<BrandEntity>().eq("brand_id", 1));
//        System.out.println(entity1);
        brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id",1)).forEach(System.out::println);


    }

}
```

> 关于@RunWith(SpringRunner.class)注解：
>
> 标准测试类里是要有@RunWith的，作用是告诉java你这个类通过用什么运行环境运行，例如启动和创建spring的应用上下文。否则你需要为此在启动时写一堆的环境配置代码。你在IDEA里去掉@RunWith仍然能跑是因为在IDEA里识别为一个JUNIT的运行环境，相当于就是一个自识别的RUNWITH环境配置。但在其他IDE里并没有。
>
> 所以，为了你的代码能在其他IDE里边正常跑，建议还是加@RunWith

（5）逆向生成所有微服务基本CRUD代码

如前4个步骤一样，生成其他模块的基本CRUD代码。

（6）设置各个模块的端口号

>  端口设置

coupon：7000

member：8000

order：9000

product：10000

ware：11000

> 如何设置

在模块的application.yml中添加：

```yaml
server:
  port: 8000
```

其他端口类似

## 四、SpringCloud Alibaba

在common模块的pom.xml中加入

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>2.2.0.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

这里的作用是是依赖管理，相当于以后在dependencies里引入spring cloud alibaba的组件就不用写版本号了， 全用dependencyManagement进行管理。

### 1、Nacos	服务发现与配置管理

Nacos：一个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台。

（1）下载并启动Nacos服务器

在这里我下载的是Windows版本的：https://github.com/alibaba/nacos/releases

下载完成后以单机模式启动Nacos

```shell
startup.cmd -m standalone
```

![image-20210114163845121](https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20210114163849.png)

启动成功后访问http://127.0.0.1:8848/nacos/

账号与密码皆为nacos

（2）启动服务发现

由于每个服务都需要Nacos注册中心，所有我们直接在common的pom依赖中添加：

```xml
<!--服务注册/发现-->
<dependency>
	<groupId>com.alibaba.cloud</groupId>
	<artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

在所有的Springboot主启动类中添加注解`@EnableDiscoveryClient` 开启服务注册发现功能

```java
@SpringBootApplication
@EnableDiscoveryClient
public class GulimallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallCouponApplication.class, args);
    }

}
```

在所有的`application.yml` 中配置 Nacos server 的地址，并添加name（这里以coupon为例）

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
  application:
    name: gulimall-coupon
```

启动每个服务后，可以在Nacos服务列表中看见每个服务。

![image-20210114165123405](https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20210114165126.png)

（3）启动配置管理

Nacos也可以作为配置中心，配置中心的意思是不在application.properties等文件中配置了，而是放到nacos配置中心公用，这样无需每台机器都改。

官方教程：https://github.com/alibaba/spring-cloud-alibaba/blob/master/spring-cloud-alibaba-examples/nacos-example/nacos-config-example/readme-zh.md

在common项目中添加pom依赖

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

在coupon项目中创建`bootstrap.properties` 中配置 Nacos server 的地址和应用名，这个文件是SpringBoot里规定的，他优先级别比application.properties高。

```properties
spring.cloud.nacos.config.server-addr=127.0.0.1:8848
spring.application.name=gulimall-coupon
```

访问`127.0.0.1:8848`去Nacos中心进行相关配置，这里需要注意的点：

1. `dataId`的格式

2. `@RefreshScope` 实现配置自动更新，即当Nacos配置中心的配置修改后，不用重启应用，也
3. 命名空间和分组的使用，可以创建多个命名空间（coupon、member、product等），然后创建多个分组（dev、test、prod等），实现不同环境不同配置
4. 加载多配置集：当我们把配置分成多个文件的时候，就可以加载多配置集，首先在`bootstrap.properties`中指定配置文件，然后去nacos创建配置文件

```properties
# 加载三个配置文件

# 配置文件名
spring.cloud.nacos.config.extension-configs[0].data-id=datasource.yml
# 配置文件所属分组
spring.cloud.nacos.config.extension-configs[0].group=dev
# 自动刷新
spring.cloud.nacos.config.extension-configs[0].refresh=true

spring.cloud.nacos.config.extension-configs[1].data-id=mybatis.yml
spring.cloud.nacos.config.extension-configs[1].group=dev
spring.cloud.nacos.config.extension-configs[1].refresh=true

spring.cloud.nacos.config.extension-configs[2].data-id=other.yml
spring.cloud.nacos.config.extension-configs[2].group=dev
spring.cloud.nacos.config.extension-configs[2].refresh=true
```

![image-20200726160748223](https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20200726160806.png)

<center>nacos配置中心实例</center>

**！！坑：**

> 如果不想要在nacos中配置，想要使用本地配置，一定要记得将nacos-config的依赖删掉，不然就可能因为找不到bootstrp.properties配置而直接报错，不会读取你的application.properties配置！！

### 2、OpenFeign

Feign即声明式远程调用，Feign是一个声明式的HTTP客户端，他的目的就是让远程调用更加简单。给远程服务发的是HTTP请求。

**测试member和coupon的远程调用**

如会员服务想要远程调用优惠券服务，只需要给会员服务里引入openfeign依赖，他就有了远程调用其他服务的能力。

（1）在member中添加pom依赖（前面已经添加过，这里不用再添加）

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

（2）在coupons模块的com.atguigu.gulimall.coupon.controller.CouponController中添加如下内容：

```java
@RequestMapping("/member/list")
// 所有返回都返回R
public R memberCoupons() {
    CouponEntity couponEntity = new CouponEntity();
    couponEntity.setCouponName("满100-10");
    return R.ok().put("coupons", Arrays.asList(couponEntity));
}
```

这样我们就准备了优惠券的调用内容

（3）添加@EnableFeignClients注解

@EnableFeignClients表明启动远程调用，括号内为Feign接口的位置。

![image-20210114171657724](https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20210114171658.png)

（4）创建Feign接口

```java
package com.atguigu.gulimall.member.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ZhengHaoYun  2021/1/14 17:06
 */
// 指明需要调用哪个服务的功能
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    //注意写全地址
    @RequestMapping("/coupon/coupon/member/list")
    R membercoupons();//得到一个R对象
}

```

（5）在member的控制层写一个测试请求

```java
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    CouponFeignService couponFeignService;

    @RequestMapping("/coupons")
    public R test(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("会员昵称张三");
        R membercoupons = couponFeignService.membercoupons();//假设张三去数据库查了后返回了张三的优惠券信息

        //打印会员和优惠券信息
        return R.ok().put("member",memberEntity).put("coupons",membercoupons.get("coupons"));
    }
}
```

（6）重新启动coupon和member服务

在这里我启动member服务的时候出现了一个问题：

>Error creating bean with name 'memberController': Unsatisfied dependency expressed through field 'couponFeignService'; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'com.atguigu.gulimall.member.feign.CouponFeignService': FactoryBean threw exception on object creation; nested exception is java.lang.IllegalStateException: No Feign Client for loadBalancing defined. Did you forget to include spring-cloud-starter-loadbalancer?

原因是：

SpringCloud Feign在Hoxton.M2 RELEASED版本之后不再使用Ribbon而是使用spring-cloud-loadbalancer，所以不引入spring-cloud-loadbalancer会报错，本着沉稳的态度，我将SpringBoot版本修改为2.1.8，SpringCloud版本修改为Greenwich.SR3，SpringCloud alibaba版本修改为2.1.2。

<img src="https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20210130141610.png" alt="image-20210130141526482" style="zoom:67%;" />

```xml
找到对应的位置进行修改：

<!-- 修改SpirngCloud版本 -->
<properties>
    <java.version>1.8</java.version>
    <spring-cloud.version>Greenwich.SR3</spring-cloud.version>
</properties>

<!-- 修改SpirngCloud Alibaba版本 -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>2.1.2.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<!-- 修改SpringBoot版本 -->
<parent>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-parent</artifactId>
	<version>2.1.8.RELEASE</version>
	<relativePath/> <!-- lookup parent from repository -->
</parent>
```

得到如下结果：

```
{"msg":"success","code":0,"coupons":[{"id":null,"couponType":null,"couponImg":null,"couponName":"满100-10","num":null,"amount":null,"perLimit":null,"minPoint":null,"startTime":null,"endTime":null,"useType":null,"note":null,"publishCount":null,"useCount":null,"receiveCount":null,"enableStartTime":null,"enableEndTime":null,"code":null,"memberLevel":null,"publish":null}],"member":{"id":null,"levelId":null,"username":null,"password":null,"nickname":"会员昵称张三","mobile":null,"email":null,"header":null,"gender":null,"birth":null,"city":null,"job":null,"sign":null,"sourceType":null,"integration":null,"growth":null,"status":null,"createTime":null}}
```

### 3、SpringCloud Gateway

发送请求需要知道商品服务的地址，如果商品服务器有1号、2号、和3号服务器，1号掉线后，还得改服务器，所以需要网关动态地管理，他能从注册中心中实时地感知某个服务上线还是下线。

请求也要加上询问权限，看用户有没有权限访问这个请求，也需要网关。

所以我们使用SpringCloud的Gateway组件做网关功能。

网关是请求浏览的入口，常用功能包括路由转发，权限校验，限流控制等。SpringCloud Gateway取代了zuul网关。

https://spring.io/projects/spring-cloud-gateway

参考手册：https://cloud.spring.io/spring-cloud-gateway/2.2.x/reference/html/

三大核心概念：

- **Route**（路由）: The basic building block of the gateway. It is defined by an ID, a destination URI, a collection of predicates断言, and a collection of filters. A route is matched if the aggregate predicate is true.路由网关的基本构建块。它由ID，目标URI，断言和过滤器集合定义。如果断言为true，则匹配路由。
- **Predicate**（断言）: This is a [Java 8 Function Predicate](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html). The input type is a [Spring Framework `ServerWebExchange`](https://docs.spring.io/spring/docs/5.0.x/javadoc-api/org/springframework/web/server/ServerWebExchange.html). This lets you match on anything from the HTTP request, such as headers or parameters.就是Java里的断言函数，匹配请求里的任何信息，包括请求头等。
- **Filter**: These are instances of [Spring Framework `GatewayFilter`](https://docs.spring.io/spring/docs/5.0.x/javadoc-api/org/springframework/web/server/GatewayFilter.html) that have been constructed with a specific factory. Here, you can modify requests and responses before or after sending the downstream request.进行过滤，在此，可以在发送下游请求之前或之后修改请求和响应。

客户端发请求给服务端。中间有网关。先交给映射器，如果能处理就交给handler进行处理，然后由filter进行一系列过滤，再给指定的服务，最后返回给客户端。

![img](https://img-blog.csdnimg.cn/img_convert/fdc2f8288ce78208ae8283ba3badc009.png)



（1）添加pom依赖

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

（2）相关概念

- **路由（Route）**：路由网关的基本构建块。它由ID，目标URI，断言和过滤器集合定义。如果断言为true，则匹配路由。
- **断言（Predicate）**：匹配规则
- **过滤器**：进行过滤，在此，可以在发送下游请求之前或之后修改请求和响应。

示例：

```
spring:
  cloud:
    gateway:
      routes:
      - id: query_route
        uri: https://example.org
        predicates:
        - Query=foo, ba.
 
# 说明：
如果请求包含一个foo查询参数，其值与ba.正则表达式匹配，则此路由将匹配，因此bar和baz将匹配。然后将会跳转到https://example.org
```

（3）更多断言工厂与过滤器类型可查看[官方文档](https://www.springcloud.cc/spring-cloud-greenwich.html#_spring_cloud_gateway)

## 五、前端

### 1、ECMAScript 6即ES6

ES6是浏览器脚本语言的规范，而我们熟知的js语言，如javascript则是规范的具体实现。

（1）let的使用

* ES6可以用let定义块级作用域变量

在ES6之前，我们都是用var来声明变量，而且JS只有函数作用域和全局作用域，没有块级作用域，所以`{}`限定不了var声明变量的访问范围。
例如：

```javascript
{ 
  var i = 9;
} 
console.log(i);  // 9
```

ES6新增的`let`，可以声明块级作用域的变量。

```javascript
{ 
  let i = 9;     // i变量只在{}内有效！
} 
console.log(i);  // Uncaught ReferenceError: i is not defined
```

* let变量不能重复声明

let不允许在相同作用域内，重复声明同一个变量。否则报错：`Uncaught SyntaxError: Identifier 'XXX' has already been declared`

例如：

```javascript
let a = 0;
let a = 'sss';
// Uncaught SyntaxError: Identifier 'a' has already been declared
```

* 用`let`声明的变量，不存在变量提升

变量提升：函数和变量的生命都将被提升到函数的最顶部，即变量可以先使用后声明。

var可以变量提升，而用`let`声明的变量，不存在变量提升。而且要求必须 等`let`声明语句执行完之后，变量才能使用，不然会报`Uncaught ReferenceError`错误。

例如：

```javascript
console.log(aicoder);    // 错误：Uncaught ReferenceError ...
let aicoder = 'aicoder.com';
// 这里就可以安全使用aicoder
```

（2）解构表达式

数组解构

```javascript
有一个数组

let arr = [2,5,-6,10];

以前：

let x = arr[0];

let y = arr[1];

现在：

let [x,y] = arr;

直接可以将数组后2个值赋值给a,b
```

对象解构

对象的解构可以用于变量的生命

```javascript
let node = {
    name: 'mike',
    age: 25
};
let {name, age} = node;
console.log(name); // mike
console.log(age); // 25
```

刚刚的例子是**数组解构**、**对象解构**，因为前端了解就行，要用的时候再百度吧。

（3）字符串扩展

* 几个新的API（startwith、endwith等等）
* 字符串模板，使用``可以不需要拼串
* 使用${}可以在字符串中插入变量和js表达式
* 支持`let arr={1,2,3};let [a,b,c]=arr`这种语法
* 支持对象解析：`const{name:abc, age, language} = person;`冒号代表改名
* 支持一个字符串为多行

```javascript
// 1.
let str = "hello.vue";
console.log(str.startsWith("hello")); // true
console.log(str.endsWith(".vue")); // true;
// ...

// 2.
let ss = `<div>
            <span>hello world></span>
          </div>`;
console.log(ss);

// 3.
function fun() {
    return "这是一个函数";
}

let abc = 10;
let info = `我是${abc}, 今年${abc + 10}了, 我想说：${fun()}`;
console.log(info);
```



（4）函数优化

* 函数参数默认值设置

```javascript
// 在ES6之前，我们无法给一个函数参数设置默认值，只能采用变通写法
function f(a, b) {
    // 判断b是否为空，为空就赋予默认值1
    b = b || 1;
    return a + b;
}

// 不给b传参
console.log(f(10));

// 在ES6之后：直接给参数写上默认值，没传就会自动使用默认值
function f1(a, b = 1) {
    return a + b;
}
// 不给b传参，b的默认值为1
console.log(f(10));
```

* 不定参数，即不规定参数数量

```javascript
// 在ES6之后：直接给参数写上默认值，没传就会自动使用默认值
function f1(a, b = 1) {
    return a + b;
}

// 不给b传参，b的默认值为1
console.log(f(10));

// 不定参数
function f2(...values) {
    console.log(values.length);
}

f2(1, 2) // 2
f2(1, 2, 3, 4) // 4
```

* 箭头函数，和lambda表示类似，箭头使用的是=>

（5）对象优化

* 新增的API：可以获取map的键值对等，如Object.keys()、values、entries
* 声明对象简写
* 对象的属性简写
* 对象拓展运算符
* map和reduce

（6）promise封装异步操作：简化ajax嵌套使用

（7）模块化：将代码进行拆分，方便重复利用。类似java中的导包，而JS中没有包的概念，换来的是模块

* export
* import

### 2、VUE

（1）MVVM思想

* M：Model，模型
* V：View，视图渲染效果
* VM：View-Model，模型与视图间的双向操作

（2）基本语法和插件安装

（3）单向绑定和双向绑定

（4）基本语法

（5）基本指令，如v-on、v-for、v-if等

（6）计算属性和侦听器

（7）组件化基础

（8）生命周期和钩子函数

（9）使用Vue脚手架进行模块化开发

### 3、elementUI的使用

[官网](https://element.eleme.cn/#/zh-CN)有详细的说明

> 因前端不属于我的领域，在这只写了提纲，了解基本使用方法即可，不明白的地方可以去查官方文档。

## 六、具体功能实现

### 1、三级分类                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        

（1）三级分类的查询

* 在CategoryEntity中添加一个字段用来存储子菜单

```
// @TableField(exist = false)表示在数据库中这个字段不存在，只是用来存储子分类
@TableField(exist = false)
private List<CategoryEntity> children;
```

* 业务逻辑

```
ist<CategoryEntity> entities = baseMapper.selectList(null);

//2、组装成父子的树形结构

//2.1、找到所有的一级分类
List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
        categoryEntity.getParentCid() == 0
).map(menu -> {
    // 可能子菜单下面还有子菜单，所有也得查出子菜单的子菜单
    menu.setChildren(getChildren(menu, entities));
    return menu;
}).sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort()))    // 排序
).collect(Collectors.toList()); // 收集
return level1Menus;
```

* 前端使用vue得到数据配合elementUI实现三级分类菜单的显示

> 补充：可以使用网关统一配置跨域请求，进行路由的配置

（2）三级分类的删除

* 在前端菜单页面增添“删除”按钮，需要注意什么时候可以删除什么时候可以新增，可以使用vue-if去实现自己的逻辑
* 后台实现删除，删除需要使用逻辑删除（即不是真正的在数据库中删掉记录，而是将某个字段的值改变，使得这条记录不再展示，就是逻辑上的删除），删除的时候需要注意当前正在删除的这个分类是否在其他地方被引用。
* 由于现在暂时还没有其他地方引用，可以添加待做事项

```
//TODO  1、检查当前删除的菜单，是否被别的地方引用
```

* 逻辑删除思路：

  * 逻辑删除配置

  ```
  logic-delete-value: 1 #逻辑已删除值（默认为1）
  logic-not-delete-value: 0 #逻辑未删除值（默认为0）	
  ```

  * 实体类字段上加上`@TableLogic`注解

  ```
  /**
   * 是否显示[0-不显示，1显示]
   */
  @TableLogic(value = "1", delval = "0")	
  // 因为这里数据库中showStatus的含义刚好和我们所配置的相反，所以在这里进行单独设置
  // 利用(value = "1", delval = "0")表示0是删除不显示，1是未删除可显示
  private Integer showStatus;
  ```

* 删除效果细化：

  * 确认删除提示框
  * 删除后再次调用显示分类方法，并且是显示之前的那个节点

（3）三级分类的新增与修改

* 新增修改按钮与删除按钮
* 修改时要实现回显的效果

（4）三级分类拖拽效果的实现

* 设置allowDrop属性
* 拖拽的节点不可超过三级菜单，即不可将节点拖拽至第四层（因为项目规定了只有三层菜单）
  * 找出最深子节点的层级-当前节点的层级+1=当前节点包含的层数
  * 当前节点包含的层数+父节点的层级<=3即可拖动
  * 怎样求出最深子节点的层级？
    * 设置一个变量：最大层级
    * 遍历当前节点的所有子节点
    * 判断当前子节点的层级是否大于最大层级，大于则改变最大层级的值为当前子节点的层级，否则不改变
    * 判断当前子节点的子节点（递归调用）的层级是否大于最大层级
  * 如果拖动没有改变当前节点的层级，那自然也不需要前面所说的逻辑了
* 成功拖拽后要修改分类的相关数据，如层级、父节点、sort等

### 2、阿里云对象存储服务

![image-20200728140254296](https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20200728140304.png)

<center>阿里云对象存储服务-服务端签名后直传</center>

操作流程：

* 用户向应用服务器请求上传Policy（策略）
* 应用服务器返回上传Policy
* 用户直接上传数据到OSS

两种方式：

（1）OSS整合JDK，可以看[帮助文档](https://help.aliyun.com/document_detail/32008.html?spm=a2c4g.11186623.6.796.381b7815Yws6Ln)

（2）SpringCloud Alibaba-OSS（推荐）

* 导入POM依赖
* 在配置文件中配置OSS服务中的accessKey、secretKey和endPoint
* 使用时自动注入OSSClient，然后调用各种方法进行操作

（3）服务端签名后直传[参考文档](https://help.aliyun.com/document_detail/31926.html?spm=a2c4g.11186623.6.1605.a3d245dc9g13ul)

### 3、表单校验

（1）前端校验：使用element-ui的表单校验器进行校验

（2）后端校验（JSR303）：

* 使用校验注解，如在方法参数上添加@Valid并在实体类上添加@NotNull
* 可以定义自己的message提示

* BindingResult中封装了校验结果

* 集中处理所有的参数校验异常（@ControllerAdvice与@ExcetpionHandler）
* 校验分组（如新增的时候id为空而修改的时候id不能为空，所以需要对id的校验进行分组），在方法参数上使用@Validated可以指定校验分组

（3）系统错误码

![image-20200728162540182](https://zhenghaoyun.oss-cn-beijing.aliyuncs.com/img/20200728162540.png)

（4）自定义校验

定义注解

```java
package com.atguigu.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = {ListValueConstraintValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface ListValue {
    String message() default "{com.atguigu.common.valid.ListValue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int[] vals() default {};
}
```
自定义校验器
```java
package com.atguigu.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {

    private Set<Integer> set = new HashSet<>();

    //初始化方法
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] vals = constraintAnnotation.vals();
        for (int val : vals) {
            set.add(val);
        }
    }

    /**
     * 判断是否校验成功
     *
     * @param value   需要校验的值
     * @param context
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return set.contains(value);
    }
}
```

### 4、SPU与SKU

SPU = Standard Product Unit （标准化产品单元）,SPU是商品信息聚合的最小单位，是一组可复用、易检索的标准化信息的**集合**，该集合描述了一个产品的特性。

SKU=stock keeping unit(库存量单位) SKU即库存进出计量的单位（买家购买、商家进货、供应商备货、工厂生产都是依据SKU进行的）。

比说iPhone 8分为iPhone 8与iPhone 8 Plus，这两个就是SPU

但是iPhone 8又有不同颜色，不同的内存，比如说 iPhone 8 银色 64G就是SKU，SKU就是对应具体的哪一个手机，我们买到的具体的手机就是SKU。

