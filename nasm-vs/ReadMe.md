

基于nasm 2.14源码，创建的vs项目

基于源码的 Mkfiles\msvc.mak 创建工程

rdf2com.exe,rdf2ith.exe,rdf2ihx.exe,rdf2srec.exe 都是rdf2bin.exe复制改名的，从msvc.mak看出来

rdf等等工具可以看 src/rdoff/README 介绍工具

nasm与ndisasm依赖 libnasm

其他rdf工具依赖 libnasm与librdoff

以下是项目建立过程：还需要看各个子项目下的ReadMe.txt

https://www.nasm.us/pub/nasm/releasebuilds/2.14/

https://www.nasm.us/pub/nasm/releasebuilds/2.14/nasm-2.14.tar.xz  源码
https://www.nasm.us/pub/nasm/releasebuilds/2.14/win64/nasm-2.14-installer-x64.exe  安装

新建vs解决方案，删除项目，新建libnasm静态库项目

解压nasm-2.14.tar.xz到项目根目录



