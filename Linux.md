# Linux基础命令
## Linux的目录结构
<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987370570-f54a2e95-59f2-4904-8c77-153e6994bb29.png)

+ `/`，根目录是最顶级的目录了
+ Linux只有一个顶级目录：`/`
+ 路径描述的层次关系同样适用`/`来表示
+ /home/itheima/a.txt，表示根目录下的home文件夹内有itheima文件夹，内有a.txt



## ls命令
功能：列出文件夹信息

语法：`ls [-l -h -a] [参数]`

+ 参数：被查看的文件夹，不提供参数，表示查看当前工作目录  /表示根目录
+ -l，以列表形式查看
+ -h，配合-l，以更加人性化的方式显示文件大小
+ -a，显示隐藏文件（以.开头的文件或文件夹代表被隐藏的文件）

ls命令的作用是以平铺的形式列出当前工作目录下的内容

HOME目录是每个用户在linux系统中的专属目录 默认在/home/用户名

当前工作目录：linux命令行在执行命令的时候需要一个工作目录 打开命令行程序（终端）默认设置工作目录在用户的HOME目录

### 隐藏文件、文件夹
在Linux中以`.`开头的，均是隐藏的。

默认不显示出来，需要`-a`选项才可查看到。



## pwd命令
功能：展示当前工作目录

语法：`pwd`



## cd命令
功能：切换工作目录

语法：`cd [目标目录]`

参数：目标目录，要切换去的地方，不提供默认切换到`当前登录用户HOME目录`

目标目录也可以是根目录下面的目录 比如cd /bin

## HOME目录
每一个用户在Linux系统中都有自己的专属工作目录，称之为HOME目录。

+ 普通用户的HOME目录，默认在：`/home/用户名`
+ root用户的HOME目录，在：`/root`



FinalShell登陆终端后，默认的工作目录就是用户的HOME目录



## 相对路径、绝对路径
+ 相对路径，<font style="background-color:#f3bb2f;">非</font>`/`开头的称之为相对路径相对路径表示以`当前目录`作为起点，去描述路径，如`test/a.txt`，表示当前工作目录内的test文件夹内的a.txt文件
+ 绝对路径，<font style="background-color:#f3bb2f;">以</font>`/`开头的称之为绝对路径绝对路径从`根`开始描述路径



## 特殊路径符
+ `.`，表示当前，比如./a.txt，表示当前文件夹内的`a.txt`文件
+ `..`，表示上级目录，比如`../`表示上级目录，`../../`表示上级的上级目录
+ `~`，表示用户的HOME目录，比如`cd ~`，即可切回用户HOME目录

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782998724318-db353dfc-8286-4d9f-89a0-830b5a5547d5.png)

## mkdir命令
功能：创建文件夹

语法：`mkdir [-p] 参数`

+ 参数：被创建文件夹的路径
+ 选项：-p，可选，表示创建前置路径



## touch命令
功能：创建文件

语法：`touch 参数`

+ 参数：被创建的文件路径



## cat命令
功能：查看文件内容

语法：`cat 参数`

+ 参数：被查看的文件路径



## more命令
功能：查看文件，可以支持翻页查看

语法：`more 参数`

+ 参数：被查看的文件路径
+ 在查看过程中：
    - `空格`键翻页
    - `q`退出查看



## cp命令
功能：复制文件、文件夹

语法：`cp [-r] 参数1 参数2`

+ 参数1，被复制的
+ 参数2，要复制去的地方
+ 选项：-r，可选，复制文件夹使用

示例：

+ cp a.txt b.txt，复制当前目录下a.txt为b.txt
+ cp a.txt test/，复制当前目录a.txt到test文件夹内
+ cp -r test test2，复制文件夹test到当前文件夹内为test2存在



## mv命令
功能：移动文件、文件夹

语法：`mv 参数1 参数2`

+ 参数1：被移动的
+ 参数2：要移动去的地方，参数2如果不存在，则会进行改名



