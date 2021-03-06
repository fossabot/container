package org.opentosca.container.api.legacy.resources.csar.servicetemplate.instances;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * TODO refactoring: move this to a suitable point
 *
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Christian Endres - christian.endres@iaas.uni-stuttgart.de
 *
 */
@Deprecated
public class BuildCorrelationToInstanceMapping {

    public static BuildCorrelationToInstanceMapping instance = new BuildCorrelationToInstanceMapping();

    private final Map<String, Integer> correlationIdToServiceTemplateInstanceId = new HashMap<>();


    private BuildCorrelationToInstanceMapping() {}

    public void correlateCorrelationIdToServiceTemplateInstanceId(final String corrId,
                                                                  final int serviceTemplateInstanceId) {
        this.correlationIdToServiceTemplateInstanceId.put(corrId, serviceTemplateInstanceId);
    }

    public Integer getServiceTemplateInstanceIdForBuildPlanCorrelation(final String corrId) {
        return this.correlationIdToServiceTemplateInstanceId.get(corrId);
    }

    public boolean knowsCorrelationId(final String buildPlanCorrId) {
        return this.correlationIdToServiceTemplateInstanceId.containsKey(buildPlanCorrId);
    }

    public String getCorrelationId(final int serviceTemplateInstanceId) {
        for (final Entry<String, Integer> entry : this.correlationIdToServiceTemplateInstanceId.entrySet()) {
            if (entry.getValue() == serviceTemplateInstanceId) {
                return entry.getKey();
            }
        }
        return null;
    }
}
