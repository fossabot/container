package org.opentosca.container.api.service;

import java.util.Set;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;

import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.ICoreFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class CsarService {

	private static Logger logger = LoggerFactory.getLogger(CsarService.class);
	
	private ICoreFileService fileService;


	/**
	 * Loads all available CSARs as {@link CSARContent}
	 *
	 * @return Set of {@link CSARContent} objects
	 */
	public Set<CSARContent> findAll() {
		final Set<CSARContent> csarSet = Sets.newHashSet();
		for (final CSARID id : this.fileService.getCSARIDs()) {
			try {
				csarSet.add(this.findById(id));
			} catch (final Exception e) {
				logger.error("Error while loading CSAR with ID \"{}\": {}", id, e.getMessage(), e);
				throw new ServerErrorException(Response.serverError().build());
			}
		}
		return csarSet;
	}
	
	/**
	 * Loads a CSAR as {@link CSARContent} by a given id
	 *
	 * @param id The id of the CSAR
	 * @return The CSAR as {@link CSARContent}
	 */
	public CSARContent findById(final CSARID id) {
		try {
			return this.fileService.getCSAR(id);
		} catch (final UserException e) {
			throw new NotFoundException("CSAR \"" + id.getFileName() + "\" could not be found");
		}
	}
	
	/**
	 * Loads a CSAR as {@link CSARContent} by a given id
	 *
	 * @param id The id of the CSAR
	 * @return The CSAR as {@link CSARContent}
	 */
	public CSARContent findById(final String id) {
		return this.findById(new CSARID(id));
	}

	public void setFileService(final ICoreFileService fileService) {
		this.fileService = fileService;
	}
}
