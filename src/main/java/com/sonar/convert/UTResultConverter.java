package com.sonar.convert;

import com.sonar.model.ComponentResult;
import com.sonar.model.UTDetailVO;
import com.sonar.model.UTResult;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class UTResultConverter extends BaseConverter {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(UTResultConverter.class);


    public static UTResult convert2Result(String json) {
        UTResult utResult = new UTResult();
        if (StringUtils.isBlank(json)) {
            return utResult;
        }

        try {
            JSONObject jsonObj = (JSONObject) JSONValue.parse(json);
            JSONArray tests = (JSONArray) jsonObj.get("tests");
            if (tests == null) {
                return utResult;
            }

            for (Object obj : tests) {
                JSONObject temp = (JSONObject) obj;
                UTDetailVO utDetailVO = convert2Result(temp);
                if (utDetailVO != null) {
                    utResult.add(utDetailVO);
                }
            }

        } catch (Exception e) {
            logger.error("convert2Result error", e);
        }

        return utResult;
    }


    public static UTDetailVO convert2Result(JSONObject json) {
        if (json == null) {
            return null;
        }
        UTDetailVO utDetailVO = new UTDetailVO();
        String id = getString(json, "id");
        String name = getString(json, "name");
        String fileId = getString(json, "fileId");
        String fileKey = getString(json, "fileKey");
        String fileName = getString(json, "fileName");
        String status = getString(json, "status");
        int durationInMs = getInt(json, "durationInMs");
        int coveredLines = getInt(json, "coveredLines");
        String stacktrace = getString(json, "stacktrace");


        utDetailVO.setCoveredLines(coveredLines);
        utDetailVO.setDurationInMs(durationInMs);
        utDetailVO.setFileId(fileId);
        utDetailVO.setFileKey(fileKey);
        utDetailVO.setFileName(fileName);
        utDetailVO.setId(id);
        utDetailVO.setName(name);
        utDetailVO.setStacktrace(stacktrace);
        utDetailVO.setStatus(status);

        return utDetailVO;

    }

    public static void main(String[] args) {
        String json = "{\"paging\":{\"pageIndex\":1,\"pageSize\":100,\"total\":3},\"tests\":[{\"id\":\"AWTz1dx7NRUlD3rv7tJj\",\"name\":\"testGetPriorDayPriceFromBrfd\",\"fileId\":\"AWH-M0ulBzQKWkhsDQMA\",\"fileKey\":\"com.xxxxxxxxx.gcth:gcth-usecase-remediation-COA18:src/test/java/com/xxxxxxxxx/gcth/coa18/service/impl/COA18ELPriceVerificationImplServiceTest.java\",\"fileName\":\"src/test/java/com/xxxxxxxxx/gcth/coa18/service/impl/COA18ELPriceVerificationImplServiceTest.java\",\"status\":\"OK\",\"durationInMs\":65,\"coveredLines\":0},{\"id\":\"AWTz1dx7NRUlD3rv7tJi\",\"name\":\"testGetPriorDayPriceFromBpri\",\"fileId\":\"AWH-M0ulBzQKWkhsDQMA\",\"fileKey\":\"com.xxxxxxxxx.gcth:gcth-usecase-remediation-COA18:src/test/java/com/xxxxxxxxx/gcth/coa18/service/impl/COA18ELPriceVerificationImplServiceTest.java\",\"fileName\":\"src/test/java/com/xxxxxxxxx/gcth/coa18/service/impl/COA18ELPriceVerificationImplServiceTest.java\",\"status\":\"ERROR\",\"durationInMs\":47,\"coveredLines\":0,\"message\":\"0 matchers expected, 134 recorded. This exception usually occurs when matchers are mixed with raw values when recording a method:  foo(5, eq(6)); // wrong You need to use no matcher at all or a matcher for every single param:  foo(eq(5), eq(6)); // right  foo(5, 6); // also right\",\"stacktrace\":\"java.lang.IllegalStateException: 0 matchers expected, 134 recorded.\\nThis exception usually occurs when matchers are mixed with raw values when recording a method:\\n\\tfoo(5, eq(6));\\t// wrong\\nYou need to use no matcher at all or a matcher for every single param:\\n\\tfoo(eq(5), eq(6));\\t// right\\n\\tfoo(5, 6);\\t// also right\\n\\tat org.easymock.internal.ExpectedInvocation.createMissingMatchers(ExpectedInvocation.java:47)\\n\\tat org.easymock.internal.ExpectedInvocation.<init>(ExpectedInvocation.java:40)\\n\\tat org.easymock.internal.RecordState.invoke(RecordState.java:78)\\n\\tat org.easymock.internal.MockInvocationHandler.invoke(MockInvocationHandler.java:40)\\n\\tat org.easymock.internal.ObjectMethodsFilter.invoke(ObjectMethodsFilter.java:85)\\n\\tat org.easymock.internal.ClassProxyFactory$MockMethodInterceptor.intercept(ClassProxyFactory.java:94)\\n\\tat com.xxxxxxxxx.gcth.application.service.SharedServices$$EnhancerByCGLIB$$1fee27aa.getPersistentService(<generated>)\\n\\tat com.xxxxxxxxx.gcth.coa18.test.utils.ProcessorUnitTestBase.mockSharedServices(ProcessorUnitTestBase.java:186)\\n\\tat com.xxxxxxxxx.gcth.coa18.test.utils.ProcessorUnitTestBase.init(ProcessorUnitTestBase.java:126)\\n\\tat com.xxxxxxxxx.gcth.coa18.service.impl.COA18ELPriceVerificationImplServiceTest.testGetPriorDayPriceFromBpri(COA18ELPriceVerificationImplServiceTest.java:53)\\n\\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\\n\\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\\n\\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\\n\\tat java.lang.reflect.Method.invoke(Method.java:497)\\n\\tat org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)\\n\\tat org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)\\n\\tat org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)\\n\\tat org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)\\n\\tat org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:28)\\n\\tat org.junit.internal.runners.statements.RunAfters.evaluate(RunAfters.java:31)\\n\\tat org.junit.runners.BlockJUnit4ClassRunner.runNotIgnored(BlockJUnit4ClassRunner.java:79)\\n\\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:71)\\n\\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:49)\\n\\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:193)\\n\\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:52)\\n\\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:191)\\n\\tat org.junit.runners.ParentRunner.access$000(ParentRunner.java:42)\\n\\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:184)\\n\\tat org.junit.runners.ParentRunner.run(ParentRunner.java:236)\\n\\tat org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:252)\\n\\tat org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:141)\\n\\tat org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:112)\\n\\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\\n\\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\\n\\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\\n\\tat java.lang.reflect.Method.invoke(Method.java:497)\\n\\tat org.apache.maven.surefire.util.ReflectionUtils.invokeMethodWithArray(ReflectionUtils.java:189)\\n\\tat org.apache.maven.surefire.booter.ProviderFactory$ProviderProxy.invoke(ProviderFactory.java:165)\\n\\tat org.apache.maven.surefire.booter.ProviderFactory.invokeProvider(ProviderFactory.java:85)\\n\\tat org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:115)\\n\\tat org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:75)\\n\"},{\"id\":\"AWTz1dx7NRUlD3rv7tJk\",\"name\":\"testGetPriorDayPriceReturnNull\",\"fileId\":\"AWH-M0ulBzQKWkhsDQMA\",\"fileKey\":\"com.xxxxxxxxx.gcth:gcth-usecase-remediation-COA18:src/test/java/com/xxxxxxxxx/gcth/coa18/service/impl/COA18ELPriceVerificationImplServiceTest.java\",\"fileName\":\"src/test/java/com/xxxxxxxxx/gcth/coa18/service/impl/COA18ELPriceVerificationImplServiceTest.java\",\"status\":\"OK\",\"durationInMs\":1,\"coveredLines\":0}]}";

        UTResult result = convert2Result(json);

        System.out.println(result);
    }
}
