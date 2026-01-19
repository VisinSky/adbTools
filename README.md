# AdbTools

[![License: GPL v3.0](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple.svg)](https://kotlinlang.org)

这个项目起源是工作经常需要adb 抓日志、看性能等等操作，周六周末很多时候又不喜欢带电脑，于是我就问ai能不能做个app，ai给我推荐了kadb，dadb，最终发现kadb 符合我的要求，很多机器只有adb 配对。于是我就基于kadb 封装了一套apk界面
这个只是最大满足我个人工作需求，如果你看到了这个项目，有需要的话，可以自行添加自己的功能。请勿用于非法用途。
AdbTools 是一个基于 Kotlin 开发的高性能 Android 调试桥（ADB）的android app。它基于 [Kadb](https://github.com/flyfishxu/kadb) 构建，提供了比较方便的界面操作
注意这个项目是ai做的，总共耗时不记得了，都是每天抽空做一做，如果你喜欢对你有用请star 下，如果发现代码有问题，崩溃卡顿，适配等问题请提交issues。目前只适配了我手上有的机器，其他的机器界面适配需要自行适配了。
## ✨ 功能特性 (Features)
| 模块 | 功能详情 |
| :--- | :--- |
| **🔌 连接** | ADB 连接 (USB/WiFi)、Android 11+ 无线配对码连接 |
| **📱 设备** | 硬件配置查看、电池状态监控、存储空间分析 |
| **📦 应用** | 安装/卸载、**应用提取**、应用冻结/解冻、清除数据 |
| **📂 文件** | 内部存储浏览、文件高速上传/下载 |
| **🚀 进程** | 实时内存监控、结束进程 |
| **🎮 遥控** | 模拟物理按键、虚拟遥控器、**文字输入** |
| **🛠️ 工具** | 截图/录屏、设备重启、**设备镜像投屏（目前还有问题）**、获取顶层窗口、性能监控、日志抓取 |
| **💻 Shell** | 内置终端执行自定义命令 (Ping 等) *[如有问题请反馈]* |



特别鸣谢 本项目的顺利开发离不开开源社区的支持。特别感谢 flyfishxu 提供的优秀开源项目[Kadb](https://github.com/flyfishxu/kadb) 高质量的代码极大地简化了 ADB 协议的实现过程，为本项目奠定了坚实的基础。
### 演示图片
## 📸 界面预览 (Screenshots)

| **1** | **2** |
| :---: | :---: |
| <img src="https://github.com/davikl/adbTools/blob/main/images/微信图片_20260118194208_28_491.jpg" width="300"/> | <img src="https://github.com/davikl/adbTools/blob/main/images/微信图片_20260118194216_35_491.jpg" width="300"/> |


| **3** | **4** |
| :---: | :---: |
| <img src="https://github.com/davikl/adbTools/blob/main/images/微信图片_20260118194216_36_491.jpg" width="300"/> | <img src="https://github.com/davikl/adbTools/blob/main/images/微信图片_20260118194218_38_491.jpg" width="300"/> |


| **5** | **6** |
| :---: | :---: |
| <img src="https://github.com/davikl/adbTools/blob/main/images/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20260118194220_40_491.jpg" width="300"/> | <img src="https://github.com/davikl/adbTools/blob/main/images/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20260118194523_6797_1.png" width="300"/> |


| **7** | **8** |
| :---: | :---: |
| <img src="https://github.com/davikl/adbTools/blob/main/images/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20260118194950_6801_1.jpg" width="300"/> | <img src="https://github.com/davikl/adbTools/blob/main/images/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20260118195644_41_491.png" width="300"/> |

| **9** | **10** |
| :---: | :---: |
| <img src="https://github.com/davikl/adbTools/blob/main/images/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20260118200330_6806_1.jpg" width="300"/> | <img src="https://github.com/davikl/adbTools/blob/main/images/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20260118200521_6808_1.png" width="300"/> |

