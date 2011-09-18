package heir.arch;

import heir.handlers.Parser.Logic;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Group {
    private String name;
    private int weight;
    private boolean base;
    private ArrayList<Node> Nodes = new ArrayList<Node>();
    private ArrayList<String> Inheritance = new ArrayList<String>();
    LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();

    public Group() {
        this.name = null;
    }

    public Group(String name) {
        this.name = trimWeight(name);
    }

    public Group(String name, boolean base) {
        this.name = name;
        this.base = base;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Node> getNodes() {
        return Nodes;
    }

    public ArrayList<String> getInheritance() {
        return Inheritance;
    }

    public LinkedHashMap<String, String> getData() {
        return data;
    }

    public Node getNode(String node) {
        if(!hasNode(node))
            return null;

        for(Node n: Nodes)
            if(n.getName().equalsIgnoreCase(node))
                return n;

        return null;
    }

    public boolean hasNode(String node) {
        for(Node n: Nodes)
            if(n.getName().equalsIgnoreCase(node))
                return true;

        return false;
    }

    public boolean hasInheritance() {
        return !this.Inheritance.isEmpty();
    }

    public boolean isDefault() {
        return base;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addNodes(ArrayList<Node> Nodes) {
        this.Nodes = Nodes;
    }

    public void addNode(Node node) {
        this.Nodes.add(node);
    }

    public void addInhertance(String group) {
        this.Inheritance.add(group);
    }

    public void setDefault(boolean base) {
        this.base = base;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
    
    public boolean hasKey(String key) {
        return this.data.containsKey(key);
    }

    public boolean hasValue(String value) {
        return this.data.containsValue(value);
    }

    public String getValue(String key) {
        return this.data.get(key);
    }

    public void addEntry(String key, String value) {
        this.data.put(key, value);
    }

    public void addEntries(LinkedHashMap<String, String> data) {
        for(String k: data.keySet())
            addEntry(k, data.get(k));
    }

    private static class Dictionary {
        static boolean isInteger(String value) {
          try  {
            int d = Integer.parseInt(value);
          }  catch(NumberFormatException E) {
            return false;
          }

          return true;
        }

        static boolean isDouble(String value) {
          try  {
            double d = Double.parseDouble(value);
          }  catch(NumberFormatException E) {
            return false;
          }

          return true;
        }

        static boolean isBoolean(String value) {
          if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
              return true;

          return false;
        }
    }

    @Override
    public String toString() {
        ArrayList<String> block = new ArrayList<String>();

        String line = "";
        int x = 0;

        if(isDefault())
            line += getName() + Logic.WEIGHT_SEPERATOR[0] + Logic.DEFAULT[0];
        else
            line += getName();

        if(hasInheritance())
            for(String group: Inheritance)
                line += " " + Logic.INHERITANCE + " " + group;

        block.add(line + " " + Logic.BLOCK_OPENING + "\r\n");

        // Reset data
        line = "";
        x = 0;

        if(!Nodes.isEmpty()) {
            block.add("    # Nodes" + "\r\n");

            for(Node n: Nodes) {
                if(x == 6) {
                    if(line.endsWith(Logic.LIST_SEPERATOR + " "))
                        line = line.substring(0, line.length()-2);

                    block.add("    " + line + "\r\n");
                    line = "";
                    x = 0;
                }

                if(n.isNegated())
                    line += Logic.NODE_NEGATE_OPENING;

                line += n.getName() + Logic.LIST_SEPERATOR + " ";

                x++;
            }

            if(line.length() > 0) {
                if(line.endsWith(Logic.LIST_SEPERATOR + " "))
                    line = line.substring(0, line.length()-2);

                block.add("    " + line + "\r\n");
                line = "";
                x = 0;
            }
        }

        if(!data.isEmpty()) {
            block.add("\r\n    # Data" + "\r\n");

            for(String k: this.data.keySet()) {
                String key = k;
                String value = this.data.get(k);

                if(!Dictionary.isBoolean(value) && !Dictionary.isDouble(value) && !Dictionary.isInteger(value))
                    value = "'" + value + "'";
                else if(value.contains(" "))
                    value = "'" + value + "'";

                block.add("    " + key + Logic.DICT_SEPERATOR + " " + value + "\r\n");
            }
        }

        if(!isDefault())
            block.add("    " + "weight" + Logic.DICT_SEPERATOR + " " + weight + "\r\n");

        block.add("]\r\n");

        // Reset line
        line = "";

        for(String s: block)
            line += s;
        
        return line;
    }

    private String trimWeight(String name) {
        for (String d: Logic.DEFAULT)
            name = name.replace(d, "").trim();

        for (String ws: Logic.WEIGHT_SEPERATOR)
            if (name.contains(ws))
                name = name.trim().split(ws)[0].trim();

        return name;
    }
}
