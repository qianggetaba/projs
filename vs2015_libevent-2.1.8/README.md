# vs2015_libevent-2.1.8
vs2015 compile libevent-2.1.8 from source code, not nmae /f Makefile.nmake

打开libevent-2.1.8.sln
会看到解决方案下有三个项目，在项目上右键生成，会编译处对应的lib文件

注：如果编译报很多头文件未找到，是因为代码内的头文件是<>包含的，所以需要添加项目的路径到头文件搜索路径

项目上右键--属性--c/c++下--常规--右边的附加包含目录--点击后，再点击右边的向下按钮，编辑--新建--填入
$(MSBuildProjectDirectory)

这是项目当前目录宏，点击确定，重新生成