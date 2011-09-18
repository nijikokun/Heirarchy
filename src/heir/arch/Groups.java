package heir.arch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Groups {
    HashMap<Integer, Group> Groups;

    public Groups() {
        this.Groups = new HashMap<Integer, Group>();
    }

    public List<Group> getGroups() {
        if(this.Groups.isEmpty())
            return null;
        
        ArrayList<Group> groups = new ArrayList<Group>();
        
        for(int w: this.Groups.keySet())
            groups.add(this.Groups.get(w));
        
        return groups;
    }

    public String[] getGroupList() {
        if(this.Groups.isEmpty())
            return null;

        ArrayList<String> groups = new ArrayList<String>();

        for(int w: this.Groups.keySet())
            groups.add(this.Groups.get(w).getName());

        return groups.toArray(new String[]{});
    }

    public Group getGroup(String group) {
        if(this.Groups.isEmpty())
            return null;

        for(int w: this.Groups.keySet())
            if(this.Groups.get(w).getName().equalsIgnoreCase(group))
                return this.Groups.get(w);

        return null;
    }

    public Group getBaseGroup() {
        if(this.Groups.isEmpty())
            return null;

        if(this.Groups.containsKey(0))
            return this.Groups.get(0);

        for(int w: this.Groups.keySet())
            if(this.Groups.get(w).isDefault())
                return this.Groups.get(w);

        return null;
    }

    public int getWeight(String group) {
        if(this.Groups.isEmpty())
            return -1;

        for(int w: this.Groups.keySet())
            if(this.Groups.get(w).getName().equalsIgnoreCase(group))
                return w;

        return -1;
    }

    public boolean hasGroup(String group) {
        if(this.Groups.isEmpty())
            return false;
        
        for(int w: this.Groups.keySet())
            if(this.Groups.get(w).getName() == null)
                System.out.println("[HARC] Inject group without a name.");
            else if(this.Groups.get(w).getName().equalsIgnoreCase(group))
                return true;

        return false;
    }

    public void addGroup(Group group) {
        if(hasGroup(group.getName()))
            return;

        int weight = getNextWeight();
        group.setWeight(weight);
        this.Groups.put(weight, group);
    }

    public void addGroup(int weight, Group group) {
        if(hasGroup(group.getName()))
            return;

        if(this.Groups.containsKey(weight))
            weight = getNextWeight(weight);

        group.setWeight(weight);

        this.Groups.put(weight, group);
    }

    public void addGroups(HashMap<Integer, Group> Groups) {
        for(int w: Groups.keySet())
            addGroup(w, Groups.get(w));
    }

    public void setGroups(HashMap<Integer, Group> Groups) {
        this.Groups = Groups;
    }

    private int getNextWeight() {
        int x = 0, weight = 0;

        for(int w: this.Groups.keySet())
            if(x == this.Groups.keySet().size())
                weight = w + 1;
            else
                x++;

        if(this.Groups.containsKey(weight))
            return getNextWeight(weight);
        else
            return weight;
    }

    private int getNextWeight(int weight) {
        for(int w: this.Groups.keySet())
            if(weight == w)
                weight = w + 1;

        if(this.Groups.containsKey(weight))
            return getNextWeight(weight);
        else
            return weight;
    }

    @Override
    public String toString() {
        String output = "";

        for(int i: this.Groups.keySet())
            output += this.Groups.get(i) + "\r\n";

        return output;
    }
}
