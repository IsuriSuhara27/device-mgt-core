package io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto;

import java.util.List;
import java.util.Set;

public class DeviceNodeResult {

    private List<DeviceNode> nodes;
    private Set<DeviceOrganization> edges;

    public DeviceNodeResult(List<DeviceNode> nodes, Set<DeviceOrganization> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public List<DeviceNode> getNodes() {
        return nodes;
    }

    public Set<DeviceOrganization> getEdges() {
        return edges;
    }

}
