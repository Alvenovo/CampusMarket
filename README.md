# 🏫 校园二手交易平台 CampusMarket

> 南京理工大学紫金学院 · 移动应用开发工程实践大作业  
> Android 本地二手交易应用 | 纯 SQLite · 无需服务器

[![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Java-11-ED8B00?logo=openjdk)](https://www.oracle.com/java/)
[![Gradle](https://img.shields.io/badge/Gradle-9.2-02303A?logo=gradle)](https://gradle.org)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

---

## 📱 简介

校园二手交易平台是一款面向在校大学生的 Android 应用，提供闲置物品发布、浏览搜索、收藏、模拟交易等功能。采用纯本地 SQLite 数据库架构，无需服务器支持，断网也能正常使用。

## ✨ 功能

| 模块 | 功能 |
|------|------|
| 🔐 用户管理 | 注册、登录、退出登录、头像设置 |
| 🏠 商品浏览 | 首页列表、关键词搜索、下拉刷新 |
| 📝 商品发布 | 标题/价格/描述/联系方式、相册选图 |
| 🔍 商品详情 | 图片展示、发布者/浏览者差异化按钮 |
| ⭐ 收藏 | 收藏/取消收藏、收藏列表 |
| 📦 我的商品 | 查看已发布商品、编辑、删除 |
| 📋 交易记录 | 买入/卖出记录分类展示 |
| 🔔 后台通知 | 新商品上架系统通知 + Toast 提醒 |

## 🛠 技术栈

| 技术 | 用途 |
|------|------|
| **SQLite** (SQLiteOpenHelper 单例) | 本地数据存储 |
| **RecyclerView** + ViewHolder | 高性能列表 |
| **Glide 4.16** | 图片加载与缓存 |
| **SharedPreferences** | 登录态 & 配置存储 |
| **ObjectAnimator** | 启动页动画 |
| **NotificationManager** | 系统通知推送 |
| **Service** (CheckService) | 后台定时巡检 |
| **BroadcastReceiver** (NetworkReceiver) | 网络状态监听 |

## 📂 项目结构

```
app/src/main/java/com/example/campusmarket/
├── SplashActivity.java        # 启动页（动画）
├── LoginActivity.java         # 登录
├── RegisterActivity.java      # 注册
├── MainActivity.java          # 主界面（底部三面板导航）
├── DetailActivity.java        # 商品详情 & 模拟交易
├── PublishActivity.java       # 商品发布
├── EditGoodsActivity.java     # 编辑商品
├── FavoriteActivity.java      # 收藏列表
├── MyGoodsActivity.java       # 我的商品
├── DealRecordActivity.java    # 交易记录
├── MineActivity.java          # 个人中心
├── CheckService.java          # 后台巡检服务
├── NetworkReceiver.java       # 网络广播接收器
├── DBHelper.java              # 数据库（单例）
├── Goods.java                 # 商品实体类
├── DealRecord.java            # 交易记录实体类
├── GoodsAdapter.java          # 商品列表适配器
├── FavoriteAdapter.java       # 收藏适配器
└── DealRecordAdapter.java     # 交易记录适配器
```

## 🗄 数据库

4 张表，SQLite 本地存储：

| 表名 | 说明 |
|------|------|
| `user` | 用户表（username 唯一） |
| `goods` | 商品表（status: 0在售/1已售） |
| `favorite` | 收藏记录表 |
| `deal_records` | 交易记录表（type: 0买入/1卖出） |

## 🚀 运行

```bash
# 克隆项目
git clone https://github.com/Alvenovo/CampusMarket.git

# Android Studio 打开 → Sync → Run
```

要求：Android Studio + JDK 11 + Android SDK 24+

---

*2315302153 Alven · 2024*