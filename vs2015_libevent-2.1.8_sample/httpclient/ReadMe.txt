========================================================================
    控制台应用程序：httpclient 项目概述
========================================================================

需要libevent_openssl.lib

1. 链接器--输入--附加依赖项
ws2_32.lib
wsock32.lib
libevent_extras.lib
libevent_core.lib
libevent_openssl.lib
$(MSBuildProjectDirectory)\libcrypto-1_1-x64.lib
$(MSBuildProjectDirectory)\libssl-1_1-x64.lib

2. vc++目录--包含目录
(MSBuildProjectDirectory)
$(SolutionDir).\libeventBase\include

3. vc++目录--库文件目录
$(SolutionDir).\libeventBase\lib


httpclient.exe" -url "http://www.baidu.com/"  https网站还有问题
