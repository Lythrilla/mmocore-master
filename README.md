# MMOCore - Lythrilla修复版

什么是MMOCore: [Spigot](https://www.spigotmc.org/resources/mmocore.70575/)

本版本并非官方版本也不是授权版本，可能存在版权问题。本版本旨在修复 MMOCore对Chemdah的兼容。

## 我做了什么

新增了对[Chemdah](https://github.com/TabooLib/chemdah)的支持，在对话时阻止了 MMOCore 屎一样的监听器，防止在对话中切换副手键和数字键失灵

修了这个啥呗一样的 MMOCore 构建，累死我了，修了好几个小时，存储库好多都404了

## 有些话

我不鼓励大家使用修改版本，因为我并没有获得授权，仅仅是为了贡献社区。

还是希望大家可以去支持原作者，下面是原作者的链接

## 原作的链接

- 购买插件: [Spigot](https://www.spigotmc.org/resources/mmocore.70575/)
- 开发版本: [PhoenixDev](https://phoenixdevt.fr/devbuilds)
- 官方文档: [Wiki](https://gitlab.com/phoenix-dvpmt/mmocore/-/wikis/home)
- Discord支持: [Discord](https://phoenixdevt.fr/discord)
- 其他插件: [Indyuce插件](https://www.spigotmc.org/resources/authors/indyuce.253965/)

## MMOCore 官方的API

注册PhoenixDevelopment公共仓库:

```xml
<repository>
    <id>phoenix</id>
    <url>https://nexus.phoenixdevt.fr/repository/maven-public/</url>
</repository>
```

添加MythicLib和MMOCore-API作为依赖:

```xml
<dependency>
    <groupId>io.lumine</groupId>
    <artifactId>MythicLib-dist</artifactId>
    <version>1.6.2-SNAPSHOT</version>
    <scope>provided</scope>
    <optional>true</optional>
</dependency>

<dependency>
    <groupId>net.Indyuce</groupId>
    <artifactId>MMOCore-API</artifactId>
    <version>1.13.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```
## 开发者

- 原作者: Indyuce
- 二次开发: Lythrilla
