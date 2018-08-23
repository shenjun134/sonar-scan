package com.sonar.convert;

import com.sonar.constant.MetricsEnum;
import com.sonar.model.PageDO;
import com.sonar.model.ProjectDO;
import com.sonar.model.ProjectUTDO;
import com.sonar.model.UTDO;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class UTConverter extends BaseConverter {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(UTConverter.class);

    //{"component":{"id":"AWRvEVU-v78epQNUIodW","key":"com.ssc.gcpv:gold-copy-where-my-trade:WEB-INF/test/com/ssc/wmt/mytrade/dao/TsmJumpLinkDaoTest.java","name":"TsmJumpLinkDaoTest.java","qualifier":"UTS","path":"WEB-INF/test/com/ssc/wmt/mytrade/dao/TsmJumpLinkDaoTest.java","language":"java","measures":[{"metric":"test_success_density","value":"93.3"},{"metric":"tests","value":"30"},{"metric":"test_failures","value":"2"}]},"periods":[]}
    public static UTDO convert(String json) {
        UTDO utdo = new UTDO();
        if (StringUtils.isBlank(json)) {
            return utdo;
        }
        try {
            JSONObject jsonObj = (JSONObject) JSONValue.parse(json);
            if (jsonObj == null) {
                logger.warn("can not parse to json obj - " + json);
                return utdo;
            }
            JSONObject component = (JSONObject) jsonObj.get("component");
            if (component == null) {
                logger.warn("no component - " + json);
                return utdo;
            }

            JSONArray measures = (JSONArray) component.get("measures");
            if (measures == null) {
                logger.warn("no measures - " + json);
                return utdo;
            }

            for (Object temp : measures) {
                JSONObject tempObj = (JSONObject) temp;
                Object metricsO = tempObj.get("metric");
                Object valueO = tempObj.get("value");
                if (metricsO == null || valueO == null) {
                    continue;
                }
                if (MetricsEnum.UT_SUCC_RATE.getCode().equalsIgnoreCase(metricsO.toString())) {
                    utdo.setSuccRate(NumberUtils.toFloat(valueO.toString(), 0));
                }
                if (MetricsEnum.UT_COUNT.getCode().equalsIgnoreCase(metricsO.toString())) {
                    utdo.setTotal(NumberUtils.toInt(valueO.toString(), 0));
                }
                if (MetricsEnum.UT_FAIL_COUNT.getCode().equalsIgnoreCase(metricsO.toString())) {
                    utdo.setFailure(NumberUtils.toInt(valueO.toString(), 0));
                }

            }


        } catch (Exception e) {
            logger.error("convert json error - " + json, e);
        }
        return utdo;
    }

    public static ProjectUTDO firstConvert(String json) {
        ProjectUTDO projectUTDO = new ProjectUTDO();
        if (StringUtils.isBlank(json)) {
            return projectUTDO;
        }
        try {
            JSONObject jsonObj = (JSONObject) JSONValue.parse(json);
            if (jsonObj == null) {
                logger.warn("can not parse to json obj - " + json);
                return projectUTDO;
            }
            JSONObject pageObj = (JSONObject) jsonObj.get("paging");
            projectUTDO.setPageDO(convert2Page(pageObj));

            JSONObject baseComponentObj = (JSONObject) jsonObj.get("baseComponent");
            UTDO baseComponent = convertComponent(baseComponentObj);
            projectUTDO.setBaseComponent(baseComponent);

            JSONArray componentsObjArr = (JSONArray) jsonObj.get("components");
            if (componentsObjArr == null) {
                logger.warn("no components - " + json);
                return projectUTDO;
            }
            for (Object obj : componentsObjArr) {
                JSONObject tempObj = (JSONObject) obj;
                UTDO component = convertComponent(tempObj);
                projectUTDO.add(component);
            }

        } catch (Exception e) {
            logger.error("firstConvert json error - " + json, e);
        }
        return projectUTDO;
    }


    private static UTDO convertComponent(JSONObject jsonObject) {
        UTDO utdo = new UTDO();
        if (jsonObject == null) {
            return utdo;
        }
        try {
            String id = getString(jsonObject, "id");
            String key = getString(jsonObject, "key");
            String name = getString(jsonObject, "name");
            String qualifier = getString(jsonObject, "qualifier");
            String path = getString(jsonObject, "path");
            String language = getString(jsonObject, "language");

            utdo.setId(id);
            utdo.setKee(key);
            utdo.setName(name);
            utdo.setLanguage(language);
            utdo.setQualifier(qualifier);
            utdo.setPath(path);

            JSONArray measures = (JSONArray) jsonObject.get("measures");
            if (measures == null) {
                logger.warn("no measures - " + jsonObject);
                return utdo;
            }

            for (Object temp : measures) {
                JSONObject tempObj = (JSONObject) temp;
                Object metricsO = tempObj.get("metric");
                Object valueO = tempObj.get("value");
                if (metricsO == null || valueO == null) {
                    continue;
                }
                if (MetricsEnum.UT_SUCC_RATE.getCode().equalsIgnoreCase(metricsO.toString())) {
                    utdo.setSuccRate(NumberUtils.toFloat(valueO.toString(), 0));
                }
                if (MetricsEnum.UT_COUNT.getCode().equalsIgnoreCase(metricsO.toString())) {
                    utdo.setTotal(NumberUtils.toInt(valueO.toString(), 0));
                }
                if (MetricsEnum.UT_FAIL_COUNT.getCode().equalsIgnoreCase(metricsO.toString())) {
                    utdo.setFailure(NumberUtils.toInt(valueO.toString(), 0));
                }
                if (MetricsEnum.UT_ERROR_COUNT.getCode().equalsIgnoreCase(metricsO.toString())) {
                    utdo.setError(NumberUtils.toInt(valueO.toString(), 0));
                }
            }
        } catch (Exception e) {
            logger.error("convertComponent json error - " + jsonObject, e);
        }

        return utdo;

    }

    private static PageDO convert2Page(JSONObject pageObj) {
        PageDO pageDO = new PageDO();
        if (pageObj == null) {
            return pageDO;
        }
        try {
            pageDO.setPageIndex(getInt(pageObj, "pageIndex"));
            pageDO.setPageSize(getInt(pageObj, "pageSize"));
            pageDO.setTotal(getInt(pageObj, "total"));
        } catch (Exception e) {
            logger.error("convert2Page json error - " + pageObj, e);
        }
        return pageDO;
    }


    /**
     * @param projectUTDO
     * @param json
     */
    public static void convert(ProjectUTDO projectUTDO, String json) {
        if (StringUtils.isBlank(json)) {
            return;
        }
        try {
            JSONObject jsonObj = (JSONObject) JSONValue.parse(json);
            if (jsonObj == null) {
                logger.warn("can not parse to json obj - " + json);
                return;
            }
            JSONObject pageObj = (JSONObject) jsonObj.get("paging");
            projectUTDO.setPageDO(convert2Page(pageObj));

            JSONObject baseComponentObj = (JSONObject) jsonObj.get("baseComponent");
            UTDO baseComponent = convertComponent(baseComponentObj);
            projectUTDO.setBaseComponent(baseComponent);

            JSONArray componentsObjArr = (JSONArray) jsonObj.get("components");
            if (componentsObjArr == null) {
                logger.warn("no components - " + json);
                return;
            }
            for (Object obj : componentsObjArr) {
                JSONObject tempObj = (JSONObject) obj;
                UTDO component = convertComponent(tempObj);
                projectUTDO.add(component);
            }
        } catch (Exception e) {
            logger.error("convert json error - " + json, e);
        }

    }


    public static void main(String[] args) {
//        String temp = "{\"component\":{\"id\":\"AWRvEVU-v78epQNUIodW\",\"key\":\"com.ssc.gcpv:gold-copy-where-my-trade:WEB-INF/test/com/ssc/wmt/mytrade/dao/TsmJumpLinkDaoTest.java\",\"name\":\"TsmJumpLinkDaoTest.java\",\"qualifier\":\"UTS\",\"path\":\"WEB-INF/test/com/ssc/wmt/mytrade/dao/TsmJumpLinkDaoTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_success_density\",\"value\":\"93.3\"},{\"metric\":\"tests\",\"value\":\"30\"},{\"metric\":\"test_failures\",\"value\":\"2\"}]},\"periods\":[]}";
//
//
//        System.out.println(convert(temp));

        String json = "{\"paging\":{\"pageIndex\":1,\"pageSize\":50,\"total\":1775},\"baseComponent\":{\"id\":\"AWH-MvU9GrPGX3-L-72X\",\"key\":\"com.statestr.gcth:gcth-project-trunk-parent\",\"name\":\"gcth-project-trunk-parent\",\"qualifier\":\"TRK\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"6\",\"periods\":[{\"index\":1,\"value\":\"-705\"},{\"index\":2,\"value\":\"0\"},{\"index\":3,\"value\":\"-748\"}]},{\"metric\":\"test_failures\",\"value\":\"4\",\"periods\":[{\"index\":1,\"value\":\"-154\"},{\"index\":2,\"value\":\"-7\"},{\"index\":3,\"value\":\"-186\"}]},{\"metric\":\"test_success_density\",\"value\":\"99.9\",\"periods\":[{\"index\":1,\"value\":\"11.5\"},{\"index\":2,\"value\":\"0.10000000000000853\"},{\"index\":3,\"value\":\"12.200000000000003\"}]}]},\"components\":[{\"id\":\"AWH-M0rEBzQKWkhsDPaR\",\"key\":\"com.statestr.gcth:gcth-usecase-inbound:src/test/java/com/statestr/gcth/inbound/processor/transformation/DefaultProcessorTest.java\",\"name\":\"DefaultProcessorTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/inbound/processor/transformation/DefaultProcessorTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"1\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"0.0\"}]},{\"id\":\"AWH-M0qdBzQKWkhsDPOz\",\"key\":\"com.statestr.gcth:gcth-usecase-common:src/test/java/com/statestr/gcth/usecase/tsm/pos/coll/processor/STX42TSMFundRollProcessorTest.java\",\"name\":\"STX42TSMFundRollProcessorTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/tsm/pos/coll/processor/STX42TSMFundRollProcessorTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"2\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"0.0\"}]},{\"id\":\"AWH-M0srBzQKWkhsDP3X\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-TRD05:src/test/java/com/statestr/gcth/usecase/trd05/domain/TRD05ReferenceTest.java\",\"name\":\"TRD05ReferenceTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/trd05/domain/TRD05ReferenceTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_failures\",\"value\":\"1\"},{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"0.0\"}]},{\"id\":\"AWH-M0ulBzQKWkhsDQMA\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-COA18:src/test/java/com/statestr/gcth/coa18/service/impl/COA18ELPriceVerificationImplServiceTest.java\",\"name\":\"COA18ELPriceVerificationImplServiceTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/coa18/service/impl/COA18ELPriceVerificationImplServiceTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"1\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"66.7\"}]},{\"id\":\"AWH-M0sqBzQKWkhsDP27\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-TRD03:src/test/java/com/statestr/gcth/usecase/TRD03/processor/TRD03OpenTradeBindTest.java\",\"name\":\"TRD03OpenTradeBindTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/TRD03/processor/TRD03OpenTradeBindTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_failures\",\"value\":\"1\"},{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"66.7\"}]},{\"id\":\"AWH-M0sHBzQKWkhsDPtU\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-INI05:src/test/java/com/statestr/gcth/usecase/ini05/processor/INI05BGIRProcessorTest.java\",\"name\":\"INI05BGIRProcessorTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/ini05/processor/INI05BGIRProcessorTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_failures\",\"value\":\"1\"},{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"80.0\"}]},{\"id\":\"AWH-M0usBzQKWkhsDQOH\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-COA20:src/test/java/com/statestr/gcth/coa20/service/impl/MT543ShareParServiceImplTest.java\",\"name\":\"MT543ShareParServiceImplTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/coa20/service/impl/MT543ShareParServiceImplTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"1\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"88.9\"}]},{\"id\":\"AWH-M0uTBzQKWkhsDQGw\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-POSITION-COMMON:src/test/java/com/statestr/gcth/usecase/remediation/POSITION/COMMON/PRCDailyProcessorTest.java\",\"name\":\"PRCDailyProcessorTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/remediation/POSITION/COMMON/PRCDailyProcessorTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_failures\",\"value\":\"1\"},{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"90.0\"}]},{\"id\":\"AWH-M0ulBzQKWkhsDQMO\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-COA18:src/test/java/com/statestr/gcth/coa19/processor/COA19CartPriceVerificationProcessorTest.java\",\"name\":\"COA19CartPriceVerificationProcessorTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/coa19/processor/COA19CartPriceVerificationProcessorTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"1\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"90.9\"}]},{\"id\":\"AWH-M0uHBzQKWkhsDQDc\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ASDI01:src/test/java/org/gcth/usecase/remediation/ASDI01/utils/AbstractASDI01ProcessorUnitTest.java\",\"name\":\"AbstractASDI01ProcessorUnitTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/org/gcth/usecase/remediation/ASDI01/utils/AbstractASDI01ProcessorUnitTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uvBzQKWkhsDQO_\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-COA21:src/test/java/com/statestr/gcth/coa21/processor/AbstractComponnentTest.java\",\"name\":\"AbstractComponnentTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/coa21/processor/AbstractComponnentTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0rmBzQKWkhsDPjx\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-INI01:src/test/java/com/statestr/gcth/usecase/service/AbstractEnrichServiceTest.java\",\"name\":\"AbstractEnrichServiceTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/service/AbstractEnrichServiceTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0rDBzQKWkhsDPZ5\",\"key\":\"com.statestr.gcth:gcth-usecase-inbound:src/test/java/com/statestr/gcth/inbound/processor/AbstractInboundTest.java\",\"name\":\"AbstractInboundTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/inbound/processor/AbstractInboundTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uDBzQKWkhsDQCY\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-STX15:src/test/java/com/statestr/gcth/usecase/stx15/linkage/support/AbstractLinkFilterTest.java\",\"name\":\"AbstractLinkFilterTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/stx15/linkage/support/AbstractLinkFilterTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0rmBzQKWkhsDPjy\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-INI01:src/test/java/com/statestr/gcth/usecase/service/AbstractMonthlyInterestServiceTest.java\",\"name\":\"AbstractMonthlyInterestServiceTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/service/AbstractMonthlyInterestServiceTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0rxBzQKWkhsDPm3\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-NSA02:src/test/java/com/statestr/gcth/usecase/NS02/utils/AbstractNSA02ProcessorUnitTest.java\",\"name\":\"AbstractNSA02ProcessorUnitTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/NS02/utils/AbstractNSA02ProcessorUnitTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uHBzQKWkhsDQDd\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ASDI01:src/test/java/org/gcth/usecase/remediation/ASDI01/utils/AbstractTestfulAction.java\",\"name\":\"AbstractTestfulAction.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/org/gcth/usecase/remediation/ASDI01/utils/AbstractTestfulAction.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uDBzQKWkhsDQCU\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-STX15:src/test/java/com/statestr/gcth/usecase/stx15/integration/AbstractTestfulAction.java\",\"name\":\"AbstractTestfulAction.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/stx15/integration/AbstractTestfulAction.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0ssBzQKWkhsDP3h\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-TRD05:src/test/java/com/statestr/gcth/usecase/trd05/utils/AbstractTRD05ProcessorUnitTest.java\",\"name\":\"AbstractTRD05ProcessorUnitTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/trd05/utils/AbstractTRD05ProcessorUnitTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uABzQKWkhsDQBZ\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-CASH12:src/test/java/com/statestr/gcth/usecase/cash12/command/cics/AcccCommandTest.java\",\"name\":\"AcccCommandTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/cash12/command/cics/AcccCommandTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uGBzQKWkhsDQDN\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ASDI01:src/test/java/com/statestr/gcth/asdi01/service/AccountingLinkingRuleTest.java\",\"name\":\"AccountingLinkingRuleTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/asdi01/service/AccountingLinkingRuleTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0qZBzQKWkhsDPNf\",\"key\":\"com.statestr.gcth:gcth-usecase-common:src/test/java/com/statestr/gcth/usecase/common/dao/impl/AccountingMatchGroupDaoImplTest.java\",\"name\":\"AccountingMatchGroupDaoImplTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/common/dao/impl/AccountingMatchGroupDaoImplTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWIsSe3Xr8YXmN0YV8oP\",\"key\":\"com.statestr.gcth:gcth-remediation-gbmesb:src/test/java/com/statestr/gcth/usecase/gbmessb/processor/AccountLookupDistributionProcessorTest.java\",\"name\":\"AccountLookupDistributionProcessorTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/gbmessb/processor/AccountLookupDistributionProcessorTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uABzQKWkhsDQBV\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-CASH12:src/test/java/com/statestr/gcth/usecase/cash12/command/acknowledge/AcknowledgeCommandTest.java\",\"name\":\"AcknowledgeCommandTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/cash12/command/acknowledge/AcknowledgeCommandTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uQBzQKWkhsDQF2\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-REPO01:src/test/java/com/statestr/gcth/usecase/repo01/comparator/ActualSettledShareComparatorTest.java\",\"name\":\"ActualSettledShareComparatorTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/repo01/comparator/ActualSettledShareComparatorTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uKBzQKWkhsDQER\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-OFFB07:src/test/java/org/gcth/usecase/remediation/OFFB07/test/AllTests.java\",\"name\":\"AllTests.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/org/gcth/usecase/remediation/OFFB07/test/AllTests.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0r4BzQKWkhsDPo8\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-WIR01:src/test/java/com/statestr/gcth/usecase/wir01/model/AmountThresholdTest.java\",\"name\":\"AmountThresholdTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/wir01/model/AmountThresholdTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uOBzQKWkhsDQFR\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-REPO01:src/test/java/com/statestr/gcth/usecase/repo01/annotate/AnnotationExtractorTest.java\",\"name\":\"AnnotationExtractorTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/repo01/annotate/AnnotationExtractorTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uGBzQKWkhsDQDO\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ASDI01:src/test/java/com/statestr/gcth/asdi01/service/AnnotationParserTest.java\",\"name\":\"AnnotationParserTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/asdi01/service/AnnotationParserTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uOBzQKWkhsDQFV\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-REPO01:src/test/java/com/statestr/gcth/usecase/repo01/annotate/type/AnnotationTypeTest.java\",\"name\":\"AnnotationTypeTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/repo01/annotate/type/AnnotationTypeTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0qYBzQKWkhsDPNQ\",\"key\":\"com.statestr.gcth:gcth-usecase-common:src/test/java/com/statestr/gcth/usecase/common/coa/processor/AOFundPostBackProcessorTest.java\",\"name\":\"AOFundPostBackProcessorTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/common/coa/processor/AOFundPostBackProcessorTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0vVBzQKWkhsDQaE\",\"key\":\"com.statestr.gcth:gcth-usecase-distribution-STX09:src/test/java/gcth_usecase_distribution_STX09/gcth_usecase_distribution_STX09/AppTest.java\",\"name\":\"AppTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/gcth_usecase_distribution_STX09/gcth_usecase_distribution_STX09/AppTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uUBzQKWkhsDQG_\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-UT01:src/test/java/org/gcth/usecase/remediation/UT01/AppTest.java\",\"name\":\"AppTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/org/gcth/usecase/remediation/UT01/AppTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uHBzQKWkhsDQDZ\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ASDI01:src/test/java/org/gcth/usecase/remediation/ASDI01/AppTest.java\",\"name\":\"AppTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/org/gcth/usecase/remediation/ASDI01/AppTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0rdBzQKWkhsDPhX\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ICL02:src/test/java/com/statestr/gcth/usecase/icl02/AQJMSTest.java\",\"name\":\"AQJMSTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/usecase/icl02/AQJMSTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uSBzQKWkhsDQGo\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-POSITION-COMMON:src/test/java/AQJMSTest.java\",\"name\":\"AQJMSTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/AQJMSTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uGBzQKWkhsDQDJ\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ASDI01:src/test/java/com/statestr/gcth/asdi01/service/ASDI01CicsServiceTest.java\",\"name\":\"ASDI01CicsServiceTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/asdi01/service/ASDI01CicsServiceTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uFBzQKWkhsDQC1\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ASDI01:src/test/java/com/statestr/gcth/asdi01/processor/ASDI01EndProcessorTest.java\",\"name\":\"ASDI01EndProcessorTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/asdi01/processor/ASDI01EndProcessorTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uFBzQKWkhsDQC2\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ASDI01:src/test/java/com/statestr/gcth/asdi01/processor/ASDI01InitProcessorTest.java\",\"name\":\"ASDI01InitProcessorTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/asdi01/processor/ASDI01InitProcessorTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uGBzQKWkhsDQDK\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ASDI01:src/test/java/com/statestr/gcth/asdi01/service/ASDI01LinkTradeServiceImplTest.java\",\"name\":\"ASDI01LinkTradeServiceImplTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/asdi01/service/ASDI01LinkTradeServiceImplTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uGBzQKWkhsDQDD\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ASDI01:src/test/java/com/statestr/gcth/asdi01/processor/canceltrade/ASDI01MSXLProcessorTest.java\",\"name\":\"ASDI01MSXLProcessorTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/asdi01/processor/canceltrade/ASDI01MSXLProcessorTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uFBzQKWkhsDQC3\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ASDI01:src/test/java/com/statestr/gcth/asdi01/processor/ASDI01MyViewProcessorTest.java\",\"name\":\"ASDI01MyViewProcessorTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/asdi01/processor/ASDI01MyViewProcessorTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uHBzQKWkhsDQDb\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ASDI01:src/test/java/org/gcth/usecase/remediation/ASDI01/utils/ASDI01ProcessorUnitTestBase.java\",\"name\":\"ASDI01ProcessorUnitTestBase.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/org/gcth/usecase/remediation/ASDI01/utils/ASDI01ProcessorUnitTestBase.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uGBzQKWkhsDQDL\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ASDI01:src/test/java/com/statestr/gcth/asdi01/service/ASDI01ReconcilingServiceTest.java\",\"name\":\"ASDI01ReconcilingServiceTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/asdi01/service/ASDI01ReconcilingServiceTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uFBzQKWkhsDQC4\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ASDI01:src/test/java/com/statestr/gcth/asdi01/processor/ASDI01RecoveryProcessorTest.java\",\"name\":\"ASDI01RecoveryProcessorTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/asdi01/processor/ASDI01RecoveryProcessorTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uFBzQKWkhsDQC-\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ASDI01:src/test/java/com/statestr/gcth/asdi01/processor/buyorsell/ASDI01REDBBookBackProcessorTest.java\",\"name\":\"ASDI01REDBBookBackProcessorTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/asdi01/processor/buyorsell/ASDI01REDBBookBackProcessorTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uGBzQKWkhsDQC_\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ASDI01:src/test/java/com/statestr/gcth/asdi01/processor/buyorsell/ASDI01REDBProcessorTest.java\",\"name\":\"ASDI01REDBProcessorTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/asdi01/processor/buyorsell/ASDI01REDBProcessorTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uGBzQKWkhsDQDE\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ASDI01:src/test/java/com/statestr/gcth/asdi01/processor/canceltrade/ASDI01REDCProcessorTest.java\",\"name\":\"ASDI01REDCProcessorTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/asdi01/processor/canceltrade/ASDI01REDCProcessorTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uGBzQKWkhsDQDF\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ASDI01:src/test/java/com/statestr/gcth/asdi01/processor/canceltrade/ASDI01REDCS4ProcessorTest.java\",\"name\":\"ASDI01REDCS4ProcessorTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/asdi01/processor/canceltrade/ASDI01REDCS4ProcessorTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]},{\"id\":\"AWH-M0uGBzQKWkhsDQDA\",\"key\":\"com.statestr.gcth:gcth-usecase-remediation-ASDI01:src/test/java/com/statestr/gcth/asdi01/processor/buyorsell/ASDI01REDSProcessorTest.java\",\"name\":\"ASDI01REDSProcessorTest.java\",\"qualifier\":\"UTS\",\"path\":\"src/test/java/com/statestr/gcth/asdi01/processor/buyorsell/ASDI01REDSProcessorTest.java\",\"language\":\"java\",\"measures\":[{\"metric\":\"test_errors\",\"value\":\"0\"},{\"metric\":\"test_failures\",\"value\":\"0\"},{\"metric\":\"test_success_density\",\"value\":\"100.0\"}]}]}";

        ProjectUTDO projectUTDO = firstConvert(json);

        System.out.println(projectUTDO);

        for (UTDO utdo : projectUTDO.getComponentMap().values()) {
            if (utdo.getFailure() > 0) {
                System.out.println(utdo);
                continue;
            }
            if (utdo.getError() > 0) {
                System.out.println(utdo);
                continue;
            }
        }
    }


}