## rm命令
功能：删除文件、文件夹

语法：`rm [-r -f] 参数...参数`

+ 参数：支持多个，每一个表示被删除的，空格进行分隔
+ 选项：-r，删除文件夹使用
+ 选项：-f，强制删除，不会给出确认提示，一般root用户会用到



> rm命令很危险，一定要注意，特别是切换到root用户的时候。
>



## which命令
功能：查看命令的程序本体文件路径

语法：`which 参数`

+ 参数：被查看的命令



## find命令
功能：搜索文件

语法1按文件名搜索：`find 路径 -name 参数`

+ 路径，搜索的起始路径
+ 参数，搜索的关键字要加双引号，支持通配符*（不用加双引号）， 比如：`*`test表示搜索任意以test结尾的文件

语法2 按文件大小去查找：find 路径 -size +|-n(kMG)

+是大于 -是小于

## grep命令
功能：过滤关键字

语法：`grep [-n] 关键字 文件路径`

+ 选项-n，可选，表示在结果中显示匹配的行的行号。
+ 参数，关键字，必填，表示过滤的关键字，带有空格或其它特殊符号，建议使用””将关键字包围起来
+ 参数，文件路径，必填，表示要过滤内容的文件路径，可作为内容输入端口



> 参数文件路径，可以作为管道符的输入
>



## wc命令
功能：统计

语法：`wc [-c -m -l -w] 文件路径`

+ 选项，-c，统计bytes数量
+ 选项，-m，统计字符数量
+ 选项，-l，统计行数
+ 选项，-w，统计单词数量
+ 参数，文件路径，被统计的文件，可作为内容输入端口



> 参数文件路径，可作为管道符的输入
>



## 管道符|
写法：`|`

功能：将符号左边的结果，作为符号右边的输入

示例：

`cat a.txt | grep itheima`，将cat a.txt的结果，作为grep命令的输入，用来过滤`itheima`关键字



可以支持嵌套：

`cat a.txt | grep itheima | grep itcast`

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1783050059737-76c7ec2a-c5e8-4de3-8972-84f5c7b24146.png)

## echo命令
功能：输出内容

语法：`echo 参数`

+ 参数：被输出的内容



## `反引号
功能：被两个反引号包围的内容，会作为命令执行

示例：

+ echo `pwd`，会输出当前工作目录



## tail命令
功能：查看文件尾部内容

语法：`tail [-f] 参数`

+ 参数：被查看的文件
+ 选项：-f，持续跟踪文件修改



## head命令
功能：查看文件头部内容

语法：`head [-n] 参数`

+ 参数：被查看的文件
+ 选项：-n，查看的行数



## 重定向符
功能：将符号左边的结果，输出到右边指定的文件中去

+ `>`，表示覆盖输出
+ `>>`，表示追加输出

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1783050825929-48decd95-b9ab-4281-ab14-a01099fcdb41.png)

## vi编辑器
### 命令模式快捷键
<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987370607-723f3fc6-6c0d-491b-b4d1-f097a1129b26.png)

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987370672-917be4be-a134-493e-8495-7bdd121fc697.png)

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987370724-a0730e02-8c42-4e18-b79e-72fc12b46d43.png)

### 底线命令快捷键
<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987370948-525fd4f6-0570-4156-ab0e-306a26253384.png)



## 命令的选项
我们学习的一系列Linux命令，它们所拥有的选项都是非常多的。

比如，简单的ls命令就有：-a -A -b -c -C -d -D -f -F -g -G -h -H -i -I -k -l -L -m -n -N -o -p -q -Q -r-R -s -S -t -T -u -U -v -w -x -X -1等选项，可以发现选项是极其多的。

课程中， 并不会将全部的选项都进行讲解，否则，一个ls命令就可能讲解2小时之久。

课程中，会对常见的选项进行讲解， 足够满足绝大多数的学习、工作场景。



