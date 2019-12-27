

https://github.com/libevent/libevent/releases/download/release-2.1.11-stable/libevent-2.1.11-stable.tar.gz

read {src}/README.md--"CMake (Windows)"

https://github.com/Kitware/CMake/releases/download/v3.16.2/cmake-3.16.2-win64-x64.msi
python-3.7.4-amd64.exe  install custom location, check add path, for all user,C:\Python37
python -V  # cmd test
http://doxygen.nl/files/doxygen-1.8.16-setup.exe
doxygen --version

https://raw.githubusercontent.com/libevent/libevent/release-2.1.11-stable/cmake/Uninstall.cmake.in  下载的源码包缺文件

VS 2017 的 x64 本机工具命令提示
cd /d D:\libevent-2.1.11-stable
mkdir build && cd build
//cmake -DOPENSSL_ROOT=c:\openssl -G "Visual Studio 15" D:\libevent-2.1.11-stable  # cmake --help可以看到前面有*的vs编译器，就用那个数字

cmake -DEVENT__DISABLE_OPENSSL=ON -G "Visual Studio 15" D:\libevent-2.1.11-stable
start libevent.sln

cmake直接生成了一个解决方案，下面有很多小项目，比如"hello-world"，这些小项目都引用了源码根目录的CMakeLists.txt，编译任意小项目会先生成event_core.lib event_extra.lib与对于dll，然后编译小项目