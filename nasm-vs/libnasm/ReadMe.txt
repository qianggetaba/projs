
nams 2.14  libnasm.lib 静态库

Mkfiles\msvc.mak 根据$(NASMLIB): $(LIBOBJ)加源码

VsProj.java 用于处理后，手动修改vs工程文件 libnasm.vcxproj  libnasm.vcxproj.filters

VC++目录--包含目录：
$(solutiondir)src
$(solutiondir)src\include
$(solutiondir)src\asm
$(solutiondir)src\x86
$(solutiondir)src\output

vs2017 需要关闭
c/c++ --常规--SDL否