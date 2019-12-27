========================================================================
    控制台应用程序：helloword 项目概述
========================================================================

需要relase版本的libevent库 libevent_core.lib libevent_extras.lib

项目右键--VC++目录--包含目录--$(SolutionDir).\libeventBase\include   库目录--$(SolutionDir).\libeventBase\lib

项目右键--链接器--输入--ws2_32.lib;wsock32.lib;libevent_extras.lib;libevent_core.lib

运行程序后，xshell telnet 127.0.0.1 9995 显示Hello, World!，程序控制台显示flushed answer 连接断开

