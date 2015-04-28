package com.liaoyu.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.liaou.po.RecordField;

/**
 * @author  liaoyu
 * @created Apr 28, 2015
 */
public class GenerateHtml {

    public static void process(File file, String targetFileName) {

        ResourceFactory processor = ResourceFactory.getSington();
        Map<String, List<RecordField>> configCache = processor.getConfigCache(targetFileName);
        List<RecordField> fields = null;

        try {

            String filePath = file.getAbsolutePath();
            File destFile = new File(filePath + ".html");

            if (!destFile.isFile()) {
                destFile.createNewFile();
            }

            FileInputStream inStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

            FileOutputStream outStream = new FileOutputStream(destFile);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));

            writer.write(appendHeader());

            String line;
            while ((line = reader.readLine()) != null) {

                if ("".equals(line.trim())) {
                    continue;
                }

                try {
                    String identify = line.substring(0, 2);

                    if (configCache.containsKey(identify)) {
                        fields = configCache.get(identify);

                        writer.write(buildOutputStr(line, fields));

                    } else {
                        writer.write("Skip Record : " + line + "<br /><br />");
                    }

                } catch (Exception e) {
                    writer.write("Parse Error : " + line + "<br /><br />");
                }

            }

            writer.write(appendFooter());

            writer.flush();
            writer.close();
            outStream.close();

            reader.close();
            inStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String appendHeader() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<!DOCTYPE html>");
        buffer.append("<html lang=\"en\">");
        buffer.append("<head>");
        buffer.append("    <meta charset=\"UTF-8\">");
        buffer.append("    <title>Document</title>");
        buffer.append("<style type=\"text/css\">");
        buffer.append("table {");
        buffer.append("    font-family: verdana,arial,sans-serif;");
        buffer.append("    font-size:11px;");
        buffer.append("    color:#333333;");
        buffer.append("    margin-bottom:10px;");
        buffer.append("    border-width: 1px;");
        buffer.append("    border-color: #666666;");
        buffer.append("    border-collapse: collapse;");
        buffer.append("}");
        buffer.append("th {");
        buffer.append("    border-width: 1px;");
        buffer.append("    padding: 8px;");
        buffer.append("    border-style: solid;");
        buffer.append("    border-color: #666666;");
        buffer.append("    background-color: #dedede;");
        buffer.append("}");
        buffer.append("td {");
        buffer.append("    border-width: 1px;");
        buffer.append("    padding: 8px;");
        buffer.append("    border-style: solid;");
        buffer.append("    border-color: #666666;");
        buffer.append("    background-color: #ffffff;");
        buffer.append("}");
        buffer.append("</style>");
        buffer.append("</head>");
        buffer.append("<body>");

        return buffer.toString();

    }

    private static String appendFooter() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("</body>");
        buffer.append("</html>");

        return buffer.toString();

    }

    private static String buildOutputStr(String line, List<RecordField> fields) throws Exception {

        StringBuffer str = new StringBuffer("");
        int pos = 0;
        List<String> titleList = new ArrayList<String>();
        List<String> contentList = new ArrayList<String>();

        try {
            for (RecordField field : fields) {
                String title = field.getTitle();
                int length = field.getLength();
                String content = line.substring(pos, pos + length);

                titleList.add(title);
                contentList.add(content);

                pos += length;
            }

        } catch (Exception e) {
            throw new Exception();
        }

        str.append("<table>");
        str.append("<tr>");

        for (String tmp : titleList) {
            str.append("<th>" + tmp + "</th>");
        }

        str.append("</tr>");
        str.append("<tr>");

        for (String tmp : contentList) {
            str.append("<td>" + tmp + "</td>");
        }

        str.append("</tr>");
        str.append("</table>");

        return str.toString();
    }
}
