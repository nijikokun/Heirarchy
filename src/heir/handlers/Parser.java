package heir.handlers;

import heir.arch.Group;
import heir.arch.Groups;
import heir.arch.Node;
import heir.util.Common;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
    Copyright (c) 2011, Nijiko Yonskai (Nijikokun) <nijikokun@gmail.com>
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

        1. Redistributions of source code must retain the above copyright
            notice, this list of conditions and the following disclaimer.

        2. Redistributions in binary form must reproduce the above copyright
            notice, this list of conditions and the following disclaimer in the
            documentation and/or other materials provided with the distribution.

        3. Neither the name of Nijiko Yonskai nor the
            names of its contributors may be used to endorse or promote products
            derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
    ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
    WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
    DISCLAIMED. IN NO EVENT SHALL Nijiko Yonskai BE LIABLE FOR ANY
    DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
    (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
    LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
    ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class Parser {
    private Common common = new Common();
    Groups Groups = new Groups();
    LinkedHashMap<Integer, String> Weights = new LinkedHashMap<Integer, String>();
    LinkedHashMap<String, LinkedList<String>> Inheritance = new LinkedHashMap<String, LinkedList<String>>(), Nodes = new LinkedHashMap<String, LinkedList<String>>();
    LinkedHashMap<String, LinkedHashMap<String, String>> Data = new LinkedHashMap<String, LinkedHashMap<String, String>>();

    public static class Logic {
        public static String BLOCK_OPENING           = "[";
        public static String BLOCK_ENDING            = "]";
        public static String INHERITANCE             = ">";
        public static String LIST_SEPERATOR          = ",";
        public static String DICT_SEPERATOR          = ":";
        public static String DATA_SEPERATOR          = " ";
        public static String LINE_ENDING             = ";";
        public static String NODE_NEGATE_OPENING     = "-";
        public static String WEIGHT_OPENING          = "weight";
        public static String NODE_LINE_OPENING       = "nodes";
        public static String[] MULTIPLE_GROUPS       = { "&", "and", "&&", "+" };
        public static String[] DEFAULT               = { "default", "main" };
        public static String[] WEIGHT_SEPERATOR      = { ":", "." };
        public static String[] SINGLE_LINE_COMMENTS  = { "#", "--", "//" };
        public static String[][] MULTI_LINE_COMMENTS = {
            { "/*", "*/" }
        };



        static class Comment {
            private static boolean[] isComment(String line) {
                return isComment(line, false);
            }

            private static boolean[] isComment(String line, boolean block) {
                for(String slc: SINGLE_LINE_COMMENTS)
                    if(line.startsWith(slc))
                        return new boolean[]{ true, false };

                for(String[] mlco: MULTI_LINE_COMMENTS)
                    if(line.startsWith(mlco[0]))
                        return new boolean[]{ true, true };
                    else if(line.contains(mlco[1]) && block)
                        return new boolean[]{ true, false };

                return new boolean[]{ false, false };
            }

            private static String trimComments(String line) {
                for(String slc: SINGLE_LINE_COMMENTS)
                    line = Common.trimAfter(line, slc);

                return line;
            }
        }
        
        static class Block {
            private static boolean isOpening(String line) {
                return line.endsWith(BLOCK_OPENING);
            }

            private static boolean isClosing(String line, boolean inBlock) {
                if(line.equals(BLOCK_ENDING) || line.equals(BLOCK_ENDING + LINE_ENDING))
                    return true;

                if(inBlock && line.endsWith(BLOCK_OPENING))
                    return true;

                return false;
            }

            static class Inheritance {
                private static boolean exists(String line) {
                    return line.contains(INHERITANCE);
                }

                static class Multiple {
                    private static boolean exists(String line) {
                        for (String mg: MULTIPLE_GROUPS)
                            if (line.contains(line))
                                return true;

                        return false;
                    }
                    
                    private static String[] toArray(String line) {
                        ArrayList<String> groups = new ArrayList<String>();
                        
                        for (String mg: MULTIPLE_GROUPS)
                            if (line.contains(mg))
                                groups.addAll(Arrays.asList(Common.trim(line.split(mg))));

                        if(groups.isEmpty())
                            groups.add(line);
                        
                        return groups.toArray(new String[]{});
                    }
                }
            }

            static class Weight {
                private static boolean exists(String line) {
                    for (String ws: WEIGHT_SEPERATOR)
                        if (line.contains(ws))
                            return true;

                    return false;
                }

                private static boolean isDefault(String line) {
                    for (String d: DEFAULT)
                        if(line.toLowerCase().contains(d))
                            return true;

                    return false;
                }
                
                private static int get(String line) {
                    String data = "";
                    int weight = 0;
                    
                    if(isDefault(line))
                        return weight;
                    
                    for (String ws: WEIGHT_SEPERATOR)
                        if (line.contains(ws))
                            data = line.trim().split(ws)[1];
                    
                    try {
                        weight = Integer.valueOf(data);
                    } catch (NumberFormatException E) { 
                        System.out.println("Invalid weight on Group: " + line + ". Returning [0] for weight.");
                    }
                    
                    return weight;
                }
                
                private static String trim(String line) {
                    for (String d: DEFAULT)
                        line = line.replace(d, "").trim();
                    
                    for (String ws: WEIGHT_SEPERATOR)
                        if (line.contains(ws))
                            line = line.trim().split(ws)[0].trim();
                    
                    return line;
                }
            }
            
            static class Node {
                private static boolean hasOpening(String line) {
                    return line.startsWith(NODE_LINE_OPENING + DICT_SEPERATOR);
                }
                
                private static boolean hasMultiple(String line) {
                    return line.contains(LIST_SEPERATOR);
                }

                private static boolean isNegative(String node) {
                    return node.startsWith(NODE_NEGATE_OPENING);
                }
                
                private static String trimOpening(String line) {
                    return line.replace(NODE_LINE_OPENING + DICT_SEPERATOR, "").trim();
                }

                private static String trimNegative(String node) {
                    return node.replace(NODE_NEGATE_OPENING, "");
                }
                
                static class Multiple {
                    private static String[] split(String line) {
                        return Common.trim(line.split(LIST_SEPERATOR));
                    }
                }
            }
            
            static class Dictionary {
                private static boolean isEntry(String line) {
                    return line.contains(DICT_SEPERATOR);
                }
                
                private static boolean isWeight(String line) {
                    return line.equalsIgnoreCase(WEIGHT_OPENING);
                }
                
                private static String getKey(String line) {
                    return Common.trim(line.split(DICT_SEPERATOR))[0];
                }
                
                private static String getValue(String line) {
                    return Common.trim(line.split(DICT_SEPERATOR))[1];
                }
                
                private static String[] getData(String line) {
                    return Common.trim(line.split(DICT_SEPERATOR));
                }
            }
        }
    }

    public void parse(String file) {
        // for external files: common.readFile(file);
        List<String> lines = common.read(file);

        // Current Group Data
        int weight = -1;
        Group Group = new Group();

        // Used for string splits
        String[] split = null, 
                gsplit = null;

        // Keeps tabs on current token groups
        boolean inBlock = false,
                inCommentBlock = false,
                inWeighting = false;

        // Current block group.
        String currentGroup = "";

        // Loop handling.
        int passes = 0,
            loops = 0,
            id = 0;

        for(String line: lines) {
            String trimmed = line.trim();

            // Remove empty lines, Comments, Etc
            if(Logic.Comment.isComment(trimmed, inCommentBlock)[0]) {
                inCommentBlock = Logic.Comment.isComment(trimmed, inCommentBlock)[1];
                continue;
            } else if(trimmed.isEmpty())
                continue;

            // Trim the same line comments.
            trimmed = Logic.Comment.trimComments(trimmed);

            // Check for endings
            if(Logic.Block.isClosing(line, inBlock)) {
                // If we were in a block, close it up.
                if(inBlock) 
                    inBlock = false;

                // If we were handling groups, close it out.
                if(Group != null || Group.getName() != null) {
                    if(weight == -1)
                        Groups.addGroup(Group);
                    else {
                        Group.setWeight(weight);
                        Groups.addGroup(weight, Group);
                    }

                    Group = new Group();
                    weight = -1;
                }

                // Clear the passes for the next block
                passes = 0;
                
                // Continue only if we are sure someone didn't forget to end a block
                // If not we continue to start the new block.
                if(!Logic.Block.isOpening(trimmed))
                    continue;
            }

            // We are in a block, the only time we aren't is never.
            if(!inBlock)
                inBlock = true;

            if(passes == 0 && Logic.Block.isOpening(trimmed)) {
                trimmed = trimmed.substring(0, trimmed.length()-1);
                
                if(!Logic.Block.Inheritance.exists(trimmed)) {
                    String group = trimmed.split(" ", 2)[0];

                    if(Logic.Block.Weight.exists(group))
                        weight = Logic.Block.Weight.get(group);

                    if(Logic.Block.Weight.isDefault(group))
                        Group.setDefault(true);

                    Group.setName(Logic.Block.Weight.trim(group));
                    passes++;
                }

                if(Logic.Block.Inheritance.exists(trimmed)) {
                    LinkedList<String> groups = new LinkedList<String>();
                    split = Common.trim(trimmed.split(Logic.INHERITANCE));
                    loops = 0;

                    for(String part: split) {
                        if(part == null || part.isEmpty()) 
                            continue;

                        gsplit = Logic.Block.Inheritance.Multiple.toArray(part);

                        for(String group: gsplit) {
                            if(group == null || group.isEmpty()) 
                                continue;

                            if(loops == 0) {
                                if(Group.getName() == null ? group != null : !Group.getName().equals(group)) {
                                    Group.setName(group);
                                } else
                                    Group.addInhertance(group);
                            } else
                                Group.addInhertance(group);

                            loops++;
                        }
                    }
                }

                // System.out.println(currentGroup);
                continue;
            }

            // Anything beyond this point needs the current group.
            if(Group.getName() == null)
                continue;

            // Remove the nodes: prefix, not needed, just there for letting me sleep at night I guess.
            if(Logic.Block.Node.hasOpening(trimmed))
                trimmed = Logic.Block.Node.trimOpening(trimmed);

            if(Logic.Block.Dictionary.isEntry(trimmed)) {
                split = Logic.Block.Dictionary.getData(trimmed);

                if(split.length < 1) 
                    continue;

                if(split[1].endsWith(Logic.LINE_ENDING))
                    split[1] = split[1].substring(0, split[1].length()-1);

                String key = split[0].trim();
                String value = split[1].trim();
                
                // Remove the semi-colon ending
                if(value.endsWith(Logic.LINE_ENDING))
                    value = value.substring(0, value.length()-1);

                // Remove quotes
                key = Common.unQuote(key);
                value = Common.unQuote(value);

                // Check the key validity
                if(key.isEmpty() || key.length() < 1)
                    continue;

                if(Logic.Block.Dictionary.isWeight(key))
                    try {
                        weight = Integer.valueOf(value);
                    } catch (NumberFormatException E) {
                        System.out.println("Invalid number for weight on group: " + currentGroup);
                    }
                else 
                    Group.addEntry(key, value);

                continue;
            }

            if(Logic.Block.Node.hasMultiple(trimmed)) {
                split = Logic.Block.Node.Multiple.split(trimmed);

                for(String node: split) {
                    if(node.endsWith(Logic.LINE_ENDING))
                        node = node.substring(0, node.length()-1);

                    boolean negative = Logic.Block.Node.isNegative(node);
                    node = node.trim();

                    if(negative)
                        node = Logic.Block.Node.trimNegative(node);

                    Group.addNode(new Node(node, negative));
                }
            } else {
                if(trimmed.isEmpty())
                    continue;

                boolean negative = Logic.Block.Node.isNegative(trimmed);
                trimmed = trimmed.trim();

                if(negative)
                    trimmed = Logic.Block.Node.trimNegative(trimmed);

                Group.addNode(new Node(trimmed, negative));
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Parser main = new Parser();
        main.parse("resources/group.harc");

        System.out.println(main.Groups);
    }

}
