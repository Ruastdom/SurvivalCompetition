package xiamomc.survivalcompetition.misc.permission;

import java.util.ArrayList;
import java.util.List;

public class PermissionNode
{
    private final List<String> nodes;

    private final String rootNode = "xiamomc.sc";

    public PermissionNode(String initialNode)
    {
        this.nodes = new ArrayList<>();
        this.nodes.add(rootNode);
        this.append(initialNode);
    }

    private PermissionNode(List<String> nodes)
    {
        this.nodes = nodes;
    }

    public PermissionNode append(String node)
    {
        if (node == null || node.isBlank() || node.isEmpty())
            throw new IllegalArgumentException("节点名称不能为空");

        if (node.contains("."))
            throw new IllegalArgumentException("节点名称不能包含“.”: " + node);

        nodes.add(node);
        return this;
    }

    public PermissionNode getCopy()
    {
        return new PermissionNode(nodes);
    }

    public static PermissionNode create(String initialNode)
    {
        return new PermissionNode(initialNode);
    }

    @Override
    public String toString()
    {
        StringBuilder finalValue = new StringBuilder();

        for (var n : nodes)
        {
            finalValue.append(n);

            if (nodes.indexOf(n) + 1 < nodes.size()) finalValue.append(".");
        }

        return finalValue.toString();
    }
}
