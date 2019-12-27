
基于nasm 2.14源码，创建的vs项目

基于源码的 Mkfiles\msvc.mak 创建工程

rdf2com.exe,rdf2ith.exe,rdf2ihx.exe,rdf2srec.exe 都是rdf2bin.exe复制改名的，从msvc.mak看出来

rdf等等工具可以看 src/rdoff/README 介绍工具

nasm与ndisasm依赖 libnasm

其他rdf工具依赖 libnasm与librdoff