### 查看命令的帮助
可以通过：`命令 --help`查看命令的帮助手册

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987371337-b8000189-d797-43cd-961e-515c1df8d8d8.png)

### 查看命令的详细手册
可以通过：`man 命令`查看某命令的详细手册

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987371393-33e47f34-542a-42e2-b887-c22e292708fc.png)

# Linux用户和权限
## root用户
<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1783061398028-92a8266b-caf9-4d67-a937-eb0730f7dd38.png)

配置办法：进入到root用户 输入visudo 进入vi模式 在最后一行输入zhuzhuALL=(ALL)	NOPASSWD:ALL

保存退出 切换到zhuzhu用户 在命令行前面加上sudo 即可在根目录下创建文件

另外：普通用户只能在HOME目录下进行操作 其他目录下可能会没有权限 但是root是有权限的 

另外 普通用户想进入超级管理用户并且不输入密码 可以直接在配置好sudo以后输入命令

sudo su - root

root到普通用户可以按exit 也可以ctrl＋d

## 用户和用户组
<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1783061860113-5c9ab875-3e2d-4b34-95df-24e5c9c82be3.png)

### 用户组
<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1783063807161-2158e787-1e23-4a12-aab4-f3f18078bedb.png)

### 用户
<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1783063878420-52d62c97-f5e6-43c8-87a3-3f763c8e4ffd.png)<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1783063954720-74e6a3c3-fd9d-4188-9232-666bce7376df.png)<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1783063974749-9b22d4f5-9785-47eb-a970-a28a0eea11d1.png)

### 查看权限控制信息
<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1783069093813-a799ecd2-45e4-4715-9506-64692cba9a97.png)<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1783069641520-a866fc6c-74e7-41a3-8800-39c427894f8a.png)

### chmod命令
<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1783069818038-cb3d8509-2650-4b55-be90-2010684e287a.png)<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1783073132444-027b49eb-5d22-497e-94c4-50c44c757afb.png)



### chown命令
<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1783073293441-4f802ea7-7aea-46e4-8d60-065fa55be6c6.png)

# Linux常用操作
## 常见快捷键
ctrl+d 退出登录 或者exit

ctrl+l 清屏 或者clear

ctrl+c 强制停止

历史命令搜索的三种办法：history 	！开头字母	ctrl+r关键字搜索 如果这条命令正是我想要的 回车键可以直接执行 ctrl+左右键可以得到这条命令不执行

ctrl+a|e 光标移动到命令行的开始或者结束

ctrl+<-|-> 向左或向右移动一个单词

## 软件安装
+ CentOS系统使用：
    - yum [install remove search] [-y] 软件名称
        * install 安装
        * remove 卸载
        * search 搜索
        * -y，自动确认
+ Ubuntu系统使用
    - apt [install remove search] [-y] 软件名称
        * install 安装
        * remove 卸载
        * search 搜索
        * -y，自动确认

> yum 和 apt 均需要root权限
>

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1783075042446-5b587aa9-2a18-4b54-beb1-3efb13b1763f.png)

## systemctl
功能：控制系统服务的启动关闭等

语法：`systemctl start | stop | restart | disable | enable | status 服务名`

+ start，启动
+ stop，停止
+ status，查看状态
+ disable，关闭开机自启
+ enable，开启开机自启
+ restart，重启



## 软链接
功能：创建文件、文件夹软链接（快捷方式）

语法：`ln -s 参数1 参数2`

+ 参数1：被链接的
+ 参数2：要链接去的地方（快捷方式的名称和存放位置）



## 日期
语法：`date [-d] [+格式化字符串]`

+ -d 按照给定的字符串显示日期，一般用于日期计算
+ 格式化字符串：通过特定的字符串标记，来控制显示的日期格式
    - %Y   年%y   年份后两位数字 (00..99)
    - %m   月份 (01..12)
    - %d   日 (01..31)
    - %H   小时 (00..23)
    - %M   分钟 (00..59)
    - %S   秒 (00..60)
    - %s   自 1970-01-01 00:00:00 UTC 到现在的秒数



