package com.sonar.base;

import com.sonar.model.MemberDO;
import com.sonar.util.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

public class MemberProperties {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(MemberProperties.class);

    private Map<String, MemberDO> memberMap = new HashMap<>();

    private TreeMap<String, MemberDO> svnMemberMap = new TreeMap<>();

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

        try {
            is = new FileInputStream(userDir + "/svn-member.properties");
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
                svnMemberMap.put(member.getEmployeeId(), member);
            }
            logger.info("load member properties finished - " + this);
        } catch (IOException e) {
            logger.error("load member properties error", e);
            System.exit(1);
        }
    }


    public void merge(String path) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, MemberDO> entry : svnMemberMap.entrySet()) {
            String emp = entry.getKey();
            MemberDO memberDO = entry.getValue();
            MemberDO srcM = memberMap.get(emp);
            String names = memberDO.getSrc();
            if (srcM != null && StringUtils.isNotBlank(srcM.getEmail())) {
                names = srcM.getName() + "<" + srcM.getEmail() + ">";
            }
            stringBuilder.append(emp);
            stringBuilder.append("=");
            stringBuilder.append(names);
            stringBuilder.append("\n");
        }
        File file = new File(path);
        try {
            IOUtils.write(stringBuilder.toString(), new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
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

    public static void main(String[] args) {
        String path = "a.txt";
        MemberProperties memberProperties = new MemberProperties();
        memberProperties.merge(path);
    }
}
