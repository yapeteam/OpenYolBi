<!--suppress HtmlDeprecatedAttribute -->
<p align="center">
  <img src="https://avatars.githubusercontent.com/u/159465859?s=64&v=4" alt="YolBi Lite" img>
</p>

为支持Minecraft开源社区 ~~(打死妖猫的圈钱梦想)~~ ，我决定开放YolBi Lite Legacy分支

为防止二次特征，loader模块不予公开

此仓库将长期维护，欢迎各位加入开发

# YolBi Lite Legacy

***Powered by Yape Development Team***

使用YolBi Lite Legacy的源代码，你必须遵守位于仓库根目录下的`YOLBI_LICENSE.md`

# Join Us

QQ Group: 699481681

## TODO List

- [ ] 1.18.1 bypass for `布吉岛`
- [ ] 更加美丽的视觉
- [ ] 注入器重构
- [ ] 能打HVH😡
- [ ] 沟槽的自动构建(为什么你的builder要写在src里面)

## Development 如何构建

准备工具 [IntelliJ IDEA](https://www.jetbrains.com/idea/)

下载 [IntelliJ IDEA](https://www.jetbrains.com/idea/)

双击下载的exe文件进行安装(不会的CSDN自己找)

安装完成后 点击github此仓库页面右上角的Fork按钮(要求已经登录github账号)

Fork完成后 进入你自己Fork的仓库界面

在Idea里面 找到“从版本控制中获取”

输入你Fork的仓库URL

导入项目

等待索引编译完成

(无需mingw编译器，因为loader二进制文件已包含在源码中)

(如果报错 尝试额外安装Java22)

(有问题建议先Google)

### Debug Build

Run `DEBUG BUILD`

### Release Build

Run `RELEASE BUILD`

### Build

1. Open this project

2. Run `DEBUG BUILD` or `RELEASE BUILD`

3. Your jar will appear in `./build`

- Cache Dir is `<userhome>/.yolbi`

## Custom

For custom build, please edit `YBuild.xml`

# Dependencies

[Reflective DLL injection](https://github.com/stephenfewer/ReflectiveDLLInjection)

[FlatLaf](https://github.com/JFormDesigner/FlatLaf)

[ASM](https://gitlab.ow2.org/asm/asm)

<hr>
