package org.opentosca.containerapi.resources.csar.servicetemplate.boundarydefinitions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.TParameter;
import org.opentosca.model.tosca.TPlan;
import org.opentosca.model.tosca.extension.helpers.PlanTypes;
import org.opentosca.toscaengine.service.IToscaReferenceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class BoundsInterfaceOperationPlanResource {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(BoundsInterfaceOperationPlanResource.class);
	CSARID csarID;
	QName serviceTemplateID;
	String intName;
	String opName;
	
	UriInfo uriInfo;
	
	
	public BoundsInterfaceOperationPlanResource(CSARID csarID, QName serviceTemplateID, String intName, String opName) {
		
		this.csarID = csarID;
		this.serviceTemplateID = serviceTemplateID;
		this.intName = intName;
		this.opName = opName;
		
		if (null == ToscaServiceHandler.getToscaEngineService()) {
			LOG.error("The ToscaEngineService is not alive.");
		}
	}
	
	/**
	 * Returns the Boundary Definitions Node Operation. TODO not yet implemented
	 * yet, thus, just returns itself.
	 * 
	 * @param uriInfo
	 * @return Response
	 * @throws UnsupportedEncodingException
	 */
	@GET
	// @Path("{PlanName}")
	@Produces(ResourceConstants.TOSCA_XML)
	public JAXBElement getPlanXML(@Context UriInfo uriInfo) throws UnsupportedEncodingException {
		String planName = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getBoundaryPlanOfCSARInterface(csarID, intName, opName).getLocalPart();
		
		TPlan plan = getPlan(planName);
		
		// if
		// (plan.getPlanType().startsWith("http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/BuildPlan"))
		// {
		// plan.getAny().add(new
		// Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(),
		// getPostPath(uriInfo, true)), XLinkConstants.REFERENCE,
		// "PlanPostURL").toXml());
		// } else {
		// plan.getAny().add((new
		// Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(),
		// getPostPath(uriInfo, false)), XLinkConstants.REFERENCE,
		// "PlanPostURL").toXml()));
		// }
		
		return ToscaServiceHandler.getIXMLSerializer().createJAXBElement(plan);
	}
	
	private String getPostPath(boolean isBuildPlan) throws UnsupportedEncodingException {
		
		String path = "CSARs/" + csarID.getFileName() + "/ServiceTemplates/" + URLEncoder.encode(serviceTemplateID.toString(), "UTF-8") + "/Instances/";
		
		if (!isBuildPlan) {
			path = path + "{instanceId}";
		}
		
		LOG.debug("POST URL: {}", path);
		
		return path;
	}
	
	/**
	 * Returns the Plan.
	 * 
	 * @param uriInfo
	 * @return Response
	 * @throws UnsupportedEncodingException
	 */
	@GET
	// @Path("{PlanName}")
	@Produces(ResourceConstants.TOSCA_JSON)
	public Response getPlanJSON(@Context UriInfo uriInfo) throws UnsupportedEncodingException {
		
		String planName = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getBoundaryPlanOfCSARInterface(csarID, intName, opName).getLocalPart();
		
		TPlan plan = getPlan(planName);
		
		JsonObject json = new JsonObject();
		
		if (plan.getPlanType().startsWith("http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/BuildPlan")) {
			json.add("Reference", new Reference(Utilities.buildURI(uriInfo.getBaseUri().toString(), getPostPath(true)), XLinkConstants.REFERENCE, "PlanPostURL").toJson());
		} else {
			json.add("Reference", new Reference(Utilities.buildURI(uriInfo.getBaseUri().toString(), getPostPath(false)), XLinkConstants.REFERENCE, "PlanPostURL").toJson());
		}
		
		JsonObject planJson = new JsonObject();
		json.add("Plan", planJson);
		planJson.addProperty("ID", plan.getId());
		planJson.addProperty("Name", plan.getName());
		planJson.addProperty("PlanType", plan.getPlanType());
		planJson.addProperty("PlanLanguage", plan.getPlanLanguage());
		
		JsonArray input = new JsonArray();
		try {
			for (TParameter param : plan.getInputParameters().getInputParameter()) {
				JsonObject paramObj = new JsonObject();
				JsonObject paramDetails = new JsonObject();
				paramDetails.addProperty("Name", param.getName());
				paramDetails.addProperty("Type", param.getType());
				paramDetails.addProperty("Required", param.getRequired().value());
				paramObj.add("InputParameter", paramDetails);
				input.add(paramObj);
			}
		} catch (NullPointerException e) {
		}
		planJson.add("InputParameters", input);
		
		JsonArray output = new JsonArray();
		try {
			for (TParameter param : plan.getOutputParameters().getOutputParameter()) {
				JsonObject paramObj = new JsonObject();
				JsonObject paramDetails = new JsonObject();
				paramDetails.addProperty("Name", param.getName());
				paramDetails.addProperty("Type", param.getType());
				paramDetails.addProperty("Required", param.getRequired().value());
				paramObj.add("OutputParameter", paramDetails);
				output.add(paramObj);
			}
		} catch (NullPointerException e) {
		}
		planJson.add("OutputParameters", output);
		
		JsonObject planModelReference = new JsonObject();
		planModelReference.addProperty("Reference", plan.getPlanModelReference().getReference());
		planJson.add("PlanModelReference", planModelReference);
		
		return Response.ok(json.toString()).build();
	}
	
	public TPlan getPlan(String plan) {
		LOG.trace("Return plan " + plan);
		Map<PlanTypes, LinkedHashMap<QName, TPlan>> map = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getCSARIDToPlans(csarID);
		
		IToscaReferenceMapper service = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper();
		String ns = service.getNamespaceOfPlan(csarID, plan);
		
		QName id = new QName(ns, plan);
		
		for (PlanTypes type : PlanTypes.values()) {
			if (map.get(type).containsKey(id)) {
				TPlan tPlan = map.get(type).get(id);
				return tPlan;
			}
		}
		
		return null;
	}
	
	// /**
	// * PUT for BUILD plans which have no CSAR-Instance-ID yet.
	// *
	// * @param planElement the BUILD PublicPlan
	// * @return Response
	// */
	// @POST
	// @Path("{PlanName}")
	// @Consumes(ResourceConstants.TOSCA_XML)
	// public Response postManagementPlan(@PathParam("PlanName") String plan,
	// JAXBElement<TPlanDTO> planElement) {
	//
	// CSARBoundsInterfaceOperationPlanResource.LOG.debug("Received a plan for
	// CSAR " + csarID);
	//
	// TPlanDTO planDTO = planElement.getValue();
	//
	// if (null == planDTO) {
	// LOG.error("The given PublicPlan is null!");
	// return Response.status(Status.CONFLICT).build();
	// }
	//
	// if (null == planDTO.getId()) {
	// LOG.error("The given PublicPlan has no ID!");
	// return Response.status(Status.CONFLICT).build();
	// }
	//
	// String namespace =
	// ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getNamespaceOfPlan(csarID,
	// planDTO.getId().getLocalPart());
	// planDTO.setId(new QName(namespace, planDTO.getId().getLocalPart()));
	//
	// LOG.debug("PublicPlan to invoke: " + planDTO.getId());
	//
	// CSARBoundsInterfaceOperationPlanResource.LOG.debug("Post of the
	// PublicPlan " + planDTO.getId());
	//
	// // TODO return correlation ID
	// String correlationID =
	// IOpenToscaControlServiceHandler.getOpenToscaControlService().invokePlanInvocation(csarID,
	// -1, planDTO);
	//
	// References refs = new References();
	//
	// refs.getReference().add(new
	// Reference(Utilities.buildURI("http://localhost:1337/containerapi",
	// "/CSARs/" + csarID + "/PlanInstances/" + correlationID),
	// XLinkConstants.SIMPLE, plan));
	//
	// return
	// Response.status(Response.Status.ACCEPTED).entity(refs.getXMLString()).build();
	//
	// }
	
	// /**
	// *
	// * TODO consider to deliver this output not under the path
	// * "{PlanName}/PlanWithMinimalInput" but with other MIME type under
	// * "{PlanName}"
	// *
	// * @param uriInfo
	// * @return Response
	// */
	// @GET
	// @Path("{PlanName}/PlanWithMinimalInput")
	// @Produces(ResourceConstants.TOSCA_XML)
	// public JAXBElement<?> getMissingInputFields(@PathParam("PlanName") String
	// plan) {
	// LOG.trace("Return missing input fields of plan " + plan);
	//
	// List<Document> docs = new ArrayList<Document>();
	//
	// List<QName> serviceTemplates =
	// ToscaServiceHandler.getToscaEngineService().getServiceTemplatesInCSAR(csarID);
	// for (QName serviceTemplate : serviceTemplates) {
	// List<String> nodeTemplates =
	// ToscaServiceHandler.getToscaEngineService().getNodeTemplatesOfServiceTemplate(csarID,
	// serviceTemplate);
	//
	// for (String nodeTemplate : nodeTemplates) {
	// Document doc =
	// ToscaServiceHandler.getToscaEngineService().getPropertiesOfNodeTemplate(csarID,
	// serviceTemplate, nodeTemplate);
	// if (null != doc) {
	// docs.add(doc);
	// LOG.trace("Found property document: {}",
	// ToscaServiceHandler.getIXMLSerializer().docToString(doc, false));
	// }
	// }
	// }
	//
	// Map<PlanTypes, LinkedHashMap<QName, TPlan>> mapPlans =
	// ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getCSARIDToPlans(csarID);
	//
	// IToscaReferenceMapper service =
	// ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper();
	// String ns = service.getNamespaceOfPlan(csarID, plan);
	//
	// QName id = new QName(ns, plan);
	// TPlan tPlan = null;
	// for (PlanTypes type : PlanTypes.values()) {
	// if (mapPlans.get(type).containsKey(id)) {
	// tPlan = mapPlans.get(type).get(id);
	// }
	// }
	//
	// TPlan retPlan = new TPlan();
	// retPlan.setId(tPlan.getId());
	// retPlan.setName(tPlan.getName());
	// retPlan.setPlanLanguage(tPlan.getPlanLanguage());
	// retPlan.setPlanType(tPlan.getPlanType());
	// retPlan.setInputParameters(new InputParameters());
	//
	// List<TParameter> list = new ArrayList<>();
	// for (TParameter para : tPlan.getInputParameters().getInputParameter()) {
	// if (para.getType().equalsIgnoreCase("correlation") ||
	// para.getName().equalsIgnoreCase("csarName") ||
	// para.getName().equalsIgnoreCase("containerApiAddress") ||
	// para.getName().equalsIgnoreCase("instanceDataAPIUrl")) {
	// LOG.trace("Skipping parameter {}", para.getName());
	// // list.add(para);
	// } else {
	// LOG.trace("The parameter \"" + para.getName() + "\" may have values in
	// the properties.");
	// String value = "";
	// for (Document doc : docs) {
	// NodeList nodes = doc.getElementsByTagNameNS("*", para.getName());
	// LOG.trace("Found {} nodes.", nodes.getLength());
	// if (nodes.getLength() > 0) {
	// value = nodes.item(0).getTextContent();
	// LOG.debug("Found value {}", value);
	// break;
	// }
	// }
	// if (value.equals("")) {
	// LOG.debug("Found empty input paramater {}.", para.getName());
	// list.add(para);
	// } else {
	// }
	// }
	// }
	// retPlan.getInputParameters().getInputParameter().addAll(list);
	//
	// return
	// ToscaServiceHandler.getIXMLSerializer().createJAXBElement(retPlan);
	// }
	
}