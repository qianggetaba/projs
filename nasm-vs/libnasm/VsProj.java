import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 编辑vs 工程配置文件，批量添加文件到项目
 */
public class VsProj {
    
    public static void main(String[] args) {

        String objStr = "stdlib\\snprintf.$(O) stdlib\\vsnprintf.$(O) stdlib\\strlcpy.$(O) \\\n" +
                "\tstdlib\\strnlen.$(O) stdlib\\strrchrnul.$(O) \\\n" +
                "\t\\\n" +
                "\tnasmlib\\ver.$(O) \\\n" +
                "\tnasmlib\\crc64.$(O) nasmlib\\malloc.$(O) \\\n" +
                "\tnasmlib\\md5c.$(O) nasmlib\\string.$(O) \\\n" +
                "\tnasmlib\\file.$(O) nasmlib\\mmap.$(O) nasmlib\\ilog2.$(O) \\\n" +
                "\tnasmlib\\realpath.$(O) nasmlib\\path.$(O) \\\n" +
                "\tnasmlib\\filename.$(O) nasmlib\\srcfile.$(O) \\\n" +
                "\tnasmlib\\zerobuf.$(O) nasmlib\\readnum.$(O) nasmlib\\bsi.$(O) \\\n" +
                "\tnasmlib\\rbtree.$(O) nasmlib\\hashtbl.$(O) \\\n" +
                "\tnasmlib\\raa.$(O) nasmlib\\saa.$(O) \\\n" +
                "\tnasmlib\\strlist.$(O) \\\n" +
                "\tnasmlib\\perfhash.$(O) nasmlib\\badenum.$(O) \\\n" +
                "\t\\\n" +
                "\tcommon\\common.$(O) \\\n" +
                "\t\\\n" +
                "\tx86\\insnsa.$(O) x86\\insnsb.$(O) x86\\insnsd.$(O) x86\\insnsn.$(O) \\\n" +
                "\tx86\\regs.$(O) x86\\regvals.$(O) x86\\regflags.$(O) x86\\regdis.$(O) \\\n" +
                "\tx86\\disp8.$(O) x86\\iflag.$(O) \\\n" +
                "\t\\\n" +
                "\tasm\\error.$(O) \\\n" +
                "\tasm\\float.$(O) \\\n" +
                "\tasm\\directiv.$(O) asm\\directbl.$(O) \\\n" +
                "\tasm\\pragma.$(O) \\\n" +
                "\tasm\\assemble.$(O) asm\\labels.$(O) asm\\parser.$(O) \\\n" +
                "\tasm\\preproc.$(O) asm\\quote.$(O) asm\\pptok.$(O) \\\n" +
                "\tasm\\listing.$(O) asm\\eval.$(O) asm\\exprlib.$(O) asm\\exprdump.$(O) \\\n" +
                "\tasm\\stdscan.$(O) \\\n" +
                "\tasm\\strfunc.$(O) asm\\tokhash.$(O) \\\n" +
                "\tasm\\segalloc.$(O) \\\n" +
                "\tasm\\preproc-nop.$(O) \\\n" +
                "\tasm\\rdstrnum.$(O) \\\n" +
                "\t\\\n" +
                "\tmacros\\macros.$(O) \\\n" +
                "\t\\\n" +
                "\toutput\\outform.$(O) output\\outlib.$(O) output\\legacy.$(O) \\\n" +
                "\toutput\\strtbl.$(O) \\\n" +
                "\toutput\\nulldbg.$(O) output\\nullout.$(O) \\\n" +
                "\toutput\\outbin.$(O) output\\outaout.$(O) output\\outcoff.$(O) \\\n" +
                "\toutput\\outelf.$(O) \\\n" +
                "\toutput\\outobj.$(O) output\\outas86.$(O) output\\outrdf2.$(O) \\\n" +
                "\toutput\\outdbg.$(O) output\\outieee.$(O) output\\outmacho.$(O) \\\n" +
                "\toutput\\codeview.$(O) \\\n" +
                "\t\\\n" +
                "\tdisasm\\disasm.$(O) disasm\\sync.$(O)";

        String objStrClean = objStr.replace("\\\n","").replace("$(O)","c").replace("\n","");
        String files[] = Arrays.stream(objStrClean.split(" ")).map(String::trim).toArray(String[]::new);
        for(String file:files) {
            if (file.length() == 0) continue;
            //检查分隔是否正确
//            System.out.println(file);
        }

        //先, 生成目录
        String folderPre = "源文件\\nasm-2.14\\";
        Set<String> folderContainer = new HashSet<>(); // 目录去重
        for(String file:files) {
            String temp = "    <Filter Include=\"%s\">\n" +
                    "      <UniqueIdentifier>{%s}</UniqueIdentifier>\n" +
                    "    </Filter>";
            int dir = file.lastIndexOf("\\");
            String folderStr = folderPre + file.substring(0,dir);
            if (dir > 0 && !folderContainer.contains(folderStr)){
                folderContainer.add(folderStr);
                System.out.println(String.format(temp,folderPre + file.substring(0,dir), UUID.randomUUID().toString()));
            }
        }

        // 文件有两部分，一部分是.vcxproj.filters，另一部分是.vcxproj
        //第一部分
        System.out.println("---------------------------------------");
        String filePre   = "..\\nasm-2.14\\";
        for(String file:files) {
            String temp = "    <ClCompile Include=\"%s\">\n" +
                    "      <Filter>%s</Filter>\n" +
                    "    </ClCompile>";
            System.out.println(String.format(temp,filePre + file,folderPre + file.substring(0,file.lastIndexOf("\\"))));
        }
        //第二部分
        System.out.println("---------------------------------------");
        for(String file:files) {
            System.out.println(String.format("<ClCompile Include=\"%s\" />",filePre + file));
        }
    }
}
