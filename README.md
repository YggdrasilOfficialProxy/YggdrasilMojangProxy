# MojangYggdrasilProxy
本项目通过启动一个自定义的Httpd服务器支持使用MC正版登录+外部Yggdrasil服务端登录,
需要配合 [Authlib-Injector](https://github.com/yushijinhun/authlib-injector)使用

## 警告
https://github.com/Karlatemp/YggdrasilMojangProxy/wiki/Home#%E8%AD%A6%E5%91%8A

## 获取
你可以从 [这里](https://github.com/Karlatemp/YggdrasilMojangProxy/blob/develop/dist/MojangYggdrasilProxy.jar) 获取

## 构建
本项目使用 [NetBeans](https://netbeans.org/) 制作, 下载后您需要导入Authlib-injector.jar

或者使用命令行构建.

依赖 [Ant](https://ant.apache.org/bindownload.cgi)

执行以下命令
```
ant -Dnb.internal.action.name=rebuild clean jar
```
构建输出于 `dist` 下

## 部署
> # 部署单个服务器
> 先部署 [Authlib-Injector](https://github.com/yushijinhun/authlib-injector#%E9%83%A8%E7%BD%B2)
> 
> 在修改成以下参数
> > 原来参数
> > ```
> > -javaagent:{authlib-injector.jar 的路径}={Yggdrasil 服务端的 URL(API Root)}
> > ```
>
> > 新参数
> > ```
> > -cp {authlib-injector.jar 的路径} -javaagent:{MojangYggdrasilProxy 的路径}={Yggdrasil 服务端的 URL(API Root)}
> > ````
>
> > 例子
> > ```
> > -cp libs/authlib-injector.jar -javaagent:libs/MojangYggdrasilProxy.jar=https://yggdrasil.example.cn
> > ```

> #部署多个服务器/部署Bungee群组
>
> 首先打开一个命令行窗口
>
> 执行以下命令
> ```
> java -jar {authlib-injector.jar 的路径} --port {一个没有被占用的端口} {Yggdrasil 服务端的 URL(API Root)}
> ```
> 然后会输出多个地址. 复制其中一个地址
> ```
> [MYP.core    INFO] API Root: https://yggdrasil.example.cn
> [MYP.httpd    INFO] Httpd proxy server run on port 21882 (这个是端口, 当没有使用 --port 定义端口的时候会随机取一个值, 不会固定)
> [MYP.core     INFO] Using proxy with
> [MYP.core     INFO] http://localhost:21882    (四选一)
> [MYP.core     INFO] http://127.0.0.1:21882
> [MYP.core     INFO] http://192.168.1.103:21882    (192.168.1.103 为我内网ip)
> [MYP.core     INFO] http://DESKTOP-MPOB4BV:21882  (DESKTOP-DESKTOP-MPOB4BV 是我计算机的名字, 正常情况不推荐使用)
> [MYP.core     INFO] http://DESKTOP-MPOB4BV:21882
> ```
> ([localhost 百度百科](https://baike.baidu.com/item/localhost/2608730?fr=aladdin))
> ([127.0.0.1 百度百科](https://baike.baidu.com/item/127.0.0.1/4563698?fr=aladdin))
> 
> 然后把每个服务器的 {Yggdrasil 服务端的 URL(API Root)} 改成输出的地址

> # 部署 [Minecraft通行证](https://login2.nide8.com:233/account/index)
> 首先获取你的 [通行证服务器ID](https://github.com/Karlatemp/YggdrasilMojangProxy/wiki/%E8%8E%B7%E5%8F%96Minecraft%E9%80%9A%E8%A1%8C%E8%AF%81ID)
>
> 然后按照上面的部署方法执行.
>
> 注意: Yggdrasil 服务端的 URL(API Root) 为 https://auth2.nide8.com:233/{通行证服务器ID} (例如 https://auth2.nide8.com:233/1234567890abcdef1234567890abcdef/)
>
> 并删掉 `-javaagent:nide8auth.jar=....`
>
> 例如
> ```
> java -cp libs/authlib-injector.jar -javaagent:libs/MojangYggdrasilProxy.jar=https://auth2.nide8.com:233/1234567890abcdef1234567890abcdef/ -jar minecraft_server.jar
> ```