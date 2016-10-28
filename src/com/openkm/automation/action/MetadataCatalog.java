/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2015 Paco Avila & Josep Llort
 * 
 * No bytes were intentionally harmed during the development of this application.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.automation.action;

import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.util.Calendar;
import com.openkm.api.OKMDocument;
import com.openkm.api.OKMFolder;
import com.openkm.api.OKMPropertyGroup;
import com.openkm.automation.Action;
import com.openkm.automation.AutomationUtils;
import com.openkm.bean.PropertyGroup;
import com.openkm.bean.form.FormElement;
import com.openkm.bean.form.Option;
import com.openkm.bean.form.Select;
import com.openkm.dao.bean.Automation;
import com.openkm.module.db.stuff.DbSessionManager;

@PluginImplementation
public class MetadataCatalog implements Action {
	private static Logger log = LoggerFactory.getLogger(MetadataCatalog.class);
	private static String TEST_PROPERTY_GROUP_NAME = "okg:test";
	private static String TEST_PROPERTY_PARENT_NAME = "okp:test.parent";
	private static String TEST_PROPERTY_CHILDREN_NAME = "okp:test.children";

	@Override
	public void executePre(Map<String, Object> env, Object... params) {
		// Nothing to do here
	}

	@Override
	public void executePost(Map<String, Object> env, Object... params) {
		log.info("executePost");
		try {
			String uuid = AutomationUtils.getUuid(env); // Getting uuid object
			if (OKMDocument.getInstance().isValid(null, uuid)) { // Testing uuid is a document
				for (PropertyGroup group : OKMPropertyGroup.getInstance().getGroups(null, uuid)) {
					if (TEST_PROPERTY_GROUP_NAME.equals(group.getName())) {
						String parent = "";
						String children = "";
						for (FormElement formElement : OKMPropertyGroup.getInstance().getProperties(null, uuid, TEST_PROPERTY_GROUP_NAME) ) {
							if (formElement instanceof Select) {
								for (Option option : ((Select) formElement).getOptions()) {
									if (option.isSelected()) {
										if (TEST_PROPERTY_PARENT_NAME.equals(formElement.getName())) {
											parent = option.getLabel();
										} else if (TEST_PROPERTY_CHILDREN_NAME.equals(formElement.getName())) {
											children = option.getLabel();
										}
									}
								}
							}
						}
						if (!parent.isEmpty() && !children.isEmpty()) {
							String systemToken = DbSessionManager.getInstance().getSystemToken();
							Calendar cal = Calendar.getInstance();
							int year = cal.get(Calendar.YEAR);
							String dstPath = "/okm:root/"+String.valueOf(year)+"/"+parent+"/"+children;
							OKMFolder.getInstance().createMissingFolders(systemToken, dstPath);
							OKMDocument.getInstance().move(null, uuid, dstPath);
							OKMDocument.getInstance().lock(null, uuid);							
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean hasPost() {
		return true;
	}

	@Override
	public boolean hasPre() {
		return false;
	}

	@Override
	public String getName() {
		return "Metadata Catalog Test";
	}

	@Override
	public String getParamType00() {
		return Automation.PARAM_TYPE_EMPTY;
	}

	@Override
	public String getParamSrc00() {
		return Automation.PARAM_SOURCE_EMPTY;
	}

	@Override
	public String getParamDesc00() {
		return "";
	}

	@Override
	public String getParamType01() {
		return Automation.PARAM_TYPE_EMPTY;
	}

	@Override
	public String getParamSrc01() {
		return Automation.PARAM_SOURCE_EMPTY;
	}

	@Override
	public String getParamDesc01() {
		return "";
	}

	@Override
	public String getParamType02() {
		return Automation.PARAM_TYPE_EMPTY;
	}

	@Override
	public String getParamSrc02() {
		return Automation.PARAM_SOURCE_EMPTY;
	}

	@Override
	public String getParamDesc02() {
		return "";
	}
	
}
