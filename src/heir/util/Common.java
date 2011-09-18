package heir.util;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Common utilities used by Parser & Writer
 * 
 * @author Nijikokun
 */
public class Common {
    public void extract(String from, File destFolder, String dest) {
        File actual = new File(destFolder, dest);
        if (!actual.exists()) {
            InputStream input = this.getClass().getResourceAsStream("/" + from);
            if (input != null) {
                FileOutputStream output = null;

                try {
                    output = new FileOutputStream(actual);
                    byte[] buf = new byte[8192];
                    int length = 0;

                    while ((length = input.read(buf)) > 0) {
                        output.write(buf, 0, length);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (input != null) {
                            input.close();
                        }
                    } catch (Exception e) { }
                    try {
                        if (output != null) {
                            output.close();
                        }
                    } catch (Exception e) { }
                }
            }
        }
    }

    public List<String> read(String s) {
        InputStream is = null;
        BufferedReader br = null;
        String line;
        ArrayList<String> list = new ArrayList<String>();

        try {
            is = this.getClass().getResourceAsStream("/" + s);
            br = new BufferedReader(new InputStreamReader(is));

            while (null != (line = br.readLine())) {
                list.add(line);
            }
        } catch (Exception e) {
            System.out.println("File Error: " + e);
        } finally {
            try {
                if (br != null)
                    br.close();

                if (is != null)
                    is.close();
            } catch (IOException e) {
                System.out.println("Couldn't close file connection: " + e);
            }
        }

        return list;
    }

    public List<String> readFile(String s) {
        InputStream is = null;
        BufferedReader br = null;
        String line;
        ArrayList<String> list = new ArrayList<String>();

        try {
            br = new BufferedReader(new FileReader(new File(s)));

            while (null != (line = br.readLine())) {
                list.add(line);
            }
        } catch (Exception e) {
            System.out.println("File Error: " + e);
        } finally {
            try {
                if (br != null)
                    br.close();

                if (is != null)
                    is.close();
            } catch (IOException e) {
                System.out.println("Couldn't close file connection: " + e);
            }
        }

        return list;
    }

    public int getUnixTimestamp() {
        return (int) (System.currentTimeMillis() / 1000L);
    }

    public String getOsName() {
        return System.getProperty("os.name");
    }

    public boolean isWindows() {
        return getOsName().startsWith("Windows");
    }

    public boolean isUnix() {
        return (!getOsName().startsWith("Windows"));
    }

    public static String[] trim(String[] values) {
        for (int i = 0, length = values.length; i < length; i++)
            if (values[i] != null)
                values[i] = values[i].trim();

        return values;
    }

    public static List trim(List values) {
        List trimmed = new ArrayList();

        for (int i = 0, length = values.size(); i < length; i++) {
            String v = (String) values.get(i);

            if (v != null) v = v.trim();

            trimmed.add(v);
        }
        
        return trimmed;
    }

    public static Set getUniqueTokens(String str, String separator) {
        String[] tmpStr = str.split(separator);
        return new HashSet(Arrays.asList(tmpStr));
    }

    public static String unQuote(String s) {
        s = (s != null && ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))) ? s.substring(1, s.length() - 1) : s;
        s = (s != null && ((s.startsWith("\"") || s.startsWith("'")))) ? s.substring(1, s.length()) : s;
        s = (s != null && ((s.endsWith("\"") || s.endsWith("'")))) ? s.substring(0, s.length()-1) : s;
        return s;
    }

    public static String trimAfter(String line, String delim) {
        if(!line.contains(delim)) return line;
        
        int count = line.split(delim).length;
        int index = 0;
        boolean found = false;

        for(int i = 0; i < count-1; i++) {
            index = line.indexOf(delim, i);

            if(line.charAt(index-1) == '\\'){
                line = line.replace("\\" + delim, delim);
                continue;
            }

            found = true; break;
        }

        return (found) ? line.substring(0, index) : line;
    }
}
