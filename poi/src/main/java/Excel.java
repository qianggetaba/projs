//单文件示例代码，转换结果在List<List<String>> container

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Excel {

    public static void main(String[] args) throws Exception {
        File test = new File(".");
        String file = test.getAbsolutePath() + "/src/main/resources/test.xlsx";

        OPCPackage pkg = OPCPackage.open(file);
        XSSFReader r = new XSSFReader(pkg);
        InputStream is = r.getSheet("rId1");
        //debug 查看转换的xml原始文件，方便理解后面解析时的处理,
        byte[] isBytes = IOUtils.toByteArray(is);
        Excel.streamOut(new ByteArrayInputStream(isBytes));

        //下面是SST 的索引会用到的
        SharedStringsTable sst = r.getSharedStringsTable();
        System.out.println("excel的共享字符表sst------------------start");
        sst.writeTo(System.out);
        System.out.println();
        System.out.println("excel的共享字符表sst------------------end");

        XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        List<List<String>> container = new ArrayList<>();
        parser.setContentHandler(new Myhandler(sst, container));

        InputSource inputSource = new InputSource(new ByteArrayInputStream(isBytes));
        parser.parse(inputSource);

        is.close();

        Excel.printContainer(container);
    }

    public static void printContainer(List<List<String>> container) {
        System.out.println("excel内容------------- -start");
        for (List<String> stringList : container) {
            for (String str : stringList) {
                System.out.printf("%20s", str + " | ");
            }
            System.out.println();
        }
        System.out.println("excel内容---------------end");
    }

    //读取流，查看文件内容
    public static void streamOut(InputStream in) throws Exception {
        System.out.println("excel转为xml------------start");
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) != -1) {
            System.out.write(buf, 0, len);
        }
        System.out.println();
        System.out.println("excel转为xml------------end");
    }


}

class Myhandler extends DefaultHandler {


    //取SST 的索引对应的值
    private SharedStringsTable sst;

    public void setSst(SharedStringsTable sst) {
        this.sst = sst;
    }

    //解析结果保存
    private List<List<String>> container;

    public Myhandler(SharedStringsTable sst, List<List<String>> container) {
        this.sst = sst;
        this.container = container;
    }

    /**
     * 存储cell标签下v标签包裹的字符文本内容
     * 在v标签开始后，解析器自动调用characters()保存到 lastContents
     * 【但】当cell标签的属性 s是 t时, 表示取到的lastContents是 SharedStringsTable 的index值
     * 需要在v标签结束时根据 index(lastContents)获取一次真正的值
     */
    private String lastContents;

    //有效数据矩形区域,A1:Y2
    private String dimension;

    //根据dimension得出每行的数据长度
    private int longest;

    //上个有内容的单元格id，判断空单元格
    private String lastCellid;

    //上一行id, 判断空行
    private String lastRowid;

    // 判断单元格cell的c标签下是否有v，否则可能数据错位
    private boolean hasV = false;


    //行数据保存
    private List<String> currentRow;

    //单元格内容是SST 的索引
    private boolean isSSTIndex = false;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//        System.out.println("startElement:"+qName);

        lastContents = "";
        if (qName.equals("dimension")) {
            dimension = attributes.getValue("ref");
            longest = covertRowIdtoInt(dimension.substring(dimension.indexOf(":") + 1));
        }
        //行开始
        if (qName.equals("row")) {
            String rowNum = attributes.getValue("r");
            //判断空行
            if (lastRowid != null) {
                //与上一行相差2, 说明中间有空行
                int gap = Integer.parseInt(rowNum) - Integer.parseInt(lastRowid);
                if (gap > 1) {
                    gap -= 1;
                    while (gap > 0) {
                        container.add(new ArrayList<>());
                        gap--;
                    }
                }
            }

            lastRowid = attributes.getValue("r");
            currentRow = new ArrayList<>();
        }
        if (qName.equals("c")) {
            String rowId = attributes.getValue("r");

            //空单元判断，添加空字符到list
            if (lastCellid != null) {
                int gap = covertRowIdtoInt(rowId) - covertRowIdtoInt(lastCellid);
                for (int i = 0; i < gap - 1; i++) {
                    currentRow.add("");
                }
            } else {
                //第一个单元格可能不是在第一列
                if (!"A1".equals(rowId)) {
                    for (int i = 0; i < covertRowIdtoInt(rowId) - 1; i++) {
                        currentRow.add("");
                    }
                }
            }
            lastCellid = rowId;


            //判断单元格的值是SST 的索引，不能直接characters方法取值
            if (attributes.getValue("t") != null && attributes.getValue("t").equals("s")) {
                isSSTIndex = true;
            } else {
                isSSTIndex = false;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
//        System.out.println("endElement:"+qName);

        //行结束,存储一行数据
        if (qName.equals("row")) {

            //判断最后一个单元格是否在最后，补齐列数
            //【注意】有的单元格只修改单元格格式，而没有内容，会出现c标签下没有v标签，导致currentRow少
            if (covertRowIdtoInt(lastCellid) < longest) {
                int min = Math.min(currentRow.size(), covertRowIdtoInt(lastCellid));
                for (int i = 0; i < longest - min; i++) {
                    currentRow.add("");
                }
            }

            container.add(currentRow);
            lastCellid = null;
        }

        //单元格结束，没有v时需要补位
        if (qName.equals("c")){
            if (!hasV) currentRow.add("");
            hasV = false;
        }

        //单元格内容标签结束，characters方法会被调用处理内容
        if (qName.equals("v")) {
            hasV = true;
            //单元格的值是SST 的索引
            if (isSSTIndex) {
                String sstIndex = lastContents.toString();
                try {
                    int idx = Integer.parseInt(sstIndex);
                    XSSFRichTextString rtss = new XSSFRichTextString(
                            sst.getEntryAt(idx));
                    lastContents = rtss.toString();
                    currentRow.add(lastContents);
                } catch (NumberFormatException ex) {
                    System.out.println(lastContents);
                }
            } else {
                currentRow.add(lastContents);
            }

        }

    }


    /**
     * 获取element的文本数据
     *
     * @see org.xml.sax.ContentHandler#characters
     */
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        lastContents += new String(ch, start, length);
    }

    /**
     * 列号转数字   AB7-->28 第28列
     *
     * @param cellId 单元格定位id，行列号，AB7
     * @return
     */
    public static int covertRowIdtoInt(String cellId) {
        StringBuilder sb = new StringBuilder();
        String column = "";
        //从cellId中提取列号
        for(char c:cellId.toCharArray()){
            if (Character.isAlphabetic(c)){
                sb.append(c);
            }else{
                column = sb.toString();
            }
        }
        //列号字符转数字
        int result = 0;
        for (char c : column.toCharArray()) {
            result = result * 26 + (c - 'A') + 1;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(Myhandler.covertRowIdtoInt("AB7"));

    }
}