示例：

+ 按照2022-01-01的格式显示日期<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987371436-e3bf884d-a06f-4f01-a5e2-08975ed20ac1.png)
+ 按照2022-01-01 10:00:00的格式显示日期<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987371675-2384ea46-4077-4d2b-8cb0-547c8f1a0897.png)
+ -d选项日期计算<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987371803-59ce69bb-e825-421d-b5af-4694a1dd63f2.png)
    - 支持的时间标记为：<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987372093-c2384524-7179-4b2c-842c-50d20d1c5dd9.png)





## 时区
修改时区为中国时区

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987372226-4a2d1fd3-e14b-48ee-9f1c-b91997ac943b.png)



## ntp
功能：同步时间

安装：`yum install -y ntp`

启动管理：`systemctl start | stop | restart | status | disable | enable ntpd`



手动校准时间：`ntpdate -u ntp.aliyun.com`



## ip地址
格式：a.b.c.d

+ abcd为0~255的数字



特殊IP：

+ 127.0.0.1，表示本机
+ 0.0.0.0
    - 可以表示本机
    - 也可以表示任意IP（看使用场景）



查看ip：`ifconfig`



## 主机名
功能：Linux系统的名称

查看：`hostname`

设置：`hostnamectl set-hostname 主机名`

windows域名解析（主机名映射）：

以管理员身份运行记事本 打开C:/windows/system32/driver/etc/host 192.168.11.133 centos	ctrl+s保存

## 配置VMware固定IP
1. 修改VMware网络，参阅PPT，图太多
2. 设置Linux内部固定IP修改文件：`/etc/sysconfig/network-scripts/ifcfg-ens33`示例文件内容：

```shell
TYPE="Ethernet"
PROXY_METHOD="none"
BROWSER_ONLY="no"
BOOTPROTO="static"			# 改为static，固定IP
DEFROUTE="yes"
IPV4_FAILURE_FATAL="no"
IPV6INIT="yes"
IPV6_AUTOCONF="yes"
IPV6_DEFROUTE="yes"
IPV6_FAILURE_FATAL="no"
IPV6_ADDR_GEN_MODE="stable-privacy"
NAME="ens33"
UUID="1b0011cb-0d2e-4eaa-8a11-af7d50ebc876"
DEVICE="ens33"
ONBOOT="yes"
IPADDR="192.168.88.131"		# IP地址，自己设置，要匹配网络范围
NETMASK="255.255.255.0"		# 子网掩码，固定写法255.255.255.0
GATEWAY="192.168.88.2"		# 网关，要和VMware中配置的一致
DNS1="192.168.88.2"			# DNS1服务器，和网关一致即可
```



## ps命令
功能：查看进程信息

语法：`ps -ef`，查看全部进程信息，可以搭配grep做过滤：`ps -ef | grep xxx`

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1783178634380-2affdfc3-033f-4143-8c48-00fb2ee8f802.png)<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1783178724952-c524e5b6-2065-473c-b041-55c44fab0599.png)

## kill命令
<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987372118-3642f7ab-6bf2-465b-973f-a7dbac53008c.png)

## 端口
<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1783178437173-4fd41a9f-3d35-4d32-a9bf-700dc075c113.png)

## nmap命令
<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987372190-1dc1994e-0c81-4907-90d9-41d2678b711c.png)



## netstat命令
功能：查看端口占用

用法：`netstat -anp | grep xxx`



## ping命令
测试网络是否联通

语法：`ping [-c num] 参数`

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987372525-3d5a11e7-a1cf-4f9b-963d-76fa8fe8a356.png)



## wget命令
<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987372851-ce29ad74-a3bf-4067-86fb-5af8f2b7a45c.png)

