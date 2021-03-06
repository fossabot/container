package org.opentosca.container.api.legacy.resources.csar.servicetemplate.instances.plans;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PlanInstanceOutput {

    private static final Logger LOG = LoggerFactory.getLogger(PlanInstanceOutput.class);

    private final CSARID csarId;
    private final QName serviceTemplateId;
    private final int serviceTemplateInstanceId;
    private final String correlationId;


    public PlanInstanceOutput(final CSARID csarID, final QName serviceTemplateID, final int serviceTemplateInstanceId,
                              final String correlationID) {
        this.csarId = csarID;
        this.serviceTemplateId = serviceTemplateID;
        this.serviceTemplateInstanceId = serviceTemplateInstanceId;
        this.correlationId = correlationID;
    }

    // /**
    // * Returns a PlanInvocationEvent for a CorrelationID.
    // *
    // * @param correlationID
    // * @return the PublicPlan for the CorrelationID
    // */
    // @GET
    // @Produces(ResourceConstants.TOSCA_XML)
    // public Response getInstance() {
    // LOG.debug("Return plan for correlation " + this.correlationId);
    //
    // PlanInstanceRepository repository = new PlanInstanceRepository();
    // PlanInstance pi = repository.findByCorrelationId(this.correlationId);
    // if (pi != null) {
    // return Response.ok(pi.getOutputs()).build();
    // } else {
    // LOG.error("Plan instance for correlation id '{}' not found", this.correlationId);
    // }
    // return Response.serverError().build();
    // // return
    // // CSARInstanceManagementHandler.csarInstanceManagement.getPlanFromHistory(this.correlationId);
    // }

    /**
     * Returns the plan information from history.
     *
     * @param uriInfo
     * @return Response
     */
    @GET
    @Produces(ResourceConstants.TOSCA_JSON)
    public Response getPlanJSON() throws Exception {
        LOG.debug("Return plan for correlation " + this.correlationId);
        final ObjectMapper mapper = new ObjectMapper();
        final Map<String, Object> response = new HashMap<>();
        final PlanInstanceRepository repository = new PlanInstanceRepository();
        final PlanInstance pi = repository.findByCorrelationId(this.correlationId);
        if (pi != null) {
            response.put("inputs", pi.getInputs());
            response.put("outputs", pi.getOutputs());
            return Response.ok(mapper.writeValueAsString(response)).build();
        } else {
            LOG.error("Plan instance for correlation id '{}' not found", this.correlationId);
        }
        return Response.serverError().build();

        // final PlanInvocationEvent event =
        // CSARInstanceManagementHandler.csarInstanceManagement.getPlanFromHistory(this.correlationId);
        //
        // final JsonObject json = new JsonObject();
        // json.addProperty("ID", event.getPlanID().toString());
        // json.addProperty("Name", event.getPlanName());
        // json.addProperty("PlanType", event.getPlanType());
        // json.addProperty("PlanLanguage", event.getPlanLanguage());
        //
        // final JsonArray input = new JsonArray();
        // try {
        // for (final TParameterDTO param : event.getInputParameter()) {
        // final JsonObject paramObj = new JsonObject();
        // final JsonObject paramDetails = new JsonObject();
        // paramDetails.addProperty("Name", param.getName());
        // paramDetails.addProperty("Type", param.getType());
        // paramDetails.addProperty("Value", param.getValue());
        // paramDetails.addProperty("Required", param.getRequired().value());
        // paramObj.add("InputParameter", paramDetails);
        // input.add(paramObj);
        // }
        // } catch (final NullPointerException e) {
        // }
        // json.add("InputParameters", input);
        //
        // final JsonArray output = new JsonArray();
        // try {
        // for (final TParameterDTO param : event.getOutputParameter()) {
        // final JsonObject paramObj = new JsonObject();
        // final JsonObject paramDetails = new JsonObject();
        // paramDetails.addProperty("Name", param.getName());
        // paramDetails.addProperty("Type", param.getType());
        // paramDetails.addProperty("Value", param.getValue());
        // paramDetails.addProperty("Required", param.getRequired().value());
        // paramObj.add("OutputParameter", paramDetails);
        // output.add(paramObj);
        // }
        // } catch (final NullPointerException e) {
        // }
        // json.add("OutputParameters", output);
        //
        // final JsonObject planModelReference = new JsonObject();
        // // planModelReference.addProperty("Reference",
        // // event.getPlanModelReference().getReference());
        // json.add("PlanModelReference", planModelReference);
        //
        // return Response.ok(json.toString()).build();
    }

}
