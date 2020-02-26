# 项目已更新.
代替项目: [YggdrasilOffcialProxy](https://github.com/Karlatemp/YggdrasilOfficialProxy.git)


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
java -javaagent:MojangYggdrasil.jar=https://yggdrasil.example.com/ -javaagent:authlib-injector.jar=https://yggdrasil.example.com/ -jar spigot-1.14.4.jar

#这是统一通信证用户
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
对呀,只需要修改BungeeCord就行了
```

## 正版用户显示伪正版皮肤
首先,你需要获取[修改器](https://github.com/Karlatemp/YggdrasilMojangProxy/blob/master/out/artifacts/SkinViewer_jar/SkinViewer.jar)
,并放在一个你认为友好的地方(比如 `F:\Java\ `) 并记住这个jar的完整路径

(如果你把下载的jar放到了 `F:\Java`, 并且 jar 的名字是 `SkinViewer.jar`, 那么这个jar的完整路径就是 `F:\Java\SkinViewer.jar`)

然后打开你的启动器配置页面并找到JVM选项

![启动器配置](https://github.com/Karlatemp/YggdrasilMojangProxy/blob/master/imgs/0.png?raw=true)

![启动器配置2](https://github.com/Karlatemp/YggdrasilMojangProxy/blob/master/imgs/1.png?raw=true)

在JVM选项的末尾加上这段参数(不要忘记空格)
```
[空格] -javaagent:{你下载的jar的完整路径}
#这是示例
 -javaagent:F:\Java\SkinViewer.jar
```
如果你的完整路径是有空格的(比如`F:\Java Tools\SkinViewer.jar`)
那么请使用下面的这段参数
```
[空格] "-javaagent:{路径}"
#示例
"-javaagent:F:\Java Tools\SkinViewer.jar"
```

最好不要尝试使用相对位置,因为我无法确定文件应该放在哪,可能每个启动器都不一样?
如果相对位置是可以用的请找Karlatemp修改
