========================================================================
    控制台应用程序：liebeventdemo 项目概述
========================================================================

基于libevent-2.1.8_demo的示例项目，不完整
可以看看每个项目的ReadMe说明

libeventBase内存放编译的relase版本的libevent库 libevent_core.lib libevent_extras.lib 每个demo都需要

设置include包含目录与lib库文件目录
项目右键--VC++目录--包含目录--$(SolutionDir).\libeventBase\include   库目录--$(SolutionDir).\libeventBase\lib

链接器需要输入的库文件名称
项目右键--链接器--输入--ws2_32.lib;wsock32.lib;libevent_extras.lib;libevent_core.lib

注意编译libevent库的位数，x86,x64与项目的demo一致

windows不支持event-read-fifo demo