## curl命令
<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987372922-1b8ec88d-232c-4223-aa4a-1d1c1c8e5d75.png)

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987372932-8d5fe1fe-475e-473b-b584-de4324cf8ed5.png)

## 主机状态监控
### top命令
功能：查看主机运行状态

语法：`top`，查看基础信息



可用选项：

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987372923-5dd4b83d-3c75-4423-96c1-cd0437b309bf.png)



交互式模式中，可用快捷键：

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987373498-9984dc48-8a17-4c4b-80aa-7e6a0a8efa15.png)



### df命令
查看磁盘占用

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987373466-eed1babb-0a03-4563-ae94-82e03259a2fe.png)



### iostat命令
查看CPU、磁盘的相关信息

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987373549-aa3358c1-c466-4d06-ba68-a59ad02cb4b2.png)

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987373583-5f64626a-d6f7-44d3-85f6-bc1217731a66.png)



## sar命令
查看网络统计

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987373764-ee8e013e-706b-4704-b042-ec38d436cc25.png)



## 环境变量
+ 临时设置：export 变量名=变量值
+ 永久设置：
    - 针对用户，设置用户HOME目录内：`.bashrc`文件
    - 针对全局，设置`/etc/profile`



### PATH变量
记录了执行程序的搜索路径

可以将自定义路径加入PATH内，实现自定义命令在任意地方均可执行的效果



## $符号
可以取出指定的环境变量的值

语法：`$变量名`

示例：

`echo $PATH`，输出PATH环境变量的值

`echo ${PATH}ABC`，输出PATH环境变量的值以及ABC

如果变量名和其它内容混淆在一起，可以使用${}





## 压缩解压
### 压缩
`tar -zcvf 压缩包 被压缩1...被压缩2...被压缩N`

+ -z表示使用gzip，可以不写



`zip [-r] 参数1 参数2 参数N`

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987374288-c6cc48bf-ef2c-408b-8089-8de61442203e.png)



### 解压
`tar -zxvf 被解压的文件 -C 要解压去的地方`

+ -z表示使用gzip，可以省略
+ -C，可以省略，指定要解压去的地方，不写解压到当前目录







`unzip [-d] 参数`

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987374347-c2c8374e-9547-43a9-9433-b00be7ff5082.png)





## su命令
切换用户

语法：`su [-] [用户]`

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987374364-3520b523-0dd4-4d5d-a2ba-38315f3c1c94.png)



## sudo命令
<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987374494-4f79ac45-4ab1-4c81-831a-a38db850e6ea.png)



比如：

```shell
itheima ALL=(ALL)       NOPASSWD: ALL
```

在visudo内配置如上内容，可以让itheima用户，无需密码直接使用`sudo`



## chmod命令
修改文件、文件夹权限



语法：`chmod [-R] 权限 参数`

+ 权限，要设置的权限，比如755，表示：`rwxr-xr-x`<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987374709-df5ae7d5-859a-45ab-933c-0063cb17858c.png)
+ 参数，被修改的文件、文件夹
+ 选项-R，设置文件夹和其内部全部内容一样生效



## chown命令
修改文件、文件夹所属用户、组



语法：`chown [-R] [用户][:][用户组] 文件或文件夹`

<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987374864-26e5911e-480c-4be6-930b-894f67212901.png)



## 用户组管理
<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987375019-d4b8affd-1503-4f5b-bae7-82c6647bd01e.png)



## 用户管理
<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987375061-defe57f2-6d62-4265-83aa-92d7beb5dbd6.png)



## getenv命令
+ `getenv group`，查看系统全部的用户组<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987375132-a3f509b0-3406-4a28-b609-c0737bb1bb53.png)
+ `getenv passwd`，查看系统全部的用户<!-- 这是一张图片，ocr 内容为： -->
![](https://cdn.nlark.com/yuque/0/2026/png/60867998/1782987375293-1acfa220-cb72-46cb-b632-99b22fbb0dd1.png)



## env命令
查看系统全部的环境变量

语法：`env`

