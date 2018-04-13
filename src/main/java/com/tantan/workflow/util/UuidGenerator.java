/**
 * 
 */
package com.tantan.workflow.util;

import java.util.UUID;

import org.activiti.engine.impl.cfg.IdGenerator;

/**
 * @author pursue2020
 *
 */
public class UuidGenerator implements IdGenerator{
	
	@Override
	public String getNextId() {
		UUID uuid = UUID.randomUUID();
		String id = uuid.toString();
		return id;
	}

}
