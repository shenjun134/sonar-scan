package com.sonar.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class TeamProperties {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(TeamProperties.class);
    private Map<String, List<String>> teamMap = new HashMap<>();
    private MemberProperties memberProperties;

    public TeamProperties() {
        String userDir = System.getProperty("user.dir");
        InputStream is = null;
        try {
            is = new FileInputStream(userDir + "/team.properties");
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        }
        if (is == null) {
            logger.error("no team.properties found...");
            System.exit(1);
        }
        try {
            Properties prop = new Properties();
            prop.load(is);
            Enumeration enumKeys = prop.keys();
            while (enumKeys.hasMoreElements()) {
                String key = (String) enumKeys.nextElement();
                String value = prop.getProperty(key);
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                List<String> list = new ArrayList<>(Arrays.asList(value.replace(" ", "").split(",")));
                teamMap.put(key, list);
            }
            logger.info("load team properties finished - " + teamMap);
        } catch (IOException e) {
            logger.error("load team properties error", e);
            System.exit(1);
        }
        memberProperties = new MemberProperties();
    }

    public Map<String, List<String>> getTeamMap() {
        return teamMap;
    }

    /**
     * @param members
     * @return
     */
    public String convert2Employ(List<String> members) {
        if (CollectionUtils.isEmpty(members)) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String m : members) {
            String name = convert2Employ(m);
            stringBuilder.append(name).append("|");
        }
        return stringBuilder.toString();
    }

    /**
     * @param m
     * @return
     */
    public String convert2Employ(String m) {
        MemberDO memberDO = this.memberProperties.getMemberMap().get(m);
        String name = "";
        if (memberDO != null) {
            name = memberDO.getName();
        }
        if (StringUtils.isBlank(name)) {
            name = m;
        }
        return name;
    }

    public String convert2EmployInfo(String m) {
        MemberDO memberDO = this.memberProperties.getMemberMap().get(m);
        String name = "";
        if (memberDO != null && StringUtils.isNotBlank(memberDO.getName())) {
            name = memberDO.getName() + "(" + m + ")";
        }
        if (StringUtils.isBlank(name)) {
            name = m;
        }
        return name;
    }


    public void setTeamMap(Map<String, List<String>> teamMap) {
        this.teamMap = teamMap;
    }

    public MemberProperties getMemberProperties() {
        return memberProperties;
    }

    public void setMemberProperties(MemberProperties memberProperties) {
        this.memberProperties = memberProperties;
    }

    @Override
    public String toString() {
        return "TeamProperties{" +
                "teamMap=" + teamMap +
                ", memberProperties=" + memberProperties +
                '}';
    }

    public static void main(String[] args) {
        System.out.println(Arrays.asList("".replace(" ", "").split(",")));
    }
}
