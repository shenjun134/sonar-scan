/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.wsclient.unmarshallers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.sonar.wsclient.services.Line;
import org.sonar.wsclient.services.Source;
import org.sonar.wsclient.services.WSUtils;

import java.util.Iterator;
import java.util.Map;

public class LineUnmarshaller extends AbstractUnmarshaller<Line> {

	@Override
	protected Line parse(Object elt) {
		System.out.println(elt);
		WSUtils ut = WSUtils.getINSTANCE();
		JSONArray arr = (JSONArray)ut.parse(elt.toString());
		Iterator it = arr.iterator();
		while(it.hasNext()){
			Object item = it.next();
			Object single = ut.parse(item.toString());
			System.out.println(single);
			
			System.out.println("ScmAuthor : " + ut.getString(single, "scmAuthor"));
			
			System.out.println("line : " + ut.getInteger(single, "line"));
			
		}
		System.out.println(arr);
		return null;
	}
}
