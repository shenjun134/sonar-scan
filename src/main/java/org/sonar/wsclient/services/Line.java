package org.sonar.wsclient.services;

import java.util.Date;
import java.util.List;

import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Model;

public class Line extends Model{
	private List<Measure> measures;
	public List<Measure> getMeasures() {
		return measures;
	}

	public void setMeasures(List<Measure> measures) {
		this.measures = measures;
	}
	private Integer id;
	  private String key;
	  private String name;
	  public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Integer getCopy() {
		return copy;
	}

	public void setCopy(Integer copy) {
		this.copy = copy;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	private String longName;
	  private String scope;
	  private String qualifier;
	  private String language;
	  private String version;
	  private Integer copy;
	  private String description;
	  private Date date;
}
