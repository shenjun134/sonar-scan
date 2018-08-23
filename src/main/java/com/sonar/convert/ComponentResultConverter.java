package com.sonar.convert;

import com.sonar.constant.MetricsEnum;
import com.sonar.model.ComponentResult;
import com.sonar.model.MeasureVO;
import com.sonar.model.MetricsDef;
import com.sonar.model.PeriodVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentResultConverter extends BaseConverter {

    private static final Logger logger = Logger.getLogger(ComponentResultConverter.class);

    interface Constant {
        String dateFormat = "";

        DecimalFormat floatFormat = new DecimalFormat("#,###,###,###,##0.0");

        DecimalFormat intFormat = new DecimalFormat("#,###,###,###,##0");

        DecimalFormat percentageFormat = new DecimalFormat("#0.0");

        int WORK_DUR = 8;

        int HOUR = 60;

        int WORK_DAY = WORK_DUR * HOUR;

        int MINUTE = 60;

        int SECOND = 1000;
    }


    /**
     * @param json
     * @return
     */
    public static List<PeriodVO> convert2Periods(String json) {
        List<PeriodVO> periodVOS = new ArrayList<>();
        if (StringUtils.isBlank(json)) {
            return periodVOS;
        }
        try {
            JSONObject jsonObj = (JSONObject) JSONValue.parse(json);
            JSONArray periods = (JSONArray) jsonObj.get("periods");
            return convert2Period(periods);
        } catch (Exception e) {
            logger.error("convert2Periods error", e);
        }

        return periodVOS;
    }


    public static ComponentResult convert2Result(String json) {
        ComponentResult componentResult = new ComponentResult();
        if (StringUtils.isBlank(json)) {
            return componentResult;
        }
        try {
            JSONObject jsonObj = (JSONObject) JSONValue.parse(json);
            JSONObject component = (JSONObject) jsonObj.get("component");
            if (component == null) {
                return componentResult;
            }
            String id = getString(component, "id");
            String key = getString(component, "key");
            String name = getString(component, "name");
            String qualifier = getString(component, "qualifier");

            componentResult.setId(id);
            componentResult.setKey(key);
            componentResult.setName(name);
            componentResult.setQualifier(qualifier);

            JSONArray measures = (JSONArray) component.get("measures");
            for (Object temp : measures) {
                JSONObject tempObj = (JSONObject) temp;
                MeasureVO measureVO = convert2Measure(tempObj);
                if (measures != null) {
                    componentResult.add(measureVO);
                }

            }


        } catch (Exception e) {
            logger.error("convert2Result error", e);
        }


        return componentResult;
    }


    public static MeasureVO convert2Measure(JSONObject jsonObj) {
        Object metricsO = jsonObj.get("metric");
        Object valueO = jsonObj.get("value");
        if (metricsO == null) {
            return null;
        }
        MeasureVO measureVO = new MeasureVO();
        measureVO.setMetric(metricsO != null ? metricsO.toString() : null);
        measureVO.setValue(valueO != null ? valueO.toString() : null);
        measureVO.setMetricEnum(MetricsEnum.codeOf(measureVO.getMetric()));
        JSONArray periods = (JSONArray) jsonObj.get("periods");

        List<PeriodVO> list = convert2Period(periods);
        if (CollectionUtils.isNotEmpty(list)) {
            measureVO.setPeriods(list);
            if (valueO == null) {
                measureVO.setValue(list.get(0).getValue());
            }
        }

        return measureVO;

    }

    public static List<PeriodVO> convert2Period(JSONArray jsonArr) {
        if (jsonArr == null) {
            return null;
        }
        List<PeriodVO> list = new ArrayList<>();
        for (Object obj : jsonArr) {
            JSONObject json = (JSONObject) obj;
            PeriodVO periodVO = new PeriodVO();
            periodVO.setIndex(getInt(json, "index"));
            periodVO.setValue(getString(json, "value"));

            periodVO.setDate(getDate(json, "date"));
            periodVO.setMode(getString(json, "mode"));
            periodVO.setParameter(getString(json, "parameter"));
            list.add(periodVO);
        }
        return list;
    }

    public static Map<String, MetricsDef> convert2MetricsMap(String json) {
        Map<String, MetricsDef> map = new HashMap<>();
        if (StringUtils.isBlank(json)) {
            return map;
        }
        try {
            JSONObject jsonObj = (JSONObject) JSONValue.parse(json);
            JSONArray metrics = (JSONArray) jsonObj.get("metrics");
            for (Object obj : metrics) {
                MetricsDef def = convert2Metrics((JSONObject) obj);
                if (def == null || StringUtils.isBlank(def.getKey())) {
                    continue;
                }
                map.put(def.getKey(), def);
            }

        } catch (Exception e) {
            logger.error("convert2Periods error", e);
        }

        return map;
    }


    public static MetricsDef convert2Metrics(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        MetricsDef def = new MetricsDef();
        boolean custom = getBoolean(jsonObj, "custom");
        String description = getString(jsonObj, "description");
        int direction = getInt(jsonObj, "direction");
        String domain = getString(jsonObj, "domain");
        boolean hidden = getBoolean(jsonObj, "hidden");
        String id = getString(jsonObj, "id");
        String key = getString(jsonObj, "key");
        String name = getString(jsonObj, "name");
        boolean qualitative = getBoolean(jsonObj, "qualitative");
        String type = getString(jsonObj, "type");
        def.setCustom(custom);
        def.setDescription(description);
        def.setDirection(direction);
        def.setDomain(domain);
        def.setHidden(hidden);
        def.setId(id);
        def.setKey(key);
        def.setName(name);
        def.setQualitative(qualitative);
        def.setType(type);
        return def;
    }

    public static void main(String[] args) {
        String json = "{\"component\":{\"id\":\"AWH-MvU9GrPGX3-L-72X\",\"key\":\"com.statestr.gcth:gcth-project-trunk-parent\",\"name\":\"gcth-project-trunk-parent\",\"qualifier\":\"TRK\",\"measures\":[{\"metric\":\"overall_uncovered_lines\",\"value\":\"141985\",\"periods\":[{\"index\":1,\"value\":\"8009\"},{\"index\":2,\"value\":\"376\"},{\"index\":3,\"value\":\"2694\"}]},{\"metric\":\"uncovered_conditions\",\"value\":\"65568\",\"periods\":[{\"index\":1,\"value\":\"2929\"},{\"index\":2,\"value\":\"167\"},{\"index\":3,\"value\":\"1141\"}]},{\"metric\":\"lines_to_cover\",\"value\":\"226883\",\"periods\":[{\"index\":1,\"value\":\"6660\"},{\"index\":2,\"value\":\"0\"},{\"index\":3,\"value\":\"750\"}]},{\"metric\":\"directories\",\"value\":\"752\",\"periods\":[{\"index\":1,\"value\":\"8\"},{\"index\":2,\"value\":\"0\"},{\"index\":3,\"value\":\"1\"}]},{\"metric\":\"functions\",\"value\":\"38334\",\"periods\":[{\"index\":1,\"value\":\"1220\"},{\"index\":2,\"value\":\"0\"},{\"index\":3,\"value\":\"129\"}]},{\"metric\":\"test_execution_time\",\"value\":\"757222\",\"periods\":[{\"index\":1,\"value\":\"-226123\"},{\"index\":2,\"value\":\"-356772\"},{\"index\":3,\"value\":\"-257483\"}]},{\"metric\":\"new_overall_uncovered_lines\",\"periods\":[{\"index\":1,\"value\":\"5333\"},{\"index\":3,\"value\":\"541\"}]},{\"metric\":\"skipped_tests\",\"value\":\"6\",\"periods\":[{\"index\":1,\"value\":\"0\"},{\"index\":2,\"value\":\"0\"},{\"index\":3,\"value\":\"0\"}]},{\"metric\":\"test_failures\",\"value\":\"11\",\"periods\":[{\"index\":1,\"value\":\"-147\"},{\"index\":2,\"value\":\"6\"},{\"index\":3,\"value\":\"-179\"}]},{\"metric\":\"lines\",\"value\":\"623965\",\"periods\":[{\"index\":1,\"value\":\"17677\"},{\"index\":2,\"value\":\"0\"},{\"index\":3,\"value\":\"1865\"}]},{\"metric\":\"comment_lines\",\"value\":\"46627\",\"periods\":[{\"index\":1,\"value\":\"626\"},{\"index\":2,\"value\":\"0\"},{\"index\":3,\"value\":\"16\"}]},{\"metric\":\"files\",\"value\":\"3894\",\"periods\":[{\"index\":1,\"value\":\"97\"},{\"index\":2,\"value\":\"0\"},{\"index\":3,\"value\":\"12\"}]},{\"metric\":\"test_errors\",\"value\":\"6\",\"periods\":[{\"index\":1,\"value\":\"-705\"},{\"index\":2,\"value\":\"2\"},{\"index\":3,\"value\":\"-748\"}]},{\"metric\":\"line_coverage\",\"value\":\"37.4\",\"periods\":[{\"index\":1,\"value\":\"-1.8000000000000043\"},{\"index\":2,\"value\":\"-0.20000000000000284\"},{\"index\":3,\"value\":\"-1.0\"}]},{\"metric\":\"new_line_coverage\",\"periods\":[{\"index\":1,\"value\":\"34.93167398731088\"},{\"index\":3,\"value\":\"45.18743667679838\"}]},{\"metric\":\"classes\",\"value\":\"4601\",\"periods\":[{\"index\":1,\"value\":\"121\"},{\"index\":2,\"value\":\"0\"},{\"index\":3,\"value\":\"13\"}]},{\"metric\":\"statements\",\"value\":\"195478\",\"periods\":[{\"index\":1,\"value\":\"5538\"},{\"index\":2,\"value\":\"0\"},{\"index\":3,\"value\":\"633\"}]},{\"metric\":\"test_success_density\",\"value\":\"99.8\",\"periods\":[{\"index\":1,\"value\":\"11.399999999999991\"},{\"index\":2,\"value\":\"-0.10000000000000853\"},{\"index\":3,\"value\":\"12.099999999999994\"}]},{\"metric\":\"new_lines_to_cover\",\"periods\":[{\"index\":1,\"value\":\"8196\"},{\"index\":3,\"value\":\"987\"}]},{\"metric\":\"new_uncovered_conditions\",\"periods\":[{\"index\":1,\"value\":\"2272\"},{\"index\":3,\"value\":\"244\"}]},{\"metric\":\"new_branch_coverage\",\"periods\":[{\"index\":1,\"value\":\"29.876543209876544\"},{\"index\":3,\"value\":\"34.40860215053763\"}]},{\"metric\":\"branch_coverage\",\"value\":\"28.2\",\"periods\":[{\"index\":1,\"value\":\"-1.3000000000000007\"},{\"index\":2,\"value\":\"-0.1999999999999993\"},{\"index\":3,\"value\":\"-1.0\"}]}]}}";

        ComponentResult result = convert2Result(json);

        System.out.println(result);
    }


    public static String convert(MeasureVO measure, MetricsDef def) {
        if (def == null) {
            return measure.getValue();
        }
        if (StringUtils.equalsIgnoreCase("INT", def.getType())) {
            return Constant.intFormat.format(NumberUtils.toDouble(measure.getValue(), 0));
        } else if (StringUtils.equalsIgnoreCase("FLOAT", def.getType())) {
            return Constant.floatFormat.format(NumberUtils.toDouble(measure.getValue(), 0));
        } else if (StringUtils.equalsIgnoreCase("PERCENT", def.getType())) {
            return StringUtils.join(new String[]{Constant.percentageFormat.format(NumberUtils.toDouble(measure.getValue(), 0)), "%"});
        } else if (StringUtils.equalsIgnoreCase("DATA", def.getType())) {
            return measure.getValue();
        } else if (StringUtils.equalsIgnoreCase("DISTRIB", def.getType())) {
            return measure.getValue();
        } else if (StringUtils.equalsIgnoreCase("MILLISEC", def.getType())) {
            StringBuilder builder = new StringBuilder();
            int millisec = NumberUtils.toInt(measure.getValue(), 0);
            int min = millisec / (Constant.MINUTE * Constant.SECOND);
            int rest = millisec % (Constant.MINUTE * Constant.SECOND);
            if (min > 10) {
                min = rest > 0 ? min + 1 : min;
                builder.append(min).append("min");
            } else if (millisec > 0) {
                int second = rest / Constant.SECOND;
                int millse = rest % Constant.SECOND;
                if (min > 0) {
                    builder.append(min).append("min");
                }
                if (second > 10) {
                    builder.append(second).append("s");
                } else {
                    if (second > 0) {
                        builder.append(second).append("s");
                    }
                    if (millse > 0) {
                        builder.append(millse).append("ms");
                    }
                }
            } else {
                builder.append("0");
            }

            return builder.toString();

        } else if (StringUtils.equalsIgnoreCase("WORK_DUR", def.getType())) {
            StringBuilder builder = new StringBuilder();
            int minutes = NumberUtils.toInt(measure.getValue(), 0);
            int day = minutes / Constant.WORK_DAY;
            int rest = minutes % Constant.WORK_DAY;
            if (day > 3) {
//                day = rest > 0 ? day + 1 : day;
                builder.append(day).append("d");
            } else if (minutes > 0) {
                int hour = rest / Constant.HOUR;
                int restMins = rest % Constant.HOUR;
                if (day > 0) {
                    builder.append(day).append("d").append(" ");
                }
                if (hour > 0) {
                    builder.append(hour).append("h").append(" ");
                }
                if (restMins > 0) {
                    builder.append(restMins).append("min").append(" ");
                }
            } else {
                builder.append("0");
            }
            return builder.toString();
        }


        //WORK_DUR
        return measure.getValue();
    }
}
