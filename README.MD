# MojangYggdrasil
本项目通过拦截http/https请求实现支持使用MC正版登录+外部Yggdrasil服务端登录,
可配合任何支持 [yggdrasil](https://github.com/yushijinhun/authlib-injector/wiki/Yggdrasil%E6%9C%8D%E5%8A%A1%E7%AB%AF%E6%8A%80%E6%9C%AF%E8%A7%84%E8%8C%83) 的插件使用

个人推荐： [Authlib-Injector](https://github.com/yushijinhun/authlib-injector)

---
## 测试环境
```
java version "1.8.0_121"
Java(TM) SE Runtime Environment (build 1.8.0_121-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.121-b13, mixed mode)
```
---

## 获取
你可以从 [这里](https://github.com/Karlatemp/YggdrasilMojangProxy/blob/master/out/artifacts/MojangYggdrasil/MojangYggdrasil.jar) 获取
---

## 部署
我们只需要为jvm启动参数加上一点点东西就ok
```
-javaagent:{下载的jar路径}={Yggdrasil Root}
# 这是示例
java -javaagent:MojangYggdrasil.jar=https://auth2.nide8.com:233/吧啦吧啦 -javaagent:nide8auth.jar=吧啦吧啦 -jar spigot-1.14.4.jar
```
### 附加参数
```
-Dmojangyggdrasil.offline=true
        #这个废了,测试盗版直接无法进入,连网络事件都没有触发
        #不过其他认证服务器的玩家可以加入??? 谁晓得呢
-Dmojangyggdrasil.authme=true
        #启动AuthMe无缝加入, 不过这个应该也没用了


-D.....   -javaagent:.....
```

### 我是统一通行证用户,我不知道Yggdrasil Root是啥玩意
```
https://auth2.nide8.com:233/{服务器ID}/
```
### 我是BungeeCord用户,我是不是只需要在修改 BC 的就行了
```
那是不可能的, 每个服务器都要加
```