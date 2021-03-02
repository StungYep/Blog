## 博客系统

## 工具

## 前端
通过 BootStrap 编写前端 css, html, javascript等。其参考网站为：[bootstrap](https://v3.bootcss.com/components/#navbar)

github 部署仓库：[github](https://github.com/StungYep/Blog)



## 登录
通过 OAuth2 授权 github 实现登录, 帮助文档：[github](https://docs.github.com/en/developers/apps/authorizing-oauth-apps)

okhttp官网：[okhttp](https://square.github.io/okhttp/)

OAuth 授权流程如下：

![1613660023562](C:\Users\StungYep\AppData\Roaming\Typora\typora-user-images\1613660023562.png)

1.  A 网站提供一个链接， 用户点击链接会进入一个授权界面，向资源所有者 B 网站发送授权请求。
2.  B 网站如果同意授权，就会传回一个授权码 code， 并跳转至 $$redirect\_url$$  中。
3.  A 网站拿到授权码后，就在后端，向认证服务器 B 网站请求令牌。在请求的时候回携带 $$client\_id\ 和\  client\_secret$$  ，用于认证服务器验证 A 的身份。
4.  B 网站收到请求后会发放令牌，即向 $$redirect\_url$$ 中发送一段 $$json$$ 数据，其中会有 $$access\_token$$ 即令牌和其他的 json 格式的用户信息，这里可以用 $$fastjson$$ 将其转换为 java 对象。
5.  A 网站拿到令牌后就可以实现登录功能。（token 会定时刷新，所以还需要验证 token 的有效性）。

mybatis-generator命令：mvn -Dmybatis.generator.overwrite=true mybatis-generator:generate

目前的问题：出错时候的图片 + 页面左上角的发布图标的问题
