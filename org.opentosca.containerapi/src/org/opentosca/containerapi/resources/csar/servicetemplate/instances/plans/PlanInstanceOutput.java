package org.opentosca.containerapi.resources.csar.servicetemplate.instances.plans;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.osgi.servicegetter.CSARInstanceManagementHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.extension.planinvocationevent.PlanInvocationEvent;
import org.opentosca.model.tosca.extension.transportextension.TParameterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class PlanInstanceOutput  {
	
	private static final Logger LOG = LoggerFactory.getLogger(PlanInstanceOutput.class);
	
	private final CSARID csarId;
	private QName serviceTemplateId;
	private int serviceTemplateInstanceId;
	private String correlationId;
	
	public PlanInstanceOutput(CSARID csarID, QName serviceTemplateID, int serviceTemplateInstanceId, String correlationID) {
		csarId = csarID;
		serviceTemplateId = serviceTemplateID;
		this.serviceTemplateInstanceId = serviceTemplateInstanceId;
		correlationId = correlationID;
	}
	
	/**
	 * Returns a PlanInvocationEvent for a CorrelationID.
	 * 
	 * @param correlationID
	 * @return the PublicPlan for the CorrelationID
	 */
	@GET
	@Produces(ResourceConstants.TOSCA_XML)
	public PlanInvocationEvent getInstance() {
		LOG.debug("Return plan for correlation " + correlationId);
		return CSARInstanceManagementHandler.csarInstanceManagement.getPlanFromHistory(correlationId);
	}
	
	/**
	 * Returns the plan information from history.
	 * 
	 * @param uriInfo
	 * @return Response
	 */
	@GET
	@Path("{CorrelationID}")
	@Produces(ResourceConstants.TOSCA_JSON)
	public Response getPlanJSON() {
		
		LOG.debug("Return plan for correlation " + correlationId);
		PlanInvocationEvent event = CSARInstanceManagementHandler.csarInstanceManagement.getPlanFromHistory(correlationId);
		
		JsonObject json = new JsonObject();
		json.addProperty("ID", event.getPlanID().toString());
		json.addProperty("Name", event.getPlanName());
		json.addProperty("PlanType", event.getPlanType());
		json.addProperty("PlanLanguage", event.getPlanLanguage());
		
		JsonArray input = new JsonArray();
		try {
			for (TParameterDTO param : event.getInputParameter()) {
				JsonObject paramObj = new JsonObject();
				JsonObject paramDetails = new JsonObject();
				paramDetails.addProperty("Name", param.getName());
				paramDetails.addProperty("Type", param.getType());
				paramDetails.addProperty("Value", param.getValue());
				paramDetails.addProperty("Required", param.getRequired().value());
				paramObj.add("InputParameter", paramDetails);
				input.add(paramObj);
			}
		} catch (NullPointerException e) {
		}
		json.add("InputParameters", input);
		
		JsonArray output = new JsonArray();
		try {
			for (TParameterDTO param : event.getOutputParameter()) {
				JsonObject paramObj = new JsonObject();
				JsonObject paramDetails = new JsonObject();
				paramDetails.addProperty("Name", param.getName());
				paramDetails.addProperty("Type", param.getType());
				paramDetails.addProperty("Value", param.getValue());
				paramDetails.addProperty("Required", param.getRequired().value());
				paramObj.add("OutputParameter", paramDetails);
				output.add(paramObj);
			}
		} catch (NullPointerException e) {
		}
		json.add("OutputParameters", output);
		
		JsonObject planModelReference = new JsonObject();
		// planModelReference.addProperty("Reference",
		// event.getPlanModelReference().getReference());
		json.add("PlanModelReference", planModelReference);
		
		return Response.ok(json.toString()).build();
	}
	
}