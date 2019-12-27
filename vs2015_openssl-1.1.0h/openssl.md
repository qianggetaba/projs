
https://www.openssl.org/source/openssl-1.0.2u.tar.gz

{src}/README-->  INSTALL.W64     Windows (64bit)-->  To build for Win64/x64

https://www.activestate.com/products/perl/downloads/   ActivePerl-5.28.1.0000-MSWin32-x64-432e1938.msi  选择typical然后默认安装
https://www.nasm.us/pub/nasm/releasebuilds/2.14.02/win64/   nasm-2.14.02-installer-x64.exe  管理员权限运行，默认安装, 添加到path，C:\Program Files\NASM

cmd test
perl -V
nasm --version

VS 2017 的 x64 本机工具命令提示
```
cd /d D:\openssl-1.0.2u
perl Configure VC-WIN64A --prefix=c:\openssl
ms\do_win64a
nmake -f ms\ntdll.mak
```

    out32dll 是最终文件
    tmp32dll 是obj中间文件

``nmake -f ms\ntdll.mak install``  -->  在{src}/INSTALL.W32  安装





