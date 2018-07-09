package com.sonar.model;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MemberProperties {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(MemberProperties.class);

    private Map<String, MemberDO> memberMap = new HashMap<>();

    private Map<String, String> tagMap = new HashMap<>();


    public MemberProperties() {
        String userDir = System.getProperty("user.dir");
        InputStream is = null;
        try {
            is = new FileInputStream(userDir + "/member.properties");
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        }
        if (is == null) {
            logger.error("no member.properties found...");
            System.exit(1);
        }
        try {
            Properties prop = new Properties();
            prop.load(is);
            Enumeration enumKeys = prop.keys();
            while (enumKeys.hasMoreElements()) {
                String key = (String) enumKeys.nextElement();
                String value = prop.getProperty(key);
                MemberDO member = new MemberDO(value, key);
                memberMap.put(member.getEmployeeId(), member);
                for (String tag : member.getTag()) {
                    tagMap.put(tag, member.getEmployeeId());
                }
            }
            logger.info("load member properties finished - " + this);
        } catch (IOException e) {
            logger.error("load member properties error", e);
            System.exit(1);
        }
    }

    public Map<String, MemberDO> getMemberMap() {
        return memberMap;
    }

    public void setMemberMap(Map<String, MemberDO> memberMap) {
        this.memberMap = memberMap;
    }

    public Map<String, String> getTagMap() {
        return tagMap;
    }

    public void setTagMap(Map<String, String> tagMap) {
        this.tagMap = tagMap;
    }

    @Override
    public String toString() {
        return "MemberProperties{" +
                "memberMap=" + memberMap +
                ", tagMap=" + tagMap +
                '}';
    }

    public static void main(String[] args){
        System.out.println(new MemberProperties());
    }
}
