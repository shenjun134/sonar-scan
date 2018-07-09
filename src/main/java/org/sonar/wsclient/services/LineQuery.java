package org.sonar.wsclient.services;

import org.sonar.wsclient.services.Query;

public class LineQuery extends Query<Line> {

	@Override
	public Class<Line> getModelClass() {
		 return Line.class;
	}

	String baseComponentKey;
	public String[] getMetricKeys() {
		return metricKeys;
	}

	public void setMetricKeys(String[] metricKeys) {
		this.metricKeys = metricKeys;
	}

	String metricKeys[];

	
	//public static final String BASE_URL = "/api/measures/component_tree";
	public static final String BASE_URL = "/api/sources/lines";
	  private String resourceKeyOrId;
	  private int from = 0;
	  private int to = 0;

	  public LineQuery(String baseComponentKey) {
	    this.baseComponentKey = baseComponentKey;
	  }

	  public String getBaseComponentKey() {
	    return baseComponentKey;
	  }

	  public LineQuery setBaseComponentKey(String baseComponentKey) {
	    this.baseComponentKey = baseComponentKey;
	    return this;
	  }

	  public int getFrom() {
	    return from;
	  }

	  /**
	   * Get only a few lines
	   * 
	   * @param from Index of the first line, starts to 1
	   * @param excludedTo Index of the last line (excluded).
	   */
	  public LineQuery setFromLineToLine(int from, int excludedTo) {
	    this.from = from;
	    this.to = excludedTo;
	    return this;
	  }

	  public LineQuery setLinesFromLine(int from, int length) {
	    this.from = from;
	    this.to = from + length;
	    return this;
	  }

	  public int getTo() {
	    return to;
	  }

	  @Override
	  public String getUrl() {
	    StringBuilder url = new StringBuilder(BASE_URL);
	    url.append('?');
	    appendUrlParameter(url, "key", baseComponentKey);
	   // appendUrlParameter(url, "metrics", metrics);
	    return url.toString();
	  }

	  public static LineQuery create(String baseComponentKey) {
	    return new LineQuery(baseComponentKey);
	  }
	  
	  private String[] metrics;
	  
